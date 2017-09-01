package fr.atesab.bot.command;

import java.util.ArrayList;
import java.util.Random;

import fr.atesab.bot.DListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class KickCommand extends Command {
	public static String[] randomWord = {"Penis","Bite","Cam Embert","Licorne","Uzi","GoodBye my friend, goodbye my lover","fanta",
			"nazi","communiste","kickoulol","ui","stiti","patocheLand"};
	public static String[] randomKickMessage = {"Goodbye %s","hey %s, I hate you.","++ dans le bus %s","vous avez vu %s ?","%s n'�tais pas tr�s fort",
			"%s a disparu !"};
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message) {
		if(args.length>0) {
			try{
				Random rnd = new Random();
				ArrayList<IUser> users = new ArrayList<IUser>();
				for(String s: args) {
					try{users.add(event.getGuild().getUserByID(Long.valueOf(s)));} catch (Exception e) {}
				}
				IVoiceChannel vc = event.getGuild().createVoiceChannel(randomWord[rnd.nextInt(randomWord.length)]);
				String outputname = "";
				for(IUser u : users) {
					try{u.moveToVoiceChannel(vc);
					outputname=u.mention()+" ";} catch (Exception e) {}
				}
				vc.delete();
				event.getChannel().sendMessage(randomKickMessage[rnd.nextInt(randomKickMessage.length)].replaceAll("%s", outputname)+" "+DListener.heart);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		} else return false;
	}
	public String neededPermission() {
		return "manageclients";
	}
	public String getName() {
		return "kick";
	}
	public String getUsage() {
		return getName()+" <userid...>";
	}
}
