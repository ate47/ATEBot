package fr.atesab.bot.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import fr.atesab.bot.BotServer;
import fr.atesab.bot.command.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class MarienbadGame implements Game {
	public class MarienbadGameInstance extends TurnGameInstance<MarienbadGame> {
		private int[] map = { 1, 3, 5, 7 };
		private int turn = 0;

		public MarienbadGameInstance(BotServer server, MarienbadGame game, IUser[] users) {
			super(server, game, users);
			this.turn = random.nextInt(this.users.length);
		}

		@Override
		public void init(IChannel channel) {
			showGame(channel);
			channel.sendMessage(server.getLanguage("game.badturn", users[getTurn()].mention()));
			super.init(channel);
		}

		private void drawMatches(Graphics2D graphics, int offsetX, int offsetY, int width, int height) {
			int l = height * 10 / 28;
			graphics.setColor(new Color(237, 206, 102));
			int k = width * 1 / 3;
			graphics.fillRect(offsetX + k / 2, offsetY + l, k * 2, height - l);
			graphics.setColor(Color.RED);
			graphics.fillOval(offsetX, offsetY + 2, width, l);
		}

		protected void showGame(IChannel channel) {
			int width = 310;
			int height = 600;
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = bufferedImage.createGraphics();
			graphics.setStroke(new BasicStroke(5.0F));
			for (int i = 0; i < map.length; i++) {
				graphics.setColor(Color.WHITE);
				graphics.drawLine(40, 40 + (i + 1) * 125, width - 40, 40 + (i + 1) * 125);
				for (int j = 0; j < map[i]; j++)
					drawMatches(graphics, 50 + j * 30, 50 + i * 125, 21, 100);
			}
			try {
				channel.sendFile("", Command.streamFromImage(bufferedImage), "show.png");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		protected boolean win() {
			for (int i = 0; i < map.length; i++) {
				if (map[i] != 0)
					return false;
			}
			return true;
		}

		@Override
		public int evaluateGameTurn(IChannel channel, int player, String[] args) {
			if (args.length != 2 || !args[0].matches("[1-4]") || !args[1].matches("[1-7]")) {
				channel.sendMessage("<1..4> <1...7>");
				return 0;
			}
			int i = Integer.valueOf(args[0]) - 1;
			int j = Integer.valueOf(args[1]);
			if (map[i] >= j) {
				map[i] -= j;
				if (win())
					return (player + 1) % this.users.length + 1;
				else {
					showGame(channel);
					turn = (turn + 1) % this.users.length;
				}
			} else if (map[i] == 0)
				channel.sendMessage(server.getLanguage("game.marienbad.badline"));
			else
				channel.sendMessage(i + " <1..." + map[i] + ">");
			return 0;
		}

		@Override
		public int getTurn() {
			return turn;
		}

	}

	@Override
	public GameInstance<?> getInstance(BotServer server, IUser... users) {
		return new MarienbadGameInstance(server, this, users);
	}

	@Override
	public String getName() {
		return "marienbad";
	}

	@Override
	public int neededPlayer() {
		return 2;
	}

}
