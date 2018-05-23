package fr.atesab.bot.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import fr.atesab.bot.math.Evaluator;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class SayCommand extends Command {
	private static List<TextOption> options = new ArrayList<>();

	public SayCommand() {
		options.add(new TextOption("author", event -> event.getAuthor().getName(), false));
		options.add(new TextOption("authorDis",
				event -> event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), false));
		options.add(new TextOption("uid", event -> String.valueOf(event.getAuthor().getLongID()), false));
		options.add(new TextOption("mid", event -> String.valueOf(event.getMessage().getLongID()), false));
		options.add(new TextOption("gid", event -> String.valueOf(event.getGuild().getLongID()), false));
		options.add(new TextOption("authorMention", event -> event.getAuthor().mention(), false));
		options.add(new TextOption("randomMention", event -> RandomCommand.getRandomUser(event.getGuild()).mention(),
				true));
		options.add(new TextOption("randomGlobalMention",
				event -> RandomCommand.getRandomGlobalUser(event.getClient()).mention(), false));
		options.add(new TextOption("randomUser",
				event -> RandomCommand.getRandomUser(event.getGuild()).getDisplayName(event.getGuild()), true));
		options.add(new TextOption("randomUserDis", event -> {
			IUser user = RandomCommand.getRandomUser(event.getGuild());
			return user.getName() + "#" + user.getDiscriminator();
		}, true));
		options.add(new TextOption("randomGlobalUser",
				event -> RandomCommand.getRandomGlobalUser(event.getClient()).getName(), false));
		options.add(new TextOption("randomGlobalUserDis", event -> {
			IUser user = RandomCommand.getRandomGlobalUser(event.getClient());
			return user.getName() + "#" + user.getDiscriminator();
		}, false));
		options.add(new TextOption("randomChannelMention",
				event -> RandomCommand.getRandomUserHere(event.getChannel()).mention(), true));
		options.add(new TextOption("randomChannelUser",
				event -> RandomCommand.getRandomUserHere(event.getChannel()).getName(), true));
		options.add(new TextOption("randomChannelUserDis", event -> {
			IUser user = RandomCommand.getRandomUserHere(event.getChannel());
			return user.getName() + "#" + user.getDiscriminator();
		}, true));
		options.add(
				new TextOption("leader", event -> event.getGuild().getOwner().getDisplayName(event.getGuild()), true));
		options.add(new TextOption("leaderMention", event -> event.getGuild().getOwner().mention(), true));
		options.add(new TextOption("leaderDis", event -> {
			IUser user = event.getGuild().getOwner();
			return user.getName() + "#" + user.getDiscriminator();
		}, true));
		options.sort(TextOption::compareTo);
	}

	public static class TextOption {
		private Function<MessageReceivedEvent, String> data;
		private boolean guildType;
		private String name;

		private TextOption(String name, Function<MessageReceivedEvent, String> data, boolean guildType) {
			this.name = name;
			this.data = data;
			this.guildType = guildType;
		}

		public String getName() {
			return name;
		}

		public Function<MessageReceivedEvent, String> getData() {
			return data;
		}

		public boolean isGuildType() {
			return guildType;
		}
		public int compareTo(TextOption option) {
			return name.compareToIgnoreCase(option.name);
		}
	}

	public static final String OPTION_CHAR = "&";

	public static String getOptionnedText(MessageReceivedEvent event, String message) {
		long it = 0;
		for (TextOption opt : options)
			if ((opt.isGuildType() && event.getGuild() != null) || !opt.isGuildType())
				while (message.contains(OPTION_CHAR + opt.getName()) && it < 1000) {
					message = message.replaceFirst("[" + OPTION_CHAR + "]" + opt.getName(), opt.getData().apply(event));
					it++;
				}
		return message;
	}

	public static String parsedMessage(MessageReceivedEvent event, String[] args) throws IllegalArgumentException {
		String s = "";
		List<String> aArgs;
		try {
			aArgs = getAdvancedArgument(args);
		} catch (IllegalArgumentException e) {
			throw e;
		}
		Map<String, String> params = new HashMap<String, String>();
		int n = aArgs.size();
		for (int i = 0; i < n; i++) {
			if (aArgs.get(i).equalsIgnoreCase("-let")) {
				if (i + 2 < n) {
					if (i + 3 < n && aArgs.get(i + 2).equalsIgnoreCase("random")) {
						String s2 = aArgs.get(i + 3);
						List<String> opt = getAdvancedArgument(
								(s2.startsWith("(") && s2.endsWith(")") ? s2.substring(1, s2.length() - 2) : s2)
										.split(" "));
						params.put(aArgs.get(i + 1), getOptionnedText(event, opt.get(random.nextInt(opt.size()))));
						i += 1;
					} else if (i + 3 < n && aArgs.get(i + 2).equalsIgnoreCase("role")) {
						String s2 = aArgs.get(i + 3);
						List<String> opt = getAdvancedArgument(
								(s2.startsWith("(") && s2.endsWith(")") ? s2.substring(1, s2.length() - 2) : s2)
										.split(" "));
						List<IRole> roles = new ArrayList<IRole>();
						for (String id : opt) {
							try {
								IRole r = event.getGuild()
										.getRoleByID(Long.valueOf(id.replaceAll("[<][@][&]([0-9]+){1}[>]", "$1")));
								if (!roles.contains(r))
									roles.add(r);
							} catch (Exception e) {
							}
						}
						List<IUser> users = event.getGuild().getUsers();
						Iterator<IUser> it = users.iterator();
						user: while (it.hasNext()) {
							IUser u = it.next();
							for (IRole r : roles)
								if (u.hasRole(r))
									continue user;
							it.remove();
						}
						params.put(aArgs.get(i + 1),
								users.size() > 0 ? users.get(random.nextInt(users.size())).mention() : "");
						i += 1;
					} else if (i + 3 < n && aArgs.get(i + 2).equalsIgnoreCase("eval")) {
						params.put(aArgs.get(i + 1), String.valueOf(new Evaluator().evaluate(aArgs.get(i + 3))));
						i += 1;
					} else if (aArgs.get(i + 2).matches("[0-9]+")) {
						int r;
						try {
							r = random.nextInt(Integer.valueOf(aArgs.get(i + 2)));
						} catch (Exception e) {
							r = random.nextInt();
						}
						params.put(aArgs.get(i + 1), String.valueOf(r));
					} else
						params.put(aArgs.get(i + 1), getOptionnedText(event, aArgs.get(i + 2)));
					i += 2;
				} else {
					throw new IllegalArgumentException("Not enought arguments at i=" + i + ".");
				}
			} else if (aArgs.get(i).equalsIgnoreCase("-define")) {
				if (i + 2 < n) {
					if (aArgs.get(i + 2).equalsIgnoreCase("random") && i + 3 < n) {
						String s2 = aArgs.get(i + 3);
						List<String> opt = getAdvancedArgument(
								(s2.length() > 1 ? s2.substring(1, s2.length() - 2) : s2).split(" "));
						params.put(aArgs.get(i + 1), opt.get(random.nextInt(opt.size())));
						i += 1;
					} else
						params.put(aArgs.get(i + 1), aArgs.get(i + 2));
					i += 2;
				} else {
					throw new IllegalArgumentException("Not enought arguments at i=" + i + ".");
				}
			} else if (aArgs.get(i).equalsIgnoreCase("-role")) {
				if (i + 1 < n && event.getGuild() != null) {
					String s2 = aArgs.get(i + 1);
					List<String> opt = getAdvancedArgument(
							(s2.startsWith("(") && s2.endsWith(")") ? s2.substring(1, s2.length() - 2) : s2)
									.split(" "));
					List<IRole> roles = new ArrayList<IRole>();
					for (String id : opt) {
						try {
							IRole r = event.getGuild()
									.getRoleByID(Long.valueOf(id.replaceAll("[<][@][&]([0-9]+){1}[>]", "$1")));
							if (!roles.contains(r))
								roles.add(r);
						} catch (Exception e) {
						}
					}
					List<IUser> users = event.getGuild().getUsers();
					Iterator<IUser> it = users.iterator();
					user: while (it.hasNext()) {
						IUser u = it.next();
						for (IRole r : roles)
							if (u.hasRole(r))
								continue user;
						it.remove();
					}
					s += (i > 0 ? " " : "")
							+ (users.size() > 0 ? users.get(random.nextInt(users.size())).mention() : "");
					i += 1;
				} else {
					throw new IllegalArgumentException("Not enought arguments at i=" + i + ".");
				}
			} else
				s += (i > 0 ? " " : "") + aArgs.get(i);
		}
		for (String key : params.keySet())
			s = s.replaceAll(key, params.get(key));
		return getOptionnedText(event, s);
	}

	public static void sendMessage(IChannel channel, MessageReceivedEvent event, String[] args,
			BotInstance botInstance) {
		botInstance.sendMessage(channel, parsedMessage(event, args), " ");
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	public String getName() {
		return "say";
	}

	public String getUsage() {
		return getName() + " ((msg|ch <id>) <text>|opt)";
	}

	public String neededPermission() {
		return "msg";
	}

	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		message = event.getMessage().getContent();
		if (args.length > 0) {
			if (args.length == 1 && args[0].equalsIgnoreCase("opt")) {
				botInstance.sendMessage(event.getChannel(), options.stream()
						.map(opt -> "\n**" + OPTION_CHAR + opt.getName() + "**: "
								+ botInstance.getServer().getLanguage("cmd.say.opt." + opt.getName()))
						.collect(Collectors.joining("", botInstance.getServer().getLanguage("cmd.say.opt") + ": ",
								"\n**-let \"name\" \"text\"**: " + botInstance.getServer().getLanguage("cmd.say.opt.let")
										+ "\n**-let \"name\" eval \"exp\"**: "
										+ botInstance.getServer().getLanguage("cmd.say.opt.let.eval")
										+ "\n**-let \"name\" (random (\"val1\" \"val2\" \"val3\"))|n**: "
										+ botInstance.getServer().getLanguage("cmd.say.opt.let.random")
										+ "\n**-role (id|tag)**: "
										+ botInstance.getServer().getLanguage("cmd.say.opt.role"))));
			} else if (args[0].equalsIgnoreCase("msg") || args[0].equalsIgnoreCase("ch")) {
				if (botInstance.getServer().userHasPerm(event.getAuthor(), event.getGuild(), "manageclients",
						botInstance)) {
					String[] sargs = new String[args.length - 2];
					System.arraycopy(args, 2, sargs, 0, sargs.length);
					if (args[0].equalsIgnoreCase("msg")) {
						try {
							sendMessage(
									botInstance.getClient().getUserByID(Long.valueOf(args[1])).getOrCreatePMChannel(),
									event, sargs, botInstance);
						} catch (Exception e) {
						}
					} else if (args[0].equalsIgnoreCase("ch")) {
						try {
							sendMessage(botInstance.getClient().getChannelByID(Long.valueOf(args[1])), event, sargs,
									botInstance);
						} catch (Exception e) {
						}
					}
				} else
					event.getChannel().sendMessage(botInstance.getServer().getLanguage("noperm"));
			} else
				sendMessage(event.getChannel(), event, args, botInstance);
			return true;
		} else
			return false;
	}

}
