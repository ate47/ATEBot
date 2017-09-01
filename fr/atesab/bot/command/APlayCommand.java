package fr.atesab.bot.command;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import fr.atesab.bot.DListener;
import fr.atesab.bot.Main;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class APlayCommand extends Command {
	public String lastPlay=null;
	@Override
	public String getName() {
		return "ap";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message) {
		if(args.length==1) {
			if(args[0].equals("stop")) {
				DListener.audioPlayer.skip();
			} else if(args[0].equals("restart")) {
				DListener.audioPlayer.skip();
				File f = new File("musics/"+lastPlay);
				try {
					DListener.audioPlayer.queue(f);
					lastPlay=args[0];
				} catch (IOException | UnsupportedAudioFileException e) {
					e.printStackTrace();
				}
			} else {
				File f = new File("musics/"+args[0]);
				if(f.exists()) {
					try {
						DListener.audioPlayer.queue(f);
						lastPlay=args[0];
						event.getChannel().sendMessage(Main.lang.getLangage("cmd.ap.play", args[0]));
					} catch (IOException | UnsupportedAudioFileException e) {
						e.printStackTrace();
					}
				} else {
					event.getChannel().sendMessage(Main.lang.getLangage("cmd.ap.nofile"));
				}
			}
		} else return false;
		return true;
	}

	@Override
	public String neededPermission() {
		return "audio";
	}
	@Override
	public String getUsage() {
		return getName()+" <file|stop|restart>";
	}

}
