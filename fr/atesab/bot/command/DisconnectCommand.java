package fr.atesab.bot.command;

import fr.atesab.bot.DListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class DisconnectCommand extends Command {
	public String getName() {
		return "disconnect";
	}
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message) {
		if(DListener.voicechannel!=null)DListener.voicechannel.leave();
		return true;
	}
	public String neededPermission() {
		return "audio";
	}
	public String getUsage() {
		return getName();
	}

}
