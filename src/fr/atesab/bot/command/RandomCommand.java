package fr.atesab.bot.command;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class RandomCommand extends Command {
	public static IUser getRandomGlobalUser(IDiscordClient client) {
		List<IUser> users = new ArrayList<IUser>();
		for (IGuild guild: client.getGuilds())
			users.addAll(guild.getUsers());
		return users.get(random.nextInt(users.size()));
	}
	public static IUser getRandomUser(IGuild g) {
		if(g==null)return null;
		List<IUser> users = g.getUsers();
		return users.get(random.nextInt(users.size()));
	}
	public static IUser getRandomUserHere(IChannel c) {
		if(c==null)return null;
		List<IUser> users = c.getUsersHere();
		return users.get(random.nextInt(users.size()));
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public String getName() {
		return "random";
	}
	@Override
	public String getUsage() {
		return getName()+" <users|n|(pos (pos)*)>";
	}
	@Override
	public String neededPermission() {
		return "msg";
	}
	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		if(args.length==0) return false;
		else if(args[0].equalsIgnoreCase("users")) {
			if(event.getGuild()!=null) {
				IUser r = (args.length==2 && args[1].equalsIgnoreCase("global"))?getRandomGlobalUser(event.getClient()):getRandomUser(event.getGuild());
				event.getChannel().sendMessage(((args.length>1 && args[1].equalsIgnoreCase("false"))
						?r.getNicknameForGuild(event.getGuild()):r.mention()));
			} else event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.random.noGuild"));
		} else if(args[0].equalsIgnoreCase("pos")) {
			if(args.length>1) {
				String[] sargs = new String[args.length-1];
				System.arraycopy(args, 1, sargs, 0, sargs.length);
				List<String> a = getAdvancedArgument(sargs);
				event.getChannel().sendMessage(SayCommand.getOptionnedText(event, a.get(random.nextInt(a.size()))));
			} else return false;
		} else if(args[0].matches("[1-9][0-9]*")) {
			try {
				event.getChannel().sendMessage(String.valueOf(random.nextInt(Integer.valueOf(args[0]))));
			} catch (Exception e) {
				throw new IllegalArgumentException(botInstance.getServer().getLanguage("cmd.random.nan")+" : "+args[0]);
			}
		} else return false;
		return true;
	}
}
