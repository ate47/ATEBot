package fr.atesab.bot.command;

import java.util.stream.Collectors;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.DiscordListener;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class HelpCommand extends Command {

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String neededPermission() {
		return null;
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		if (args.length != 0)
			return false;
		BotInstance
				.sendMessage(event.getChannel(),
						botInstance
								.getServer().getCommands().stream().filter(
										cmd -> botInstance.getServer().userHasPerm(event.getAuthor(), event.getGuild(),
												cmd.neededPermission(), botInstance))
								.map(cmd -> "\n**"
										+ (event.getGuild() != null
												? botInstance
														.getServerConfigById(event.getGuild().getLongID()).commandPrefix
												: DiscordListener.DEFAULT_COMMAND_PREFIX)
										+ cmd.getUsage() + "**: "
										+ botInstance.getServer().getLanguage("cmd." + cmd.getName()))
								.collect(Collectors.joining("",
										botInstance.getServer().getLanguage("cmd.help.list") + ":", "")));
		return true;
	}

}
