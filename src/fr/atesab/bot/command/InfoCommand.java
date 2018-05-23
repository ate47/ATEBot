package fr.atesab.bot.command;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.BotServer;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class InfoCommand extends Command {
	private List<String> aliases;
	public InfoCommand() {
		aliases = new ArrayList<String>();
		aliases.add("bi");
	}
	@Override
	public List<String> getAliases() {
		return aliases;
	}
	public String getName() {
		return "botinformation";
	}
	public String getUsage() {
		return getName();
	}
	public String neededPermission() {
		return null;
	}
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		event.getChannel().sendMessage(botInstance.getServer().getEmbedObject(event.getClient().getOurUser(), botInstance.getServer().getLanguage("botinformation"), 
				botInstance.getServer().getLanguage("cmd.bi.msg", BotServer.BOT_NAME + " v" + BotServer.BOT_VERSION, BotServer.BOT_AUTHOR)));
		return true;
	}

}
