package fr.atesab.bot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import fr.atesab.bot.game.Game;
import fr.atesab.bot.game.GameInstance;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class PartyCommand extends Command {

	class Party {
		IUser leader;
		List<IUser> users = new ArrayList<>();
		List<IUser> invites = new ArrayList<>();

		Party(IUser leader) {
			this.leader = leader;
		}

		boolean containUser(IUser user) {
			return leader.equals(user) || users.contains(user);
		}

		void setLeader(IUser user) {
			users.add(leader);
			leader = user;
			users.remove(user);
		}

		boolean removeUser(IUser user) {
			return !user.equals(leader) && users.remove(user);
		}

		boolean addUser(IUser user) {
			return !user.equals(leader) && !users.contains(user) && users.add(user);
		}

		public int count() {
			return 1 + users.size();
		}

		boolean startGame(Game game, BotInstance instance, IChannel channel) {
			for (IUser user : users) {
				GameInstance<?> gi = instance.getGameInstanceByPlayer(user);
				if (gi != null)
					instance.endGame(gi, channel);
			}
			List<IUser> players = new ArrayList<>(users);
			players.add(leader);
			return instance.startGame(game, channel, players);
		}
	}

	abstract class PartySubCommand extends Command {
		private String name;
		private String permission;
		private boolean needParty;
		private List<String> aliases;

		public PartySubCommand(String name, String permission, boolean needParty, String... aliases) {
			this.name = name;
			this.permission = permission;
			this.aliases = Arrays.asList(aliases);
			this.needParty = needParty;
		}

		public PartySubCommand(String name, boolean needParty) {
			this(name, null, needParty);
		}

		@Override
		public String neededPermission() {
			return permission;
		}

		public boolean needParty() {
			return needParty;
		}

		@Override
		public List<String> getAliases() {
			return aliases;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public final boolean runCommand(MessageReceivedEvent event, String[] args, String message,
				ServerConfig serverConfig, BotInstance botInstance) {
			return true;
		}

		public abstract boolean runCommand(MessageReceivedEvent event, String[] args, String message,
				ServerConfig serverConfig, BotInstance botInstance, Party party);
	}

	private Map<BotInstance, List<Party>> parties = new HashMap<>();
	private List<PartySubCommand> subCommands = new ArrayList<>();

	public PartyCommand() {
		subCommands.add(new PartySubCommand("create", false) {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] args, String message,
					ServerConfig serverConfig, BotInstance botInstance, Party party) {
				if (party == null) {
					createParty(botInstance, event.getAuthor());
					event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.party.create.create"));
				} else
					event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.party.create.already"));
				return true;
			}
		});
		subCommands.add(new PartySubCommand("delete", true) {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] args, String message,
					ServerConfig serverConfig, BotInstance botInstance, Party party) {
				if (party.leader.equals(event.getAuthor())) {
					removeParty(botInstance, party);
					event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.party.delete.delete"));
				} else
					event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.party.notLeader"));
				return true;
			}
		});
		subCommands.add(new PartySubCommand("list", true) {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] args, String message,
					ServerConfig serverConfig, BotInstance botInstance, Party party) {
				BotInstance.sendMessage(event.getChannel(),
						party.users.stream().map(u -> "\n- " + u.getName() + "#" + u.getDiscriminator())
								.collect(Collectors.joining("",
										botInstance.getServer().getLanguage("cmd.party.leader") + ": "
												+ party.leader.getName() + "#" + party.leader.getDiscriminator() + "\n"
												+ botInstance.getServer().getLanguage("cmd.party.members") + ":",
										"")));
				return true;
			}
		});
		subCommands.add(new PartySubCommand("invite", true) {
			@Override
			public String getUsage() {
				return super.getUsage() + " <@tag>";
			}

			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] args, String message,
					ServerConfig serverConfig, BotInstance botInstance, Party party) {
				if (party.leader.equals(event.getAuthor())) {
					if (args.length != 1)
						return false;
					try {
						IUser user = event.getGuild().getUserByID(Long.valueOf(unformatMention(args)[0]));
						if (!party.containUser(user)) {
							if (!party.invites.remove(user)) {
								party.invites.add(user);
								event.getChannel().sendMessage(
										botInstance.getServer().getLanguage("cmd.party.invite.invite", user.mention()));
							} else
								event.getChannel().sendMessage(botInstance.getServer()
										.getLanguage("cmd.party.invite.invite.delete", user.mention()));
						} else
							event.getChannel()
									.sendMessage(botInstance.getServer().getLanguage("cmd.party.invite.already"));
					} catch (Exception e) {
						event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.tree.bind.NaU"));
					}
				} else
					event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.party.notLeader"));
				return true;
			}
		});
		subCommands.add(new PartySubCommand("kick", true) {
			@Override
			public String getUsage() {
				return super.getUsage() + " <@tag>";
			}

			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] args, String message,
					ServerConfig serverConfig, BotInstance botInstance, Party party) {
				if (party.leader.equals(event.getAuthor())) {
					if (args.length != 1)
						return false;
					try {
						IUser user = event.getGuild().getUserByID(Long.valueOf(unformatMention(args)[0]));
						if (party.users.remove(user)) {
							event.getChannel().sendMessage(
									botInstance.getServer().getLanguage("cmd.party.kick.kick", user.mention()));
						} else if (party.leader.equals(user))
							event.getChannel()
									.sendMessage(botInstance.getServer().getLanguage("cmd.party.kick.leader"));
						else
							event.getChannel().sendMessage(
									botInstance.getServer().getLanguage("cmd.party.kick.notInParty", user.mention()));
					} catch (Exception e) {
						event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.tree.bind.NaU"));
					}
				} else
					event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.party.notLeader"));
				return true;
			}
		});
		subCommands.add(new PartySubCommand("join", false) {
			@Override
			public String getUsage() {
				return super.getUsage() + " <@tag>";
			}

			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] args, String message,
					ServerConfig serverConfig, BotInstance botInstance, Party party) {
				if (args.length != 1)
					return false;
				else if (party == null) {
					try {
						IUser user = event.getGuild().getUserByID(Long.valueOf(unformatMention(args)[0]));
						Party newParty = getPartyByPlayer(botInstance, user);
						if (newParty != null) {
							if (newParty.invites.remove(event.getAuthor())) {
								newParty.users.add(event.getAuthor());
								event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.party.join",
										newParty.leader.mention()));
							} else
								event.getChannel()
										.sendMessage(botInstance.getServer().getLanguage("cmd.party.invite.close"));
						} else
							event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.party.invite.NaP"));
					} catch (Exception e) {
						event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.tree.bind.NaU"));
					}
				} else
					event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.create.already"));
				return true;
			}
		});
		subCommands.add(new PartySubCommand("lead", true) {
			@Override
			public String getUsage() {
				return super.getUsage() + " <@tag>";
			}

			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] args, String message,
					ServerConfig serverConfig, BotInstance botInstance, Party party) {
				if (party.leader.equals(event.getAuthor())) {
					try {
						IUser user = event.getGuild().getUserByID(Long.valueOf(unformatMention(args)[0]));
						if (party.users.remove(user)) {
							party.leader = user;
							party.users.add(event.getAuthor());
							event.getChannel().sendMessage(
									botInstance.getServer().getLanguage("cmd.party.lead.lead", user.mention()));
						} else if (party.leader.equals(user))
							event.getChannel()
									.sendMessage(botInstance.getServer().getLanguage("cmd.party.lead.already"));
						else
							event.getChannel()
									.sendMessage(botInstance.getServer().getLanguage("cmd.party.lead.notInParty"));
					} catch (Exception e) {
						event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.tree.bind.NaU"));
					}
				} else
					event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.party.notLeader"));
				return true;
			}
		});
		subCommands.add(new PartySubCommand("gamelist", null, false, "glist") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] args, String message,
					ServerConfig serverConfig, BotInstance botInstance, Party party) {
				if (args.length != 0)
					return false;
				BotInstance.sendMessage(event.getChannel(), getGameList(botInstance));
				return true;
			}
		});
		subCommands.add(new PartySubCommand("leave", true) {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] args, String message,
					ServerConfig serverConfig, BotInstance botInstance, Party party) {
				if (args.length != 0)
					return false;
				if (party.users.remove(event.getAuthor())) {
					event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.party.leave"));
				} else
					event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.party.leave.leader"));
				return true;
			}
		});
	}

	private Party createParty(BotInstance instance, IUser leader) {
		Party party = new Party(leader);
		parties.putIfAbsent(instance, new ArrayList<>());
		parties.get(instance).add(party);
		return party;
	}

	private boolean removeParty(BotInstance instance, Party party) {
		List<Party> parties = this.parties.getOrDefault(instance, new ArrayList<>());
		return parties.remove(party);
	}

	private Party getPartyByPlayer(BotInstance instance, IUser user) {
		List<Party> parties = this.parties.getOrDefault(instance, new ArrayList<>());
		for (Party party : parties)
			if (party.containUser(user))
				return party;
		return null;
	}

	public PartySubCommand getSubCommandByName(String name) {
		for (PartySubCommand sub : subCommands) {
			if (sub.getName().equalsIgnoreCase(name))
				return sub;
			for (String alias : sub.aliases)
				if (alias.equalsIgnoreCase(name))
					return sub;
		}
		return null;
	}

	@Override
	public String getName() {
		return "party";
	}

	@Override
	public String neededPermission() {
		return "party";
	}

	@Override
	public String getUsage() {
		return super.getUsage() + " <" + subCommands.stream().map(Command::getName).sorted(String::compareToIgnoreCase)
				.collect(Collectors.joining("|")) + ">";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		if (args.length == 0 || event.getGuild() == null)
			return false;
		Party party = getPartyByPlayer(botInstance, event.getAuthor());
		PartySubCommand sub = getSubCommandByName(args[0]);
		if (sub != null) {
			if ((sub.needParty && party != null) || !sub.needParty) {
				String[] sargs = new String[args.length - 1];
				System.arraycopy(args, 1, sargs, 0, sargs.length);
				if (botInstance.getServer().userHasPerm(event.getAuthor(), event.getGuild(), sub.permission,
						botInstance)) {
					if (!sub.runCommand(event, sargs, message, serverConfig, botInstance, party))
						event.getChannel().sendMessage(serverConfig.commandPrefix + getName() + " " + sub.getUsage());
				} else
					event.getChannel().sendMessage(botInstance.getServer().getLanguage("noperm"));
			} else
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.party.delete.noParty"));
		} else {
			Game game = botInstance.getServer().getGameByName(args[0]);
			if (game != null) {
				if ((party != null && party.leader.equals(event.getAuthor()))
						|| (party == null && game.neededPlayer() == 1)) {
					GameInstance<?> gameInstance = botInstance.getGameInstanceByPlayer(event.getAuthor());
					if (gameInstance == null) {
						if (party != null)
							party.startGame(game, botInstance, event.getChannel());
						else
							botInstance.getGameInstances()
									.add(game.getInstance(botInstance.getServer(), event.getAuthor()));
					} else
						event.getChannel().sendMessage(botInstance.getServer().getLanguage("game.inagame"));
				} else
					event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.party.notLeader"));
			} else
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("game.nag"));
		}
		return true;
	}
}
