package fr.atesab.bot.command;

import java.util.List;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import fr.atesab.bot.utils.AudioProvider;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.IVoiceState;

public class ConnectCommand extends Command {
	public static AudioProvider connect(IVoiceChannel voiceChannel, BotInstance botInstance, ServerConfig serverConfig) {
		IGuild vcg = voiceChannel.getGuild();
		try {
			voiceChannel.leave();
		} catch (Exception e) {}
		voiceChannel.join();
		AudioProvider audioProvider = new AudioProvider(
				botInstance.getListener().playerManager.createPlayer(), botInstance.getListener(), vcg, serverConfig.providerType);
		audioProvider.getAudioPlayer().setVolume(serverConfig.vol);
		botInstance.getListener().getAudioPlayers().put(vcg.getLongID(), audioProvider);
		vcg.getAudioManager().setAudioProvider(audioProvider);
		return audioProvider;
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	public String getName() {
		return "connect";
	}

	public String getUsage() {
		return getName() + " [channelid]";
	}

	public String neededPermission() {
		return "audio";
	}
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		IGuild g = event.getGuild();
		if (g != null) {
			IVoiceChannel voiceChannel = null;
			if (args.length == 1) {
				try {
					voiceChannel = event.getClient().getVoiceChannelByID(Long.valueOf(args[0]));
				} catch (Exception e) {

				}
			} else if (event.getAuthor().getVoiceStateForGuild(g) != null) {
				IVoiceState state = event.getAuthor().getVoiceStateForGuild(g);
				if (state != null) {
					voiceChannel = state.getChannel();
				}
			} else
				return false;
			if (voiceChannel != null) {
				connect(voiceChannel, botInstance, serverConfig);
			} else
				return false;
		}
		return true;
	}
}
