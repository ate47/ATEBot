package fr.atesab.bot.command;

import java.util.List;
import java.util.Queue;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import fr.atesab.bot.utils.AudioProvider;
import fr.atesab.bot.utils.AudioProvider.ProviderType;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

public class APlayCommand extends Command {
	public static String getTrackMetadata(AudioTrack tracks) {
		String s = " - " + tracks.getInfo().author + "\n" + tracks.getInfo().uri;
		return s;
	}

	public static void play(IChannel channel, String song, AudioProvider audioPlayer, BotInstance botInstance) {
		Queue<AudioTrack> queue = audioPlayer.getQueue();
		botInstance.getListener().playerManager.loadItem(song, new AudioLoadResultHandler() {
			@Override
			public void loadFailed(FriendlyException exception) {
				if (channel != null)
					channel.sendMessage(botInstance.getServer().getLanguage("cmd.ap.loadfailed"));
			}

			@Override
			public void noMatches() {
				if (channel != null)
					channel.sendMessage(botInstance.getServer().getLanguage("cmd.ap.nomatch"));
			}

			public void playlistLoaded(AudioPlaylist playlist) {
				if (audioPlayer.getAudioPlayer().getPlayingTrack() == null && queue.size() == 0
						&& playlist.getTracks().size() > 0)
					audioPlayer.getAudioPlayer().playTrack(playlist.getTracks().remove(0));
				queue.addAll(playlist.getTracks());
				if (channel != null)
					channel.sendMessage(botInstance.getServer().getLanguage("cmd.ap.playlist"));
			}

			@Override
			public void trackLoaded(AudioTrack track) {
				if (audioPlayer.getAudioPlayer().getPlayingTrack() == null && queue.size() == 0)
					audioPlayer.getAudioPlayer().playTrack(track);
				else
					queue.add(track);
				if (channel != null)
					channel.sendMessage(botInstance.getServer().getLanguage("cmd.ap.loadtrack"));
			}
		});
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public String getName() {
		return "ap";
	}

	@Override
	public String getUsage() {
		return getName() + " <URL|file|queue|skip|vol|restart|clear|info|type>";
	}

	@Override
	public String neededPermission() {
		return "audio";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		long gid = event.getGuild().getLongID();
		if (botInstance.getListener().getAudioPlayers().containsKey(gid)) {
			AudioProvider audioPlayer = botInstance.getListener().getAudioPlayers().get(gid);
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("skip")) {
					audioPlayer.getAudioPlayer().stopTrack();
				} else if (args.length == 2 && args[0].equalsIgnoreCase("vol")) {
					try {
						audioPlayer.getAudioPlayer().setVolume(Integer.valueOf(args[1]));
					} catch (Exception e) {
						event.getChannel().sendMessage(botInstance.getServer().getLanguage("math.dc.nan"));
					}
				} else if (args[0].equalsIgnoreCase("type")) {
					String s = "";
					switch (args.length == 2 ? args[1].toLowerCase() : "") {
					case "none":
						audioPlayer.setType(serverConfig.providerType = ProviderType.NONE);
						break;
					case "loop":
						audioPlayer.setType(serverConfig.providerType = ProviderType.LOOP);
						break;
					default:
						s= botInstance.getServer().getLanguage("cmd.ap.type") + ":";
						for (ProviderType type : ProviderType.class.getEnumConstants()) {
							String n = type.name();
							s += "\n- " + n + ": " + botInstance.getServer().getLanguage("cmd.ap.type." + n);
						}
						break;
					}
					BotInstance.sendMessage(event.getChannel(), s+
							"\n"+botInstance.getServer().getLanguage("cmd.ap.type.current") + ": " + botInstance.getServer().getLanguage("cmd.ap.type." + audioPlayer.getType().name()));
				} else if (args[0].equalsIgnoreCase("restart")) {
					AudioTrack next = audioPlayer.getAudioPlayer().getPlayingTrack();
					if (next != null)
						audioPlayer.getAudioPlayer().playTrack(next);
				} else if (args[0].equalsIgnoreCase("queue")) {
					String s;
					if (audioPlayer.getQueue().size() == 0)
						s = botInstance.getServer().getLanguage("cmd.ap.queue.empty");
					else {
						s = botInstance.getServer().getLanguage("cmd.ap.queue") + ":";
						AudioTrack[] tracks = audioPlayer.getQueue()
								.toArray(new AudioTrack[audioPlayer.getQueue().size()]);
						for (int i = 0; i < tracks.length; i++) {
							s += (s.isEmpty() ? "" : "\n") + String.valueOf(i + 1) + getTrackMetadata(tracks[i]);
						}
					}
					BotInstance.sendMessage(event.getChannel(), s);
				} else if (args[0].equalsIgnoreCase("clear")) {
					audioPlayer.getQueue().clear();
					audioPlayer.getAudioPlayer().stopTrack();
				} else if (args[0].equalsIgnoreCase("info")) {
					AudioTrack current = audioPlayer.getAudioPlayer().getPlayingTrack();
					if (current != null) {
						event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.ap.play", current.getInfo().title,
								current.getInfo().author));
					} else
						event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.ap.play.none"));
				} else
					play(event.getChannel(), buildString(args, 0), audioPlayer, botInstance);
			} else
				return false;
		} else
			event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.ap.noconnect"));
		return true;
	}
}
