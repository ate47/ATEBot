package fr.atesab.bot.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Supplier;

import fr.atesab.bot.BotServer;
import fr.atesab.bot.command.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class TicTacToeGame implements Game {
	public class TicTacToeInstance extends TurnGameInstance<TicTacToeGame> {
		private int turn;
		private int[][] map;
		private boolean infoMode = false;
		private TicTacToeElementDrawer drawer;

		public TicTacToeInstance(BotServer server, TicTacToeGame game, IUser[] users) {
			super(server, game, users);
			this.turn = random.nextInt(this.users.length);
			this.drawer = drawerSupplier.get();
			map = new int[sizeX][sizeY];
			for (int i = 0; i < map.length; i++)
				for (int j = 0; j < map[i].length; j++)
					map[i][j] = 0;
			drawer.init(this);
		}

		protected final boolean containZero(int[][] map) {
			for (int[] sm : map)
				for (int i : sm)
					if (i == 0)
						return true;
			return false;
		}

		private void drawSquare(Graphics2D graphics, int offsetX, int offsetY, int width, int height, int player, int x,
				int y) {
			graphics.setColor(Color.WHITE);
			graphics.drawRect(offsetX, offsetY, width, height);
			drawer.draw(graphics, offsetX, offsetY, width, height, player, x, y);
			if (infoMode && player == 0) {
				graphics.setColor(Color.MAGENTA);
				Command.drawCenteredString((x + 1) + " " + (y + 1), offsetX + width / 2,
						offsetY + height / 2 + (graphics.getFontMetrics().getHeight() / 2), graphics);
			}
		}

		private void usage(IChannel channel) {
			channel.sendMessage("<1-" + sizeX + "> <1-" + sizeY + "> | info");
		}

		@Override
		public int evaluateGameTurn(IChannel channel, int player, String[] args) {
			if (args.length == 2) {
				try {
					int x = (Integer.parseInt(args[0]) - 1);
					int y = (Integer.parseInt(args[1]) - 1);
					if (x >= 0 && y >= 0 && x < sizeX && y < sizeY && (map[x][y]) == 0) {
						map[x][y] = player + 1;
						this.turn = (1 + this.turn) % getUsers().length;
						showGame(channel);
						return winGame();
					} else {
						channel.sendMessage(server.getLanguage("game.ttt.badplay"));
						return 0;
					}
				} catch (NumberFormatException e) {
					usage(channel);
					return 0;
				}

			} else if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
				channel.sendMessage(server.getLanguage("game.ttt.infomode") + ": "
						+ server.getLanguage((infoMode = !infoMode) ? "debug.enabled" : "debug.disabled"));
				showGame(channel);
				return 0;
			} else {
				usage(channel);
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
			int width = 12 + 200 * sizeX;
			int height = 12 + 200 * sizeY;
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = bufferedImage.createGraphics();
			graphics.setFont(new Font(graphics.getFont().getFontName(), Font.BOLD, 100));
			graphics.setStroke(new BasicStroke(4));
			for (int i = 0; i < map.length; i++)
				for (int j = 0; j < map[i].length; j++)
					drawSquare(graphics, 1 + i * 200, 1 + j * 200, 200, 200, map[i][j], i, j);
			try {
				channel.sendFile("", Command.streamFromImage(bufferedImage), "show.png");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public int winGame() {
			int i, j, k, n, m, w;
			n = sizeX - elementToWin + 1;
			m = sizeY - elementToWin + 1;
			for (i = 0; i < n; i++)
				for (j = 0; j < m; j++) {
					if ((w = map[i][j]) == 0)
						continue;
					// line
					winBlock: {
						for (k = 1; k < elementToWin; k++)
							if (map[i + k][j] != w)
								break winBlock;
						return w;
					}
					// vertical
					winBlock: {
						for (k = 1; k < elementToWin; k++)
							if (map[i][j + k] != w)
								break winBlock;
						return w;
					}
					// right diagonal
					winBlock: {
						for (k = 1; k < elementToWin; k++)
							if (map[i + k][j + k] != w)
								break winBlock;
						return w;
					}
				}
			for (i = elementToWin - 1; i < sizeX; i++)
				for (j = 0; j < m; j++) {
					if ((w = map[i][j]) == 0)
						continue;
					// left diagonal
					winBlock: {
						for (k = 1; k < elementToWin; k++)
							if (map[i - k][j + k] != w)
								break winBlock;
						return w;
					}
				}
			return containZero(map) ? 0 : -1;
		}

	}

	private static final Color[] DEFAULT_MODE_COLORS = { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN,
			Color.ORANGE, Color.MAGENTA, Color.PINK, Color.LIGHT_GRAY, Color.DARK_GRAY };
	public static final Supplier<TicTacToeElementDrawer> DEFAULT_MODE = () -> (Graphics2D graphics, int offsetX,
			int offsetY, int width, int height, int player, int x, int y) -> {
		int i = width / 8;
		int j = height / 8;
		int left = offsetX + i;
		int right = offsetX + width - i;
		int top = offsetY + j;
		int bottom = offsetY + height - j;
		graphics.setColor(DEFAULT_MODE_COLORS[(player - 1) % DEFAULT_MODE_COLORS.length]);
		switch (player) {
		case 1:
			graphics.drawLine(left, top, right, bottom);
			graphics.drawLine(right, top, left, bottom);
			break;
		case 2:
			graphics.drawOval(left, top, right - left, bottom - top);
			break;
		case 3:
			graphics.drawPolyline(new int[] { width / 2 + offsetX, left, right }, new int[] { top, bottom, bottom }, 3);
			break;
		case 4:
			graphics.drawOval(left, top, right - left, bottom - top);
			graphics.drawLine(left, top + height / 2, right, top + height / 2);
			graphics.drawLine(left + width / 2, top, left + width / 2, bottom);
			break;
		default:
			graphics.drawOval(left, top, right - left, bottom - top);
			Command.drawCenteredString(player + "", offsetX + width / 2,
					offsetY + height / 2 + (graphics.getFontMetrics().getHeight() / 2), graphics);
			break;
		}
	};
	public static final Supplier<TicTacToeElementDrawer> ICON_MODE = () -> new TicTacToeElementDrawer() {

		private BufferedImage[] playerIcons;

		@Override
		public void draw(Graphics2D graphics, int offsetX, int offsetY, int width, int height, int player, int x,
				int y) {
			int i = width / 8;
			int j = height / 8;
			int left = offsetX + i;
			int right = offsetX + width - i;
			int top = offsetY + j;
			int bottom = offsetY + height - j;
			if (player != 0)
				graphics.drawImage(playerIcons[player - 1], left, top, right, bottom, 0, 0,
						playerIcons[player - 1].getWidth(), playerIcons[player - 1].getHeight(), null);
		}

		@Override
		public void init(TicTacToeInstance is) {
			playerIcons = new BufferedImage[is.users.length];
			for (int i = 0; i < is.users.length; i++) {
				try {
					playerIcons[i] = Command.getAvatarImage(is.users[i]);
				} catch (Exception e) {
				}
			}
		}

	};
	private String name;
	private boolean iconMode;
	private int sizeX;
	private int sizeY;
	private int elementToWin;
	private int neededPlayer;
	private int maxPlayer;
	private Supplier<TicTacToeElementDrawer> drawerSupplier;

	public TicTacToeGame(String name, Supplier<TicTacToeElementDrawer> drawerSupplier, int sizeX, int sizeY,
			int elementToWin, int neededPlayer, int maxPlayer) {
		this.name = name;
		this.drawerSupplier = drawerSupplier;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.elementToWin = elementToWin;
		this.neededPlayer = neededPlayer;
		this.maxPlayer = maxPlayer;
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
		return neededPlayer;
	}

	@Override
	public int maxPlayer() {
		return maxPlayer;
	}

	@FunctionalInterface
	public static interface TicTacToeElementDrawer {
		public void draw(Graphics2D graphics, int offsetX, int offsetY, int width, int height, int player, int x,
				int y);

		default void init(TicTacToeInstance is) {
		}
	}
}
