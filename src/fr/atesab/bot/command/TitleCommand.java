package fr.atesab.bot.command;

import java.util.List;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

public class TitleCommand extends Command {
	@Override
	public List<String> getAliases() {
		return null;
	}
	public String getName() {
		return "title";
	}
	public String getUsage() {
		return getName()+" (streaming|playing|listening|watch|watching) <clear|title>";
	}
	public String neededPermission() {
		return "bot";
	}
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig, BotInstance botInstance) {
		if(args.length>0) {
			ActivityType type = ActivityType.PLAYING;
			int i = 0;
			switch (args[0].toLowerCase()) {
			case "playing":
			case "streaming":
				i = 1;
				type = ActivityType.STREAMING;
				break;
			case "listening":
				i = 1;
				type = ActivityType.LISTENING;
				break;
			case "watching":
				i = 1;
				type = ActivityType.WATCHING;
				break;
			default:
				break;
			}
			String s = args[0].equalsIgnoreCase("clear")?"":buildString(args, i);
			event.getClient().changePresence(StatusType.ONLINE, type, s);
			botInstance.getConfig().setPlayinformation(s);
			botInstance.getConfig().setActivityType(type);
			botInstance.getServer().saveConfig();
			return true;
		} else return false;
	}

}
