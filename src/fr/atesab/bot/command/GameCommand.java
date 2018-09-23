package fr.atesab.bot.command;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
		return getName() + " <stop|game|list> [players...]";
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
				botInstance.endGame(gi, event.getChannel());
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("game.stopgame"));
			} else
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("game.inagame"));
		} else {
			if (gi != null)
				botInstance.endGame(gi, event.getChannel());
			if (args[0].equalsIgnoreCase("list")) {
				botInstance.sendMessage(event.getChannel(), getGameList(botInstance));
			} else if (args[0].equalsIgnoreCase("instances")) {
				List<GameInstance<?>> instances = botInstance.getGameInstances();
				botInstance.sendMessage(event.getChannel(),
						botInstance.getServer().getLanguage("cmd.game.instances") + ":\n"
								+ (instances.isEmpty() ? botInstance.getServer().getLanguage("cmd.game.instances.empty")
										: instances.stream().map(GameInstance::toString)
												.collect(Collectors.joining("\n-", "- ", ""))));
			} else {
				Game g = botInstance.getServer().getGameByName(args[0]);
				if (g != null) {
					if (args[1].equalsIgnoreCase("instances")) {
						List<GameInstance<Game>> instances = botInstance.getGameInstanceByGame(g);
						botInstance.sendMessage(event.getChannel(), botInstance.getServer()
								.getLanguage("cmd.game.instances")
								+ ":\n"
								+ (instances.isEmpty() ? botInstance.getServer().getLanguage("cmd.game.instances.empty")
										: instances.stream().map(GameInstance::toString)
												.collect(Collectors.joining("\n-", "- ", ""))));
					} else {
						List<IUser> mentions = new ArrayList<>(event.getMessage().getMentions());
						if (mentions.size() < g.neededPlayer())
							mentions.add(event.getAuthor()); // add the author if there isn't enough players
						botInstance.startGame(g, event.getChannel(), mentions);
					}
				} else
					botInstance.sendMessage(event.getChannel(), botInstance.getServer().getLanguage("game.nag"));
			}
		}
		return true;
	}

}
