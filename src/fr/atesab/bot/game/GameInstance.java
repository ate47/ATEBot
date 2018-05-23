package fr.atesab.bot.game;

import fr.atesab.bot.BotServer;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public abstract class GameInstance<T extends Game> {
	protected T game;
	protected IUser[] users;
	protected boolean ended = false;
	protected BotServer server;
	public GameInstance(BotServer server, T game, IUser[] users){
		this.server = server;
		this.game = game;
		this.users = users;
	}
	public boolean containUser(IUser user) {
		return containUser(user.getLongID());
	}
	public boolean containUser(long userId) {
		for (IUser u: users)
			if(u.getLongID()==userId) return true;
		return false;
	}
	public abstract int evaluateGame(IChannel channel, int player, String[] args);
	public T getGame() {
		return game;
	}
	public abstract int getTurn();
	public IUser[] getUsers() {
		return users;
	}
	public IUser getUserTurn() {
		return users[getTurn()];
	}
	public boolean isEnded() {
		return ended;
	}
	public void setEnded() {
		ended = true;
	}
}
