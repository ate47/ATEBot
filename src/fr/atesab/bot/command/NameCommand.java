package fr.atesab.bot.command;

import java.util.List;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class NameCommand extends Command {
	@Override
	public List<String> getAliases() {
		return null;
	}
	public String getName() {
		return "name";
	}
	public String getUsage() {
		return getName()+" (name)";
	}
	public String neededPermission() {
		return "bot";
	}
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig, BotInstance botInstance) {
		if(args.length==1) {
			botInstance.getClient().changeUsername(args[0]);
			return true;
		}else return false;
	}

}
