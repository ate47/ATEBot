package fr.atesab.bot.game;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import fr.atesab.bot.BotServer;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public abstract class GameInstance<T extends Game> {
	@Override
	public String toString() {
		return "[" + game + "]: " + Arrays.stream(users).map(u -> u.getName() + "#" + u.getDiscriminator())
				.collect(Collectors.joining(", ")) + ".";
	}

	protected static final Random random = new Random();
	/**
	 * The game of this instance
	 */
	protected T game;
	/**
	 * Players of this instance
	 */
	protected IUser[] users;
	protected boolean ended = false;
	protected BotServer server;

	public GameInstance(BotServer server, T game, IUser[] users) {
		this.server = server;
		this.game = game;
		this.users = users;
	}

	/**
	 * true if this user is a player of this instance
	 */
	public boolean containUser(IUser user) {
		return containUser(user.getLongID());
	}

	/**
	 * true a player of this instance have this id
	 */
	public boolean containUser(long userId) {
		for (IUser u : users)
			if (u.getLongID() == userId)
				return true;
		return false;
	}

	/**
	 * Evaluate the game :
	 * <p>
	 * -1 : mat
	 * </p>
	 * <p>
	 * 0 : no win/mat Player number : win of this
	 * </p>
	 * <p>
	 * player -2 : win but without message
	 * </p>
	 */
	public abstract int evaluateGame(IChannel channel, IUser player, String[] args);

	public T getGame() {
		return game;
	}

	public IUser[] getUsers() {
		return users;
	}

	public boolean isEnded() {
		return ended;
	}

	public final void setEnded(IChannel channel) {
		setEnded(channel, -1);
	}

	public final void setEnded(IChannel channel, int winner) {
		ended = true;
		try {
			end(channel, winner);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Call when the instance start
	 */
	public void init(IChannel channel) {
	}

	/**
	 * Call when the instance stop
	 * <p>
	 * <code>winner >=0 && winner < users.length</code>
	 * </p>
	 * is a index in {@link #users}
	 * <p>
	 * <code>winner == -2</code> is a mat
	 * </p>
	 */
	public void end(IChannel channel, int winner) {
	}
}
