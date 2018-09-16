package fr.atesab.bot.game;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import fr.atesab.bot.BotServer;
import fr.atesab.bot.command.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

public class HangmanGame implements Game {
	public class HangmanGameInstance extends GameInstance<HangmanGame> {
		protected IUser main;
		protected char[] text;
		protected char[] showText;
		protected Set<Character> playedChars = new HashSet<>();
		protected int phase = -1;
		protected IChannel mainChannel;
		private boolean win = false;

		public HangmanGameInstance(BotServer server, HangmanGame game, IUser[] users) {
			super(server, game, users);
		}

		protected boolean containChar(char c) {
			for (int i = 0; i < text.length; i++)
				if (text[i] == c)
					return true;
			return false;
		}

		@Override
		public void end(IChannel channel, int winner) {
			String s;
			switch (winner) {
			case -3: // loose
				s = server.getLanguage("game.hangman.loose");
				break;
			case -4: // win for all
				s = server.getLanguage("game.hangman.loose");
				break;
			default:
				s = !channel.equals(mainChannel) ? server.getLanguage("game.stopgame") : "";
				break;
			}
			mainChannel.sendMessage(s + " " + server.getLanguage("game.hangman.endMessage", new String(text)));
			super.end(channel, winner);
		}

		@Override
		public int evaluateGame(IChannel channel, IUser player, String[] args) {
			if (phase < 0) {
				if (player.equals(main)) {
					String message = Command.buildString(args, 0).trim().toLowerCase();
					if (message.length() > 2 && message.matches("([a-z ]|(-))+")) {
						text = message.toCharArray();
						showText = new char[text.length * 2];
						/*
						 * Write 1st and last letters
						 */
						showText[0] = text[0];
						showText[1] = ' ';
						int k = text.length - 1;
						showText[k * 2] = text[k];
						showText[k * 2 + 1] = ' ';
						phase = 0;
						mainChannel.sendMessage(server.getLanguage("game.hangman.select",
								Arrays.stream(users).map(IUser::mention).collect(Collectors.joining(", "))));
						showGame();
					} else
						channel.sendMessage(server.getLanguage("game.hangman.needSend.pm.error"));
				}
			} else {
				if (!channel.equals(mainChannel) || main.equals(player))
					return 0;
				String message = Command.buildString(args, 0).trim().toLowerCase();
				ms: if (message.length() == text.length) {
					/*
					 * Check if the word is correct
					 */
					char[] text2 = message.toCharArray();
					for (int i = 0; i < text2.length; i++)
						if (text[i] != text2[i]) {
							phase++;
							break ms;
						}
					win = true;
					return -3;
				} else if (message.length() == 1) {
					/*
					 * Check if this letter is between a and z, not already played and in text
					 */
					char c = message.charAt(0);
					if (c >= 'a' && c <= 'z' && playedChars.add(c) && !containChar(c))
						phase++;
				}
				showGame();
			}
			return phase >= 11 ? -2 : win() ? -3 : 0;
		}

		@Override
		public void init(IChannel channel) {
			mainChannel = channel;
			main = users[random.nextInt(users.length)];
			channel.sendMessage(server.getLanguage("game.hangman.needSend", main));
			try {
				main.getOrCreatePMChannel().sendMessage(server.getLanguage("game.hangman.needSend.pm"));
			} catch (DiscordException e) {
				; // if doesn't allow PM
			}
			super.init(channel);
		}

		public void showGame() {
			if (phase < 0)
				return;
			for (int i = 1; i < text.length - 1; i++) {
				char c = text[i];
				showText[i * 2] = (c == ' ' || c == '-' || playedChars.contains(c)) ? c : '_';
				showText[i * 2 + 1] = ' ';
			}
			int width = 600;
			int height = 600;
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = bufferedImage.createGraphics();
			graphics.setFont(new Font(graphics.getFont().getFontName(), Font.BOLD, 25));
			graphics.setStroke(new BasicStroke(5));
			if (phase > 0) {
				graphics.drawLine(60, 570, 240, 570); // bottom
				if (phase > 1) {
					graphics.drawLine(150, 570, 150, 120); // main
					if (phase > 2) {
						graphics.drawLine(150, 120, 480, 120); // top
						if (phase > 3) {
							graphics.drawLine(150, 210, 240, 120); // retention plank
							if (phase > 4) {
								graphics.setStroke(new BasicStroke(3));
								graphics.drawLine(480, 120, 480, 240); // rope
								if (phase > 5) {
									graphics.drawOval(405, 240, 150, 150); // head
									if (phase > 6) {
										graphics.drawLine(480, 390, 480, 460); // body
										if (phase > 7) {
											graphics.drawLine(480, 405, 420, 440); // left arm
											if (phase > 8) {
												graphics.drawLine(480, 405, 540, 440); // right arm
												if (phase > 9) {
													graphics.drawLine(480, 460, 520, 505); // left leg
													if (phase > 10) {
														graphics.drawLine(480, 460, 440, 505); // right leg
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			Command.drawCenteredString(new String(phase > 10 || win ? text : showText), width / 2, 601, graphics);
			try {
				mainChannel.sendFile("", Command.streamFromImage(bufferedImage), "show.png");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		protected boolean win() {
			for (int i = 0; i < text.length; i++) {
				char c = text[i];
				if (!(c == ' ' || c == '-') && !playedChars.contains(c))
					return false;
			}
			return true;
		}
	}

	@Override
	public GameInstance<?> getInstance(BotServer server, IUser... users) {
		return new HangmanGameInstance(server, this, users);
	}

	@Override
	public String getName() {
		return "hangman";
	}

	@Override
	public int maxPlayer() {
		return -1;
	}

	@Override
	public int neededPlayer() {
		return 2;
	}

}
