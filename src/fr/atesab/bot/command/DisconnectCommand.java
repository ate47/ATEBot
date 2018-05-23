package fr.atesab.bot.command;

import java.util.List;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class DisconnectCommand extends Command {
	@Override
	public List<String> getAliases() {
		return null;
	}
	public String getName() {
		return "disconnect";
	}
	public String getUsage() {
		return getName();
	}
	public String neededPermission() {
		return "audio";
	}
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig, BotInstance botInstance) {
		long id = event.getGuild().getLongID();
		for (IVoiceChannel voiceChannel: event.getClient().getConnectedVoiceChannels()) {
			if(voiceChannel.getGuild().getLongID() == id) {
				voiceChannel.leave();
				break;
			}
		}
		if(botInstance.getListener().getAudioPlayers().containsKey(id)) {
			botInstance.getListener().getAudioPlayers().get(id).getAudioPlayer().destroy();
			botInstance.getListener().getAudioPlayers().remove(id);
		}
		return true;
	}

}
