package fr.atesab.bot.game;

import fr.atesab.bot.BotServer;
import sx.blah.discord.handle.obj.IUser;

public interface Game {
	public GameInstance<?> getInstance(BotServer server, IUser... users);

	public String getName();

	public int neededPlayer();

	public default int maxPlayer() {
		return neededPlayer();
	};
}
