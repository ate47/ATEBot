package fr.atesab.bot.command;

import java.util.List;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;

public class InformationCommand extends Command {
	public String getName() {
		return "ci";
	}
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message) {
		String s = "";
		if(event.getGuild()!=null){
			s+="\nRôles: {";
			List<IRole> r = event.getAuthor().getRolesForGuild(event.getGuild());
			for (int i = 0; i < r.size(); i++) {
				if(i>0)s+=",";
				IRole d = r.get(i);
				s+="[id:"+d.getStringID()+", name:"+d.getName()+"]";
			}
			s+="}";
		}
		event.getChannel().sendMessage("Client Information: \nAuteur: "+event.getAuthor()+s+"\nId: "+event.getAuthor().getStringID());
		return true;
	}
	public String neededPermission() {
		return "info";
	}
	public String getUsage() {
		return getName()+"";
	}

}
