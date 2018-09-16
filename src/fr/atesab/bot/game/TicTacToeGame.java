package fr.atesab.bot.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import fr.atesab.bot.BotServer;
import fr.atesab.bot.command.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class TicTacToeGame implements Game {
	public static class TicTacToeInstance extends TurnGameInstance<TicTacToeGame> {
		private int turn;
		private int[] map;
		private boolean infoMode = false;
		private BufferedImage[] playerIcons;

		public TicTacToeInstance(BotServer server, TicTacToeGame game, IUser[] users) {
			super(server, game, users);
			this.turn = random.nextInt(this.users.length);
			map = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			if (game.isIconMode()) {
				playerIcons = new BufferedImage[this.users.length];
				for (int i = 0; i < this.users.length; i++) {
					try {
						playerIcons[i] = Command.getAvatarImage(this.users[i]);
					} catch (Exception e) {
					}
				}
			}
		}

		protected final boolean containZero(int[] map) {
			for (int i : map)
				if (i == 0)
					return true;
			return false;
		}

		private void drawSquare(Graphics2D graphics, int offsetX, int offsetY, int width, int height, int player,
				int id) {
			graphics.setColor(Color.WHITE);
			graphics.drawRect(offsetX, offsetY, width, height);
			int i = width / 8;
			int j = height / 8;
			int left = offsetX + i;
			int right = offsetX + width - i;
			int top = offsetY + j;
			int bottom = offsetY + height - j;
			if (!game.isIconMode()) {
				if (player == 1) {
					graphics.setColor(Color.RED);
					graphics.drawLine(left, top, right, bottom);
					graphics.drawLine(right, top, left, bottom);
				} else if (player == 2) {
					graphics.setColor(Color.BLUE);
					graphics.drawOval(left, top, right - left, bottom - top);
				} else if (player == 3) {
					graphics.setColor(Color.GREEN);
					graphics.drawPolyline(new int[] { width / 2 + offsetX, left, right },
							new int[] { top, bottom, bottom }, 3);
				}
			} else if (player != 0)
				graphics.drawImage(playerIcons[player - 1], left, top, right, bottom, 0, 0,
						playerIcons[player - 1].getWidth(), playerIcons[player - 1].getHeight(), null);
			if (infoMode && player == 0) {
				graphics.setColor(Color.MAGENTA);
				Command.drawCenteredString((id % 3 + 1) + " " + (id / 3 + 1), offsetX + width / 2,
						offsetY + height / 2 + (graphics.getFontMetrics().getHeight() / 2), graphics);
			}
		}

		@Override
		public int evaluateGameTurn(IChannel channel, int player, String[] args) {
			if (args.length == 2 && args[0].matches("[123]") && args[1].matches("[123]")) {
				int index = (Integer.valueOf(args[0]) - 1) + 3 * (Integer.valueOf(args[1]) - 1);
				if (map[index] == 0) {
					map[index] = player + 1;
					this.turn = (1 + this.turn) % getUsers().length;
					showGame(channel);
					return winGame();
				} else {
					channel.sendMessage(server.getLanguage("game.ttt.badplay"));
					return 0;
				}
			} else if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
				channel.sendMessage(server.getLanguage("game.ttt.infomode") + ": "
						+ server.getLanguage((infoMode = !infoMode) ? "debug.enabled" : "debug.disabled"));
				showGame(channel);
				return 0;
			} else {
				channel.sendMessage("<1-2-3> <1-2-3> | info");
				return 0;
			}
		}

		@Override
		public int getTurn() {
			return this.turn % getUsers().length;
		}

		@Override
		public void init(IChannel channel) {
			showGame(channel);
			channel.sendMessage(server.getLanguage("game.badturn", users[getTurn()].mention()));
			super.init(channel);
		}

		protected void showGame(IChannel channel) {
			int width = 612;
			int height = 612;
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = bufferedImage.createGraphics();
			graphics.setFont(new Font(graphics.getFont().getFontName(), Font.BOLD, 100));
			graphics.setStroke(new BasicStroke(4));
			for (int i = 0; i < map.length; i++)
				drawSquare(graphics, 1 + (i % 3) * 200, 1 + (i / 3) * 200, 200, 200, map[i], i);
			try {
				channel.sendFile("", Command.streamFromImage(bufferedImage), "show.png");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public int winGame() {
			if (map[0] == map[1] && map[1] == map[2] && map[0] != 0)
				return map[0];
			if (map[3] == map[4] && map[4] == map[5] && map[3] != 0)
				return map[3];
			if (map[6] == map[7] && map[7] == map[8] && map[6] != 0)
				return map[6];
			if (map[0] == map[3] && map[3] == map[6] && map[0] != 0)
				return map[0];
			if (map[1] == map[4] && map[4] == map[7] && map[1] != 0)
				return map[1];
			if (map[2] == map[5] && map[5] == map[8] && map[2] != 0)
				return map[2];
			if (map[0] == map[4] && map[4] == map[8] && map[0] != 0)
				return map[4];
			if (map[2] == map[4] && map[4] == map[6] && map[2] != 0)
				return map[4];
			return containZero(map) ? 0 : -1;
		}

	}

	private String name;

	private boolean iconMode;

	public TicTacToeGame(boolean iconMode) {
		this.name = (this.iconMode = iconMode) ? "ttti" : "ttt";
	}

	@Override
	public GameInstance<TicTacToeGame> getInstance(BotServer server, IUser... users) {
		return new TicTacToeInstance(server, this, users);
	}

	@Override
	public String getName() {
		return name;
	}

	public boolean isIconMode() {
		return iconMode;
	}

	@Override
	public int neededPlayer() {
		return 2;
	}
}
