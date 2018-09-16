package fr.atesab.bot.command;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.BotServer;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

public abstract class Command {
	public static final Random random = new Random();
	public static final Color DISCORD_DARK_COLOR = new Color(54, 57, 62);

	public static String buildString(String[] array, int start) {
		String s = "";
		for (int i = start; i < array.length; i++) {
			if (i > start)
				s += " ";
			s += array[i];
		}
		return s;
	}

	public static String getGameList(BotInstance botInstance) {
		return botInstance.getServer().getGames().stream()
				.map(g -> g.getName() + ": " + botInstance.getServer().getLanguage("game." + g.getName()))
				.collect(Collectors.joining("\n"));
	}

	public static String getTime(long time) {
		return time < 1000 ? time + "ms"
				: ((time < 10000) ? BotServer.significantNumbers((time / 1000D), 3) + "s"
						: ((time < 60000) ? (time / 1000) + "s"
								: ((time < 600000) ? BotServer.significantNumbers(((time / 1000) / 60D), 3) + "min"
										: ((time < 36000000)
												? BotServer.significantNumbers(((time / 60000) / 60D), 3) + "min"
												: (time / 3600000) + "h"))));
	}

	public static String getAvatarURL(IUser user) {
		return user.getAvatarURL().toLowerCase().endsWith("null.png")
				? "https://discordapp.com/assets/0e291f67c9274a1abdddeb3fd919cbaa.png"
				: user.getAvatarURL().replaceAll("[.]webp", ".png");
	}

	public static BufferedImage getAvatarImage(IUser user) throws MalformedURLException, IOException {
		if (user == null)
			return null;
		URL url = new URL(getAvatarURL(user));
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("User-Agent", BotServer.BOT_NAME + "/" + BotServer.BOT_VERSION);
		connection.connect();
		return ImageIO.read(connection.getInputStream());
	}

	public static void drawCenteredString(String str, int posX, int posY, Graphics2D graphics) {
		int len = graphics.getFontMetrics().stringWidth(str);
		graphics.drawString(str, posX - len / 2, posY);
	}

	public static InputStream streamFromImage(BufferedImage bufferedImage) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", outputStream);
		return new ByteArrayInputStream(outputStream.toByteArray());
	}

	public static String[] unformatMention(String[] args) {
		return unformatMention(args, false);
	}

	public static String[] unformatMention(String[] args, boolean clone) {
		if (clone)
			args = args.clone();
		for (int i = 0; i < args.length; i++)
			args[i] = args[i].replaceAll("<[@!#&]+([0-9]+)>", "$1");
		return args;
	}

	public static List<String> getAdvancedArgument(String[] args) throws IllegalArgumentException {
		List<String> elm = new ArrayList<String>();
		String arg = null;
		String larg = null;
		for (String s : args) {
			if (larg != null) {
				if (s.endsWith(")")) {
					elm.add(larg + " " + s);
					larg = null;
				} else
					larg += " " + s;
			} else {
				if (arg != null) {
					if (s.endsWith("\"")) {
						elm.add(arg + " " + s.substring(0, s.length() - 1));
						arg = null;
					} else
						arg += " " + s;
				} else if (s.startsWith("\"")) {
					if (s.endsWith("\""))
						elm.add(s.substring(1, s.length() - 1));
					else
						arg = s.substring(1);
				} else if (s.startsWith("(")) {
					if (s.endsWith(")"))
						elm.add(s);
					else
						larg = s;
				} else
					elm.add(s);
			}
		}
		if (arg != null)
			throw new IllegalArgumentException("Syntax error: '\"' expected");
		else if (larg != null)
			throw new IllegalArgumentException("Syntax error: ')' expected");
		return elm;
	}

	public List<String> getAliases() {
		return null;
	}

	public abstract String getName();

	public String getUsage() {
		return getName();
	}

	public abstract String neededPermission();

	public abstract boolean runCommand(MessageReceivedEvent event, String[] args, String message,
			ServerConfig serverConfig, BotInstance botInstance);
}
