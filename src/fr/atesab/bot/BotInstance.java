package fr.atesab.bot;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.atesab.bot.DiscordListener.Answer;
import fr.atesab.bot.game.Game;
import fr.atesab.bot.game.GameInstance;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class BotInstance {
	private static final long TIME_BETWEEN_SEND = 500L;
	private IDiscordClient client;
	private BotConfig config;
	private DiscordListener listener;
	private List<GameInstance<?>> gameInstances = new ArrayList<>();
	private BotServer server;

	public BotInstance(BotServer server, BotConfig config) {
		this.server = server;
		this.config = config;
	}

	public BotInstance(BotServer server, String name, String token) {
		this(server, new BotConfig(name, token));
	}

	public IDiscordClient getClient() {
		return client;
	}

	public BotConfig getConfig() {
		return config;
	}

	public void endGame(GameInstance<?> gameInstance, IChannel channel) {
		endGame(gameInstance, channel, -1);
	}

	public void endGame(GameInstance<?> gameInstance, IChannel channel, int winner) {
		if (gameInstance == null)
			return;
		gameInstance.setEnded(channel, winner);
		gameInstances.remove(gameInstance);
	}

	@SuppressWarnings("unchecked")
	public <G extends Game> List<GameInstance<G>> getGameInstanceByGame(G game) {
		List<GameInstance<G>> list = new ArrayList<>();
		gameInstances.stream().filter(gi -> game.getClass().isAssignableFrom(gi.getGame().getClass()) && !gi.isEnded())
				.forEach(gi -> list.add((GameInstance<G>) gi));
		return list;
	}

	public GameInstance<? extends Game> getGameInstanceByPlayer(IUser player) {
		return getGameInstanceByPlayer(player.getLongID());
	}

	public void startGame(GameInstance<?> instance, IChannel channel) {
		getGameInstances().add(instance);
		instance.init(channel);
	}

	public GameInstance<? extends Game> getGameInstanceByPlayer(long id) {
		for (Iterator<GameInstance<?>> iterator = gameInstances.iterator(); iterator.hasNext();) {
			GameInstance<?> gameInstance = iterator.next();
			if (gameInstance.isEnded())
				iterator.remove();
			else if (gameInstance.containUser(id))
				return gameInstance;
		}
		return null;
	}

	public List<GameInstance<? extends Game>> getGameInstances() {
		return gameInstances;
	}

	public DiscordListener getListener() {
		return listener;
	}

	public BotServer getServer() {
		return server;
	}

	public ServerConfig getServerConfigById(long id) {
		if (client.getGuildByID(id) != null) {
			config.getConfig().putIfAbsent(String.valueOf(id), new ServerConfig(server));
			return config.getConfig().get(String.valueOf(id));
		}
		return null;
	}

	public void sendMessage(IChannel channel, String msg) {
		sendMessage(channel, msg, null, "\n");
	}

	public void sendMessage(IChannel channel, String msg, String separator) {
		sendMessage(channel, msg, null, separator);
	}

	public void sendMessage(IChannel channel, String msg, InputStream stream) {
		sendMessage(channel, msg, stream, "\n");
	}

	private void sendBlock(IChannel channel, String msg, InputStream stream) {
		if (stream != null)
			channel.sendFile(msg, stream, "unknow");
		else
			channel.sendMessage(msg);
	}

	public void sendMessage(IChannel channel, String msg, InputStream stream, String separator) {
		if (msg.length() < 1 && stream == null)
			return;
		if (msg.length() < 2000) {
			sendBlock(channel, msg, stream);
			return;
		}
		if (separator == null || separator.isEmpty())
			throw new IllegalArgumentException("The separator can't be null or empty");
		new Thread(() -> {
			StringBuffer buffer = new StringBuffer();
			String[] array = msg.split(separator);
			int send = 0;
			for (int i = 0; i < array.length; i++) {
				String next;
				if (array[i].length() > 1999) {
					next = array[i].substring(2000);
					array[i] = array[i].substring(2000, array[i].length());
					i--;
				} else
					next = array[i];
				next = separator + next;
				if (next.length() + buffer.length() < 2000) {
					buffer.append(next);
				} else {
					channel.sendMessage(buffer.toString());
					send++;
					if (send > 2)
						try {
							Thread.sleep(TIME_BETWEEN_SEND);
						} catch (InterruptedException e) {
						}
					buffer.setLength(0);
					buffer.append(next);
				}
			}
			if (buffer.length() != 0 || stream != null)
				sendBlock(channel, buffer.toString(), stream);
		}, "MessageSender").start();
	}

	public void sendAnswers(MessageReceivedEvent event, Answer... answers) {
		new Thread(() -> {
			for (int i = 0; i < answers.length; i++) {
				answers[i].answer(event);
				if (i > 1)
					try {
						Thread.sleep(TIME_BETWEEN_SEND);
					} catch (InterruptedException e) {
					}
			}
		}, "MessageSender").start();
	}

	public BotInstance setClient(IDiscordClient client) {
		this.client = client;
		return this;
	}

	public void setListener(DiscordListener listener) {
		this.listener = listener;
	}
}
