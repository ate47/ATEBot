package fr.atesab.bot.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.BotServer;
import fr.atesab.bot.utils.BotUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

public class BattleshipGame implements Game {
	private static final String HELP_COMMAND = "?";
	private static final String INIT_END_COMMAND = "confirm";

	public class BattleshipGameInstance extends GameInstance<BattleshipGame> {
		private IChannel mainChannel;
		private int turn;
		private boolean gameStarted = false;
		private Map<IUser, Map<Ship, ShipData>> playerData;
		private Map<IUser, Boolean> initEnds;
		private int[][] map;

		public BattleshipGameInstance(BotServer server, BattleshipGame game, IUser[] users) {
			super(server, game, users);
			map = new int[10][10];
			for (int i = 0; i < map.length; i++)
				for (int j = 0; j < map[i].length; j++)
					map[i][j] = 0;
			playerData = BotUtils.createHashMap(users, k -> new HashMap<Ship, ShipData>());
			initEnds = BotUtils.createHashMap(users, false);
		}

		@Override
		public void end(IChannel channel, int winner) {
			showGame(mainChannel, null,
					(winner >= 0 && winner < users.length) ? server.getLanguage("game.win", users[winner]) : "");
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
			if (!gameStarted) {
				if (args.length == 1 && args[0].equalsIgnoreCase(HELP_COMMAND))
					sendHelp(channel);
				else if (args.length == 1 && args[0].equalsIgnoreCase(INIT_END_COMMAND)) {
					initEnds.put(player, true);
					new Thread(() -> {
						int i = 0;
						for (IUser p : users) {
							try {
								if (i > 2)
									Thread.sleep(BotInstance.TIME_BETWEEN_SEND);
								IChannel pmc = p.getOrCreatePMChannel();
								pmc.sendMessage(server.getLanguage("game.start"));
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
							Map<Ship, ShipData> playerData = this.playerData.get(player);
							if (cs.length == 1 && cs[0] >= 'a' && cs[0] <= 'j' && args[2].matches("[1-9]|(10)")
									&& placeShip(playerData, cs[0] - 'a', Integer.valueOf(args[2]), ship, dir)) {
								BotInstance.sendMessage(channel, server.getLanguage("game.battleship.init.placed"));
								if (playerData.size() == Ship.values().length) // confirm end
									BotInstance.sendMessage(channel,
											server.getLanguage("game.battleship.init.end", INIT_END_COMMAND));
								showGame(channel, player, "");
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

		private boolean placeShip(Map<Ship, ShipData> playerData, int x, int y, Ship ship, Direction dir) {
			ShipData data = new ShipData(x, y, ship, dir);
			for (ShipData d : playerData.values()) {
				if (d.ship == ship)
					continue;
				if (d.collide(data))
					return false;
			}
			playerData.put(ship, data);
			return true;
		}

		private void showGame(IChannel channel, IUser player, String title) {
			// TODO Auto-generated method stub
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

		private int deltaX;
		private int deltaY;

		private String lang;

		private Direction(int deltaX, int deltaY, String lang) {
			this.deltaX = deltaX;
			this.deltaY = deltaY;
			this.lang = lang;
		}

		public int getDeltaX() {
			return deltaX;
		}

		public int getDeltaY() {
			return deltaY;
		}

		public String getName(BotServer server) {
			return server.getLanguage(lang);
		}
	}

	public enum Ship {
		CARRIER(5, "game.battleship.ship.carrier"),

		BATTLESHIP(4, "game.battleship.ship.battleship"),

		CRUISER(3, "game.battleship.ship.cruiser"),

		SUBMARINE(3, "game.battleship.ship.submarine"),

		DESTROYER(2, "game.battleship.ship.destroyer");

		public static Ship getByNameIgnoreCase(String name) {
			for (Ship ship : Ship.class.getEnumConstants())
				if (ship.name().equalsIgnoreCase(name))
					return ship;
			return null;
		}

		public final int length;

		private String lang;

		private Ship(int lenght, String lang) {
			this.length = lenght;
			this.lang = lang;
		}

		public String getName(BotServer server) {
			return server.getLanguage(lang);
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
			int sx = x + d.getDeltaX() * ship.length;
			int sy = y + d.getDeltaY() * ship.length;
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
			for (int i = 0, j = data.x, k = data.y; i < data.ship.length; i++, j += data.d.getDeltaX(), k += data.d
					.getDeltaY())
				if (x >= j && sx <= j && y >= k && sy <= k)
					return true;
			return false;
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
			for (int i = 0, j = this.x, k = this.y; i < ship.length; i++, j += d.getDeltaX(), k += d.getDeltaY())
				if (j == x && k == y)
					return true;
			return false;
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
