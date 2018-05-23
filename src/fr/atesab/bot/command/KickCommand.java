package fr.atesab.bot.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class KickCommand extends Command {
	@Override
	public List<String> getAliases() {
		return null;
	}
	public String getName() {
		return "kick";
	}
	public String getUsage() {
		return getName()+" <userid...>";
	}
	public String neededPermission() {
		return "manageclients";
	}
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig, BotInstance botInstance) {
		if(args.length>0) {
			try{
				Random rnd = new Random();
				List<IUser> users = new ArrayList<IUser>();
				users.addAll(event.getMessage().getMentions());
				for(String s: args) {
					try{
						if(s.equalsIgnoreCase("all")) {
							users.addAll(event.getGuild().getUsers());
							break;
						}
						IUser u = event.getGuild().getUserByID(Long.valueOf(s));
						if(!users.contains(u)) users.add(u);
					} catch (Exception e) {}
				}
				IVoiceChannel vc = event.getGuild().createVoiceChannel(UUID.randomUUID().toString());
				String outputname = "";
				for(IUser u : users) {
					try{u.moveToVoiceChannel(vc);
					outputname=(outputname.isEmpty()?"":", ")+u.mention();} catch (Exception e) {}
				}
				vc.delete();
				event.getChannel().sendMessage(outputname.isEmpty()?
						botInstance.getServer().getLanguage("cmd.kick.nokick")
						:serverConfig.kickChannelMessage.get(rnd.nextInt(serverConfig.kickChannelMessage.size())).replaceAll("%s", outputname));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		} else return false;
	}
}
