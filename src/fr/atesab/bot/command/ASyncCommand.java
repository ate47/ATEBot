package fr.atesab.bot.command;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public abstract class ASyncCommand extends Command {

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		new Thread(() -> botInstance.getListener()
				.tryCommand(() -> runCommandASync(event, args, message, serverConfig, botInstance), event.getChannel()))
						.start();
		return true;
	}

	public abstract boolean runCommandASync(MessageReceivedEvent event, String[] args, String message,
			ServerConfig serverConfig, BotInstance botInstance);

}
