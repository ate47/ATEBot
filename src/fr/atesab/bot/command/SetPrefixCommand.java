package fr.atesab.bot.command;

import java.util.List;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class SetPrefixCommand extends Command {
	@Override
	public List<String> getAliases() {
		return null;
	}
	@Override
	public String getName() {
		return "setprefix";
	}
	@Override
	public String getUsage() {
		return getName()+" <prefix>";
	}

	@Override
	public String neededPermission() {
		return "configserver";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		if(args.length==1) {
			event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.setprefix.msg", (serverConfig.commandPrefix=args[0])));
			botInstance.getServer().saveConfig();
		}
		else return false;
		return true;
	}

}
