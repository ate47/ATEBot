package fr.atesab.bot.command;

import fr.atesab.bot.Main;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class TitleCommand extends Command {
	public String getName() {
		return "title";
	}
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message) {
		if(args.length>0) {
			for (int i = 1; i < args.length; i++) {
				args[0]+=" "+args[i];
			}
			Main.client.changePlayingText(args[0]);
			Main.playinformation = args[0];
			return true;
		} else return false;
	}
	public String neededPermission() {
		return "bot";
	}
	public String getUsage() {
		return getName()+" <title>";
	}

}
