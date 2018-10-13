package fr.atesab.bot.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.BotServer;
import fr.atesab.bot.command.Command;
import fr.atesab.bot.utils.BotUtils;
import fr.atesab.bot.utils.Square;
import fr.atesab.bot.utils.TriConsumer;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

public class BattleshipGame implements Game {
	private static final String HELP_COMMAND = "?";
	private static final String INIT_END_COMMAND = "confirm";
	private static final Color COLOR_WATER = new Color(0xff008ddc);
	private static final Color COLOR_SHIP_MAIN = new Color(0xff7f7f7f);
	private static final Color COLOR_SHIP_SECOND = new Color(0xff474747);
	private static final Color COLOR_SHOOT_WATER = new Color(0xffdddddd);
	private static final Color COLOR_SHOOT_SHIP = new Color(0xffee0000);
	private static final int MAP_HEIGHT = 412;

	public class BattleshipGameInstance extends GameInstance<BattleshipGame> {
		private IChannel mainChannel;
		private int turn;
		private boolean gameStarted = false;
		private Map<IUser, PlayerData> playerDatas;

		public BattleshipGameInstance(BotServer server, BattleshipGame game, IUser[] users) {
			super(server, game, users);
			playerDatas = BotUtils.createHashMap(users, PlayerData::new);
		}

		@Override
		public void end(IChannel channel, int winner) {
			showGame(mainChannel, null,
					(winner >= 0 && winner < users.length) ? server.getLanguage("game.win", users[winner]) : "", true);
			super.end(channel, winner);
		}

		private void sendHelp(IChannel channel) {
			String length = server.getLanguage("game.battleship.ship.length") + ": ";
			BotInstance.sendMessage(channel,
					server.getLanguage("game.battleship.help") + ":\n" + Arrays.stream(Ship.values()).map(
							s -> s.getName(server) + ": " + s.name().toLowerCase() + " (" + length + s.length + ")")
							.collect(
									Collectors.joining("\n", server.getLanguage("game.battleship.ships") + ":\n", "\n"))
							+ Arrays.stream(Direction.values())
									.map(d -> d.getName(server) + ": " + d.name().toLowerCase())
									.collect(Collectors.joining("\n",
											server.getLanguage("game.battleship.dirs") + ":\n", "\n"))
							+ server.getLanguage("game.battleship.help.command",
									Ship.CARRIER.name() + " G 6 " + Direction.UP.name(), Ship.CARRIER.getName(server),
									"G6", Direction.UP.getName(server)));
		}

		@Override
		public int evaluateGame(IChannel channel, IUser player, String[] args) {
			PlayerData playerData = playerDatas.get(player);
			if (!gameStarted) {
				if (args.length == 1 && args[0].equalsIgnoreCase(HELP_COMMAND))
					sendHelp(channel);
				else if (args.length == 1 && args[0].equalsIgnoreCase(INIT_END_COMMAND)) {
					playerData.setInitEnds(true);
					new Thread(() -> {
						int i = 0;
						for (IUser p : users) {
							try {
								if (i > 2)
									Thread.sleep(BotInstance.TIME_BETWEEN_SEND);
								showGame(p.getOrCreatePMChannel(), p, server.getLanguage("game.start"), true);
								i++;
							} catch (DiscordException e) {
								mainChannel.sendMessage(server.getLanguage("cantPM", player.mention()));
							} catch (InterruptedException e) {
								return;
							}
						}
					}, "BattleShipMessageSender");
				} else if (args.length == 4) {
					Ship ship = Ship.getByNameIgnoreCase(args[0]);
					if (ship != null) {
						Direction dir = Direction.getByNameIgnoreCase(args[3]);
						if (dir != null) {
							char[] cs = args[1].toLowerCase().toCharArray();
							Map<Ship, ShipData> shipData = playerData.shipData;
							if (cs.length == 1 && cs[0] >= 'a' && cs[0] <= 'j' && args[2].matches("[1-9]|(10)")
									&& playerData.placeShip(cs[0] - 'a', Integer.valueOf(args[2]), ship, dir)) {
								BotInstance.sendMessage(channel, server.getLanguage("game.battleship.init.placed"));
								if (shipData.size() == Ship.values().length) // confirm end
									BotInstance.sendMessage(channel,
											server.getLanguage("game.battleship.init.end", INIT_END_COMMAND));
								showGame(channel, player, "", false);
							} else // not valid location
								BotInstance.sendMessage(channel, server.getLanguage("game.battleship.init.nal") + " "
										+ server.getLanguage("game.battleship.help.do", HELP_COMMAND));
						} else // not valid direction
							BotInstance.sendMessage(channel, server.getLanguage("game.battleship.init.nad") + " "
									+ server.getLanguage("game.battleship.help.do", HELP_COMMAND));
					} else // not valid ship
						BotInstance.sendMessage(channel, server.getLanguage("game.battleship.init.nas") + " "
								+ server.getLanguage("game.battleship.help.do", HELP_COMMAND));
				} else // not a valid command
					BotInstance.sendMessage(channel, String.format("<%s> <A-J> <1-10> <%s> | ?",
							server.getLanguage("game.battleship.ship"), server.getLanguage("game.battleship.dir")));
			} else {
				IUser p = getUsers()[turn];
				if (player.getLongID() == p.getLongID()) {
					if (args.length == 2) {

					}
					// TODO
				} else {
					channel.sendMessage(server.getLanguage("game.badturn", p.mention()));
					return 0;
				}
			}
			return 0;
		}

