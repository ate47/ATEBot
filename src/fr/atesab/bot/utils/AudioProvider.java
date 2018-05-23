package fr.atesab.bot.utils;

import sx.blah.discord.handle.audio.IAudioProvider;
import sx.blah.discord.handle.obj.IGuild;

import java.util.Queue;

import org.eclipse.jetty.util.BlockingArrayQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import fr.atesab.bot.DiscordListener;
import sx.blah.discord.handle.audio.AudioEncodingType;

public class AudioProvider implements IAudioProvider {
	public static enum ProviderType {
		LOOP,
		NONE;
	}
	private final AudioPlayer audioPlayer;

	private final IGuild guild;
	private ProviderType type;

	private Queue<AudioTrack> queue;

	private AudioFrame lastFrame;

	public AudioProvider(AudioPlayer audioPlayer, DiscordListener listener, IGuild guild, ProviderType type) {
		this.audioPlayer = audioPlayer;
		this.queue = new BlockingArrayQueue<>();
		this.guild = guild;
		this.type = type;
		AudioProvider This = this;
		audioPlayer.addListener(new AudioEventAdapter() {
			@Override
			public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
				listener.onTrackEnd(guild, This, track, endReason);
				super.onTrackEnd(player, track, endReason);
			}
		});
	}

	@Override
	public AudioEncodingType getAudioEncodingType() {
		return AudioEncodingType.OPUS;
	}

	public AudioPlayer getAudioPlayer() {
		return audioPlayer;
	}

	@Override
	public int getChannels() {
		return 2;
	}

	public IGuild getGuild() {
		return guild;
	}

	public Queue<AudioTrack> getQueue() {
		return queue;
	}

	public ProviderType getType() {
		return type;
	}

	@Override
	public boolean isReady() {
		if (lastFrame == null) {
			lastFrame = audioPlayer.provide();
		}

		return lastFrame != null;
	}

	@Override
	public byte[] provide() {
		if (lastFrame == null) {
			lastFrame = audioPlayer.provide();
		}

		byte[] data = lastFrame != null ? lastFrame.data : null;
		lastFrame = null;

		return data;
	}
	
	public void setType(ProviderType type) {
		this.type = type;
	}
}