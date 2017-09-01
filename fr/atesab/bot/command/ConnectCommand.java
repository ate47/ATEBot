package fr.atesab.bot.command;

import fr.atesab.bot.DListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.handle.obj.IGuild;

public class ConnectCommand extends Command {
	public String getName() {
		return "connect";
	}
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message) {
		IGuild g = event.getGuild();
		if(g!=null) {
			if(args.length==1) {
				try{
					DListener.voicechannel = event.getClient().getVoiceChannelByID(Long.valueOf(args[0]));
					DListener.voicechannel.join();
					DListener.audioPlayer = AudioPlayer.getAudioPlayerForGuild(g);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else return false;
		}
		return true;
	}
	public String neededPermission() {
		return "audio";
	}

	public String getUsage() {
		return getName()+" <channelid>";
	}
}
