package fr.atesab.bot.command;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.BotServer;
import fr.atesab.bot.ServerConfig;
import fr.atesab.bot.utils.CollectorImpl;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class OlderCommand extends ASyncCommand {
	static class MessageElement {
		IUser user;
		long timestamp;
		long firstTimestamp;
		int number;

		MessageElement(IMessage message, int number) {
			user = message.getAuthor();
			timestamp = (message.getEditedTimestamp().isPresent() ? message.getEditedTimestamp().get()
					: message.getTimestamp()).getEpochSecond();
			firstTimestamp = timestamp;
			this.number = number;
		}

		int compareTo(MessageElement messageElement) {
			return Long.compare(timestamp, messageElement.timestamp);
		}

		void update(IMessage message) {
			long timestamp = (message.getEditedTimestamp().isPresent() ? message.getEditedTimestamp().get()
					: message.getTimestamp()).getEpochSecond();
			this.timestamp = Math.max(timestamp, this.timestamp);
			firstTimestamp = Math.min(timestamp, firstTimestamp);
			this.number += 1;
		}

		MessageElement add(MessageElement me2) {
			this.user = me2.user;
			this.timestamp = Math.max(me2.timestamp, timestamp);
			this.firstTimestamp = Math.min(me2.firstTimestamp, firstTimestamp);
			this.number += me2.number;
			return this;
		}

		static List<MessageElement> merge(List<MessageElement> lme1, List<MessageElement> lme2) {
			lme1.removeIf(me1 -> {
				Optional<MessageElement> msg = lme2.stream().filter(me2 -> me1.user.equals(me2.user)).findAny();
				if (!msg.isPresent())
					return false;
				me1.add(msg.get());
				return true;
			});
			lme1.addAll(lme2);
			return lme1;
		}

	}

	@Override
	public boolean runCommandASync(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		if (args.length != 0 || event.getGuild() == null)
			return false;
		long startTime = System.currentTimeMillis();
		event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.scm.pre")); // Lecture en cours...
		StringBuffer buffer = new StringBuffer();
		SimpleDateFormat format = new SimpleDateFormat(botInstance.getServer().getLanguage("cmd.older.date")); // yyyy.MM.dd
																												// HH:mm:ss
		event.getGuild().getChannels().stream()
				.collect(new CollectorImpl<>(() -> new ArrayList<IMessage>(), (list, channel) -> {
					try {
						list.addAll(channel.getFullMessageHistory());
					} catch (Exception e) {
					}
				}, (l1, l2) -> {
					l1.addAll(l2);
					return l1;
				}, t -> {
					event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.scm.post") // Lecture
																										// terminer.
							+ "(" + getTime(System.currentTimeMillis() - startTime) + ")");
					return t;
				})).stream().collect(new CollectorImpl<>(() -> new ArrayList<>(), (lme, msg) -> {
					Optional<MessageElement> messageElement = lme.stream().filter(me -> me.user.equals(msg.getAuthor()))
							.findFirst();
					if (messageElement.isPresent())
						messageElement.get().update(msg);
					else
						lme.add(new MessageElement(msg, 1));
				}, MessageElement::merge)).stream().sorted(MessageElement::compareTo).forEach(me -> {
					String next = "\n" + me.user.getName() + "#" + me.user.getDiscriminator() + " ("
							+ botInstance.getServer().getLanguage("cmd.older.first") + ": " // Premier
							+ format.format(new Date(me.firstTimestamp * 1000L)) + "): "
							+ format.format(new Date(me.timestamp * 1000L)) + " - " + me.number + " "
							+ botInstance.getServer()
									.getLanguage(me.number > 1 ? "cmd.older.messages" : "cmd.older.message"); // message(s)
					if (next.length() + buffer.length() < 2000) {
						buffer.append(next);
					} else {
						event.getChannel().sendMessage(buffer.toString());
						buffer.setLength(0);
						buffer.append(next);
					}
				});
		if (buffer.length() != 0)
			event.getChannel().sendMessage(buffer.toString());
		return true;
	}

	@Override
	public String getName() {
		return "older";
	}

	@Override
	public String neededPermission() {
		return "savemessage";
	}

	private static String getTime(long time) {
		return time < 1000 ? time + "ms"
				: ((time < 10000) ? BotServer.significantNumbers((time / 1000D), 3) + "s"
						: ((time < 60000) ? (time / 1000) + "s"
								: ((time < 600000) ? BotServer.significantNumbers(((time / 1000) / 60D), 3) + "min"
										: ((time < 36000000)
												? BotServer.significantNumbers(((time / 60000) / 60D), 3) + "min"
												: (time / 3600000) + "h"))));
	}
}
