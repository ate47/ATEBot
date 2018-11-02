package fr.atesab.bot.game;

import fr.atesab.bot.BotServer;
import fr.atesab.bot.command.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class NumberFindGame implements Game {
	public class NumberFindInstance extends GameInstance<NumberFindGame> {
		private long start;
		private int number;
		private int numberTried = 0;

		public NumberFindInstance(BotServer server, NumberFindGame game, IUser[] users) {
			super(server, game, users);
			start = System.currentTimeMillis();
			number = random.nextInt(bound) + 1;
		}

		@Override
		public void init(IChannel channel) {
			channel.sendMessage(server.getLanguage("game.nfind.start", game.bound));
			super.init(channel);
		}

		@Override
		public int evaluateGame(IChannel channel, IUser player, String[] args) {
			if (args.length != 1 || !args[0].matches("[1-9][0-9]*")) {
				channel.sendMessage("1-...-" + game.bound);
			} else {
				try {
					int g = Integer.valueOf(args[0]);
					numberTried++;
					if (number == g) {
						return -2;
					} else if (number > g)
						channel.sendMessage(server.getLanguage("game.nfind.more"));
					else
						channel.sendMessage(server.getLanguage("game.nfind.less"));
				} catch (NumberFormatException e) {
					channel.sendMessage(server.getLanguage("game.nfind.less"));
				}
			}
			return 0;
		}

		@Override
		public void end(IChannel channel, int winner) {
			if (winner == -1)
				channel.sendMessage(server.getLanguage("game.nfind.loose", number));
			else
				channel.sendMessage(server.getLanguage("game.nfind.find",
						Command.getTime(System.currentTimeMillis() - start), numberTried, users[winner]));
			super.end(channel, winner);
		}
	}

	private int bound;

	public NumberFindGame(int bound) {
		if (bound < 1)
			throw new IllegalArgumentException("NumberFind boundary must be higher than 1");
		this.bound = bound;
	}

	@Override
	public GameInstance<?> getInstance(BotServer server, IUser... users) {
		return new NumberFindInstance(server, this, users);
	}

	@Override
	public String getName() {
		return "nfind";
	}

	@Override
	public int neededPlayer() {
		return 1;
	}

	@Override
	public int maxPlayer() {
		return -1;
	}
}