		@Override
		public void init(IChannel channel) {
			mainChannel = channel;
			// TODO Auto-generated method stub
			super.init(channel);
		}

		private void showGame(IChannel channel, IUser player, String title, boolean showOthers) {
			PlayerData data = playerDatas.get(player);
			int maps = showOthers ? playerDatas.size() : 1;
			int width = 400;
			int height = MAP_HEIGHT * maps;
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = bufferedImage.createGraphics();
			graphics.setFont(new Font(graphics.getFont().getFontName(), Font.BOLD, 100));
			graphics.setStroke(new BasicStroke(4));
			graphics.setColor(Color.WHITE);
			graphics.drawRect(3, 15, width - 3, height - 3);
			if (data != null) {
				int i = 0;
				if (showOthers)
					for (Entry<IUser, PlayerData> p : playerDatas.entrySet()) {
						if (player.getLongID() == p.getKey().getLongID())
							continue;
						drawGame(graphics, i * MAP_HEIGHT, p.getValue(), false);
						i++;
					}
				drawGame(graphics, i * MAP_HEIGHT, data, true);
			} else {
				int i = 0;
				for (Entry<IUser, PlayerData> p : playerDatas.entrySet()) {
					drawGame(graphics, i * MAP_HEIGHT, p.getValue(), true);
					i++;
				}
			}
		}

		private void drawGame(Graphics2D graphics, int offsetY, PlayerData data, boolean drawShip) {
			graphics.setColor(COLOR_WATER);
			graphics.fillRect(3, offsetY, 394, 394);
			for (int i = 0; i < 9; i++)
				for (int j = 0; j < 9; j++) {
					graphics.drawLine(43, offsetY + 43 + j * 40, 43, offsetY + 43 + j * 40);
					graphics.drawLine(43 + i * 40, offsetY + 43, 43 + i * 40, offsetY + 43);
				}
			data.shipData.values().stream().filter(shipData -> drawShip || shipData.isDead(data))
					.forEach(shipData -> shipData.draw(graphics,
							new Square(20 + shipData.x * 40 - 18 * shipData.d.deltaX,
									offsetY + 20 + shipData.y * 40 - 18 * shipData.d.deltaY,
									20 + shipData.x * 40 + 18 * shipData.d.deltaX * shipData.ship.length,
									offsetY + 20 + shipData.y * 40 + 18 * shipData.d.deltaY * shipData.ship.length)));
			for (int i = 0; i < 10; i++)
				for (int j = 0; j < 10; j++) {
					int k = data.map[i][j];
					boolean touch = (k & 2) == 1;
					graphics.setColor(drawShip || touch ? (k & 1) == 1 ? COLOR_SHOOT_SHIP : COLOR_SHOOT_WATER
							: COLOR_SHOOT_WATER);
					if (touch)
						graphics.drawOval(10 + i * 40, 10 + i * 40, 20, 20);
					else
						graphics.fillOval(10 + i * 40, 10 + i * 40, 20, 20);
				}
			Command.drawCenteredString(data.player.getName(), 200, offsetY, graphics);
		}
	}

	public enum Direction {
		UP(0, -1, "game.battleship.dir.up"),

		DOWN(0, 1, "game.battleship.dir.down"),

		LEFT(-1, 0, "game.battleship.dir.left"),

		RIGHT(1, 0, "game.battleship.dir.right");

		public static Direction getByNameIgnoreCase(String name) {
			for (Direction dir : Direction.class.getEnumConstants())
				if (dir.name().equalsIgnoreCase(name))
					return dir;
			return null;
		}

		public final int deltaX;
		public final int deltaY;

		private final String lang;

		private Direction(int deltaX, int deltaY, String lang) {
			this.deltaX = deltaX;
			this.deltaY = deltaY;
			this.lang = lang;
		}

		public String getName(BotServer server) {
			return server.getLanguage(lang);
		}
	}

	public enum Ship {
		CARRIER(5, "game.battleship.ship.carrier", (g, d, s) -> {
			// TODO draw
		}),

