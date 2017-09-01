package fr.atesab.bot.command;

import fr.atesab.bot.Main;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class SayCommand extends Command {
	public String getName() {
		return "say";
	}
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message) {
		if(args.length>0) {
			int a = 1;
			if(args[0].equals("msg") || args[0].equals("ch")) {
				a = 3;
			}
			for (int i = a; i < args.length; i++) {
				args[a-1]+=" "+args[i];
			}
			if(args[0].equals("msg")) {
				try{
					IUser u = Main.client.getUserByID(Long.valueOf(args[1]));
					u.getOrCreatePMChannel().sendMessage(args[a-1]);
				}catch (Exception e) {}
			} else if(args[0].equals("ch")) {
				try{
					IChannel c = Main.client.getChannelByID(Long.valueOf(args[1]));
					c.sendMessage(args[a-1]);
				}catch (Exception e) {}
			} else {
				event.getChannel().sendMessage(args[a-1]);
			}
			return true;
		} else return false;
	}
	public String neededPermission() {
		return "manageclients";
	}
	public String getUsage() {
		return getName()+" (msg|ch <id>) <text>";
	}

}
