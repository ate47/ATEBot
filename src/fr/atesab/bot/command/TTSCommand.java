package fr.atesab.bot.command;

import java.util.List;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class TTSCommand extends Command {
	@Override
	public List<String> getAliases() {
		return null;
	}
	public String getName() {
		return "tts";
	}
	public String getUsage() {
		return getName()+" <text>";
	}
	public String neededPermission() {
		return "audio";
	}
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig, BotInstance botInstance) {
		if(args.length<1)return false;
		for (int i = 3; i < args.length; i++) {
			args[2]+=" "+args[i];
		}
		
		return true;
	}

}