		BATTLESHIP(4, "game.battleship.ship.battleship", (g, d, s) -> {
			// TODO draw
		}),

		CRUISER(3, "game.battleship.ship.cruiser", (g, d, s) -> {
			// TODO draw
		}),

		SUBMARINE(3, "game.battleship.ship.submarine", (g, d, s) -> {
			// TODO draw
		}),

		DESTROYER(2, "game.battleship.ship.destroyer", (g, d, s) -> {
			// TODO draw
		});

		public static Ship getByNameIgnoreCase(String name) {
			for (Ship ship : Ship.class.getEnumConstants())
				if (ship.name().equalsIgnoreCase(name))
					return ship;
			return null;
		}

		public final int length;
		private String lang;
		public final TriConsumer<Graphics2D, ShipData, Square> drawer;

		private Ship(int lenght, String lang, TriConsumer<Graphics2D, ShipData, Square> drawer) {
			this.length = lenght;
			this.lang = lang;
			this.drawer = drawer;
		}

		public String getName(BotServer server) {
			return server.getLanguage(lang);
		}
	}

	public class PlayerData {
		public final Map<Ship, ShipData> shipData = new HashMap<>();
		private boolean initEnds = false;
		/**
		 * touch|ship example : 2 (10) -> touched water 3 (11) -> touched ship
		 */
		public final int[][] map = new int[10][10];
		public final IUser player;

		public PlayerData(IUser player) {
			this.player = player;
			for (int i = 0; i < map.length; i++)
				for (int j = 0; j < map[i].length; j++) {
					map[i][j] = 0;
				}
		}

		public boolean hasInitEnds() {
			return initEnds;
		}

		public void setInitEnds(boolean initEnds) {
			this.initEnds = initEnds;
		}

		public boolean placeShip(int x, int y, Ship ship, Direction dir) {
			ShipData data = new ShipData(x, y, ship, dir);
			for (ShipData d : shipData.values()) {
				if (d.ship == ship)
					continue;
				if (d.collide(data))
					return false;
			}
			ShipData oldData = shipData.put(ship, data);
			if (oldData != null)
				applyOnMap(oldData, i -> i & 0xfffffffe); // remove old ship
			applyOnMap(data, i -> i | 1); // place new ship
			return true;
		}

		public void applyOnMap(ShipData data, Function<Integer, Integer> function) {
			int n, m;
			n = data.x + data.ship.length * data.d.deltaX;
			m = data.y + data.ship.length * data.d.deltaY;
			for (int i = data.x; i < n && i < 10; i += data.d.deltaX)
				for (int j = data.y; i < m && j < 10; j += data.d.deltaY)
					map[i][j] = function.apply(map[i][j]);
		}
	}

	public class ShipData {
		public final int x, y;
		public final Ship ship;
		public final Direction d;

		public ShipData(int x, int y, Ship ship, Direction d) {
			this.x = x;
			this.y = y;
			this.ship = ship;
			this.d = d;
		}

		/**
		 * true if the given ship collide with it
		 */
		public boolean collide(ShipData data) {
			if (data.equals(this))
				return true;
			int x = this.x;
			int y = this.y;
			int sx = x + d.deltaX * ship.length;
			int sy = y + d.deltaY * ship.length;
			if (x > sx) { // switch x<->sx
				x -= sx;
				sx += x;
				x = sx - x;
			}
			if (y > sy) { // switch y<->sy
				x -= sy;
				sy += y;
				y = sy - y;
			}
			for (int i = 0, j = data.x, k = data.y; i < data.ship.length; i++, j += data.d.deltaX, k += data.d.deltaY)
				if (x >= j && sx <= j && y >= k && sy <= k)
					return true;
			return false;
		}

		public void draw(Graphics2D graphics, Square square) {
			ship.drawer.accept(graphics, this, square);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ShipData other = (ShipData) obj;
			if (d != other.d)
				return false;
			if (ship != other.ship)
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		/**
		 * true if this location is a part of the ship
		 */
		public boolean shot(int x, int y) {
			for (int i = 0, j = this.x, k = this.y; i < ship.length; i++, j += d.deltaX, k += d.deltaY)
				if (j == x && k == y)
					return true;
			return false;
		}

		public boolean isDead(PlayerData data) {
			for (int i = 0, j = this.x, k = this.y; i < ship.length; i++, j += d.deltaX, k += d.deltaY)
				if ((data.map[j][k] & 2) == 1)
					return true;
			return true;
		}

	}

	@Override
	public GameInstance<?> getInstance(BotServer server, IUser... users) {
		return new BattleshipGameInstance(server, this, users);
	}

	@Override
	public String getName() {
		return "battleship";
	}

	@Override
	public int neededPlayer() {
		return 2;
	}

}
