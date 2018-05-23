package fr.atesab.bot;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import fr.atesab.bot.game.GameInstance;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class BotInstance {
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

	public GameInstance<?> getGameInstanceByPlayer(IUser player) {
		return getGameInstanceByPlayer(player.getLongID());
	}

	public GameInstance<?> getGameInstanceByPlayer(long id) {
		for (GameInstance<?> gameInstance : gameInstances)
			if (gameInstance.isEnded() && gameInstance.containUser(id))
				return gameInstance;
		return null;
	}

	public List<GameInstance<?>> getGameInstances() {
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
		if (msg.length() < 2000)
			sendBlock(channel, msg, stream);
		if (separator == null || separator.isEmpty())
			throw new IllegalArgumentException("The seperator can't be null or empty");
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
				if (next.length() + buffer.length() < 2000) {
					buffer.append(next);
				} else {
					if (send > 3)
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) { }
					channel.sendMessage(buffer.toString());
					send++;
					buffer.setLength(0);
					buffer.append(next);
				}
			}
			if (buffer.length() != 0 || stream != null)
				sendBlock(channel, buffer.toString(), stream);
		}, "MessageSender");
	}

	public BotInstance setClient(IDiscordClient client) {
		this.client = client;
		return this;
	}

	public void setListener(DiscordListener listener) {
		this.listener = listener;
	}
}
