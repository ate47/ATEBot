package fr.atesab.bot.command;

import java.util.List;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import fr.atesab.bot.game.Game;
import fr.atesab.bot.game.GameInstance;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

public class GameCommand extends Command {

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public String getName() {
		return "game";
	}

	@Override
	public String getUsage() {
		return getName() + " <stop|game> [players...]";
	}

	@Override
	public String neededPermission() {
		return "game";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		if (args.length == 0)
			return false;
		GameInstance<?> gi = botInstance.getGameInstanceByPlayer(event.getAuthor());
		if (gi != null && gi.isEnded()) {
			if (args[0].equalsIgnoreCase("stop")) {
				botInstance.getGameInstances().remove(gi);
				gi.setEnded();
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("game.stopgame"));
			} else
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("game.inagame"));
		} else {
			Game g = botInstance.getServer().getGameByName(args[0]);
			if (g != null) {
				List<IUser> mentions = event.getMessage().getMentions();
				if (mentions.size() == g.neededPlayer()) {
					botInstance.getGameInstances()
							.add(g.getInstance(botInstance.getServer(), mentions.toArray(new IUser[mentions.size()])));
				} else
					event.getChannel()
							.sendMessage(botInstance.getServer().getLanguage("game.badnumber", g.neededPlayer()));
			} else
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("game.nag"));
		}
		return true;
	}

}
