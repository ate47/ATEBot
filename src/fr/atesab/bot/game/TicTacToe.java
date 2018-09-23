package fr.atesab.bot.game;

import java.awt.Color;
import java.awt.image.BufferedImage;

import fr.atesab.bot.BotServer;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class TicTacToe implements Game {
	public static class TicTacToeInstance extends TurnGameInstance<TicTacToe> {
		private int turn;
		private int[] map;
		public TicTacToeInstance(BotServer server, TicTacToe game, IUser[] users) {
			super(server, game, users);
			this.turn = -1;
			map = new int[] {0, 0, 0,  0, 0, 0,  0, 0, 0};
		}
		@Override
		public int evaluateGameTurn(IChannel channel, int player, String[] args) {
			if(args.length==2 && args[0].matches("[123]") && args[1].matches("[123]")) {
				int index = (Integer.valueOf(args[0])-1) + 3*(Integer.valueOf(args[1])-1);
				if(map[index]==0) {
					map[index] = player;
					this.showGame(channel);
					return this.winGame();
				} else {
					channel.sendMessage(server.getLanguage("game.ttt.badplay"));
					return 0;
				}
			} else {
				channel.sendMessage("<0-1-2> <0-1-2>");
				return 0;
			}
		}
		@Override
		public int getTurn() {
			return (this.turn+=1)%getUsers().length;
		}
		public void showGame(IChannel channel) {
			int width = 640;
			int height = 360;
			BufferedImage bufImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			for (int x = 0; x < width; x++)
				for (int y = 0; y < height; y++) {
					bufImage.setRGB(x, y, Color.WHITE.getRGB());
					
				}
			
		}
		public int winGame() {
			if(map[0] == map[1] && map[1] == map[2] && map[0]!=0) return map[0];
			if(map[3] == map[4] && map[4] == map[5] && map[3]!=0) return map[3];
			if(map[6] == map[7] && map[7] == map[8] && map[6]!=0) return map[6];
			if(map[0] == map[3] && map[3] == map[6] && map[0]!=0) return map[0];
			if(map[1] == map[4] && map[4] == map[7] && map[1]!=0) return map[1];
			if(map[2] == map[5] && map[5] == map[8] && map[2]!=0) return map[2];
			if(map[0] == map[4] && map[4] == map[8] && map[0]!=0) return map[4];
			if(map[2] == map[4] && map[4] == map[6] && map[2]!=0) return map[4];
			return 0;
		}
	}
	@Override
	public GameInstance<TicTacToe> getInstance(BotServer server, IUser... users) {
		if(users.length!=2)throw new IllegalArgumentException("");
		return new TicTacToeInstance(server, this, users);
	}
	@Override
	public String getName() {
		return "ttt";
	}
	
	@Override
	public int neededPlayer() {
		return 2;
	}
}
