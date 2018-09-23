package fr.atesab.bot.game;

import fr.atesab.bot.BotServer;
import sx.blah.discord.handle.obj.IUser;

/**
 * A basic game
 */
public interface Game {
	/**
	 * Build an instance of this game with users (Between {@link #neededPlayer()}
	 * and {@link #maxPlayer()} or higher than {@link #neededPlayer()} if
	 * {@link #maxPlayer()} is negative)
	 */
	public GameInstance<?> getInstance(BotServer server, IUser... users);

	/**
	 * Game name
	 */
	public String getName();

	/**
	 * What is minimum player to play
	 */
	public int neededPlayer();

	/**
	 * What is maximum player to play (negative to remove player limit), default to
	 * {@link #neededPlayer()}
	 */
	public default int maxPlayer() {
		return neededPlayer();
	};
}
