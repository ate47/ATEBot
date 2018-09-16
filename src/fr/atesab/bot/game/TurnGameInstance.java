package fr.atesab.bot.game;

import fr.atesab.bot.BotServer;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public abstract class TurnGameInstance<T extends Game> extends GameInstance<T> {

	public TurnGameInstance(BotServer server, T game, IUser[] users) {
		super(server, game, users);
	}

	public int evaluateGame(IChannel channel, IUser player, String[] args) {
		int turn = getTurn();
		IUser p = getUsers()[turn];
		if (p.getLongID() == player.getLongID()) {
			return evaluateGameTurn(channel, turn, args);
		} else {
			channel.sendMessage(server.getLanguage("game.badturn", p.mention()));
			return 0;
		}
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
	public abstract int evaluateGameTurn(IChannel channel, int player, String[] args);

	public abstract int getTurn();

	public IUser getUserTurn() {
		return users[getTurn()];
	}

}
