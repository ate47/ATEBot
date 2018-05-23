package fr.atesab.bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import fr.atesab.bot.ServerConfig;
import fr.atesab.bot.command.APlayCommand;
import fr.atesab.bot.command.Command;
import fr.atesab.bot.command.ConnectCommand;
import fr.atesab.bot.command.SayCommand;
import fr.atesab.bot.game.GameInstance;
import fr.atesab.bot.handler.tools.AutoDeleteMessageHandler;
import fr.atesab.bot.handler.tools.AutoMessageHandler;
import fr.atesab.bot.handler.tools.AutoMessageHandler.MessageElement;
import fr.atesab.bot.utils.AudioProvider;
import fr.atesab.bot.utils.AudioProvider.ProviderType;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;

public class DiscordListener {
	public static class Answer {
		public String text;
		public File file;

		public Answer(String text) {
			this(text, null);
		}

		public Answer(String text, File file) {
			this.text = text;
			this.file = file;
		}

		public void answer(MessageReceivedEvent event) {
			String t = SayCommand.parsedMessage(event, this.text.split(" "));
			if (file != null)
				try {
					event.getChannel().sendFile(t, this.file);
				} catch (FileNotFoundException e) {
					event.getChannel().sendMessage(t);
				}
			else
				event.getChannel().sendMessage(t);
		}

	}

	public static final String USE_COMMAND_APP = "usecommand";
	public static final String SHOW_USER_LEAVE_APP = "showleave";
	public static final String SHOW_USER_JOIN_APP = "showjoin";
	public static final String SHOW_TRACK_PLAY_APP = "showtrackplay";
	public static final String DEFAULT_COMMAND_PREFIX = "!";

	public static String getReplaceText(String s) {
		return s.replace("[\u00E9\u00E8\u00EA\u00EB\u0119\u0117\u0113]", "e")
				.replace("[\u00E0\u00E2\u00E1\u00E4\u00E3\u00E5\u0101\u00AA]", "a").replace("\u00FF", "y")
				.replace("\u00E6", "ae").replace("\u0153", "oe").replace("[\u016B\u00FA\u00FC\u00F9\u00FB]", "u")
				.replace("[\u012B\u012F\u00ED\u00EC\u00EF\u00EE]", "i")
				.replace("[\u00BA\u014D\u00F8\u00F5\u00F3\u00F2\u00F6\u00F4]", "o");
	}

	private BotInstance botInstance;
	public final AudioPlayerManager playerManager;
	private Map<Long, AudioProvider> audioPlayers = new HashMap<Long, AudioProvider>();

	public DiscordListener(BotInstance botInstance) {
		this.botInstance = botInstance;
		this.playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerLocalSource(this.playerManager);
		AudioSourceManagers.registerRemoteSources(this.playerManager);
	}

	public boolean checkApp(String name, IGuild guild) {
		return checkApp(name, guild.getLongID());
	}

	public boolean checkApp(String name, long guildId) {
		ServerConfig config = botInstance.getServerConfigById(guildId);
		return (config != null && config.tools.contains(name));
	}

	public Map<Long, AudioProvider> getAudioPlayers() {
		return audioPlayers;
	}

	public BotInstance getBotInstance() {
		return botInstance;
	}

	@EventSubscriber
	public void onMessageReceivedEvent(MessageReceivedEvent event) {
		tryCommand(() -> {
			ServerConfig config = null;
			if (event.getGuild() != null)
				config = botInstance.getServerConfigById(event.getGuild().getLongID());
			String commandPrefix = config != null ? config.commandPrefix : DEFAULT_COMMAND_PREFIX;
			IMessage m = event.getMessage();
			String message = m.getContent();
			List<Answer> ans = new ArrayList<Answer>();
			if (message.startsWith(commandPrefix)) {
				if ((config != null && config.tools.contains(USE_COMMAND_APP)) || config == null) {
					message = message.substring(commandPrefix.length());
					String[] els = message.split(" ");
					String[] args = new String[els.length - 1];
					Command cmd = botInstance.getServer().getCommandByName(els[0]);
					if (cmd != null) {
						System.arraycopy(els, 1, args, 0, args.length);
						if (botInstance.getServer().userHasPerm(event.getAuthor(), event.getGuild(),
								cmd.neededPermission(), botInstance)) {
							try {
								if (!cmd.runCommand(event, args, message, config, botInstance))
									event.getChannel().sendMessage(commandPrefix + cmd.getUsage());
							} catch (MissingPermissionsException e) {
								String s = "";
								for (Permissions p : e.getMissingPermissions())
									s += (s.isEmpty() ? "" : ", ")
											+ botInstance.getServer().getLanguage("tools.guildmod.perm." + p.name());
								event.getChannel()
										.sendMessage(botInstance.getServer().getLanguage("needperm", s) + ".");
							} catch (IllegalArgumentException e) {
								event.getChannel().sendMessage("IllegalArgumentException: " + e.getMessage() + ".");
								return;
							}
						} else
							ans.add(new Answer(botInstance.getServer().getLanguage("noperm")));
					}
				}
			} else {
				message = m.getFormattedContent();
				if (config != null) {
					if (config.tools.contains(AutoDeleteMessageHandler.getInstance().toolName()))
						for (MessageElement msg : config.deleteMessages) {
							boolean a = false;
							for (String component : msg.question.split("::")) {
								switch (msg.type) {
								case equals:
									a = getReplaceText(message).equalsIgnoreCase(getReplaceText(component));
									break;
								case contain:
									a = getReplaceText(message.toLowerCase())
											.contains(getReplaceText(component.toLowerCase()));
									break;
								case startWith:
									a = getReplaceText(message.toLowerCase())
											.startsWith(getReplaceText(component.toLowerCase()));
									break;
								case regex:
									a = getReplaceText(message.toLowerCase())
											.matches(getReplaceText(component.toLowerCase()));
									break;
								}
								if (a) {
									event.getMessage().delete();
									return;
								}
							}
						}
					if (config.tools.contains(AutoMessageHandler.getInstance().toolName()))
						for (MessageElement msg : config.messages) {
							boolean a = false;
							for (String component : msg.question.split("::")) {
								switch (msg.type) {
								case equals:
									a = getReplaceText(message).equalsIgnoreCase(getReplaceText(component));
									break;
								case contain:
									a = getReplaceText(message.toLowerCase())
											.contains(getReplaceText(component.toLowerCase()));
									break;
								case startWith:
									a = getReplaceText(message.toLowerCase())
											.startsWith(getReplaceText(component.toLowerCase()));
									break;
								case regex:
									a = getReplaceText(message.toLowerCase())
											.matches(getReplaceText(component.toLowerCase()));
									break;
								}
								if (a)
									break;
							}
							if (a) {
								String[] answer = msg.answer.split("::");
								String aswr = answer.length != 0 ? answer[new Random().nextInt(answer.length)] : "";
								String[] file = msg.file.split("::");
								File f = new File(
										"atebot/" + (file.length != 0 ? file[new Random().nextInt(file.length)] : ""));
								if (f.exists() && !f.isDirectory())
									ans.add(new Answer(aswr, f));
								else
									ans.add(new Answer(aswr));
							}
						}
				}
				GameInstance<?> instance = this.botInstance.getGameInstanceByPlayer(event.getAuthor());
				if (instance != null) {
					int turn = instance.getTurn();
					IUser p = instance.getUsers()[turn];
					if (p.getLongID() == event.getAuthor().getLongID()) {
						int w;
						if ((w = instance.evaluateGame(event.getChannel(), turn, message.split(" "))) != 0) {
							event.getChannel().sendMessage(botInstance.getServer().getLanguage("game.win",
									instance.getUsers()[w - 1].mention()));
							instance.setEnded();
							this.botInstance.getGameInstances().remove(instance);
						}
					} else
						event.getChannel()
								.sendMessage(botInstance.getServer().getLanguage("game.badturn", p.mention()));
				}
			}
			if (ans.size() > 0) {
				for (Answer a : ans)
					a.answer(event);
			}
		}, event.getChannel());
	}

	@EventSubscriber
	public void onMove(UserVoiceChannelJoinEvent ev) {
		if (ev.getUser().getLongID() == ev.getClient().getOurUser().getLongID()) {
			ServerConfig config = botInstance.getServerConfigById(ev.getGuild().getLongID());
			config.oldVoiceChannelId = ev.getVoiceChannel().getLongID();
		}
	}

	@EventSubscriber
	public void onMove(UserVoiceChannelLeaveEvent ev) {
		if (ev.getUser().getLongID() == ev.getClient().getOurUser().getLongID()) {
			ServerConfig config = botInstance.getServerConfigById(ev.getGuild().getLongID());
			config.oldVoiceChannelId = 0;
		}
	}

	@EventSubscriber
	public void onMove(UserVoiceChannelMoveEvent ev) {
		if (ev.getUser().getLongID() == ev.getClient().getOurUser().getLongID()) {
			ServerConfig config = botInstance.getServerConfigById(ev.getGuild().getLongID());
			config.oldVoiceChannelId = ev.getNewChannel().getLongID();
		}
	}

	@EventSubscriber
	public void onReadyEvent(ReadyEvent event) {
		for (String gid : botInstance.getConfig().getConfig().keySet())
			try {
				IGuild guild = event.getClient().getGuildByID(Long.valueOf(gid));
				if (guild != null) {
					ServerConfig config = botInstance.getConfig().getConfig().get(gid);
					if (config.oldVoiceChannelId != 0) {
						IVoiceChannel voiceChannel = guild.getVoiceChannelByID(config.oldVoiceChannelId);
						if (voiceChannel != null) {
							AudioProvider audioProvider = ConnectCommand.connect(voiceChannel, botInstance, config);
							if (config.oldTrack != null) {
								APlayCommand.play(null, config.oldTrack, audioProvider, botInstance);
								if (!config.oldQueues.isEmpty()) {
									for (String identifier : config.oldQueues)
										APlayCommand.play(null, identifier, audioProvider, botInstance);
								}
							}
						}
					}
				}
			} catch (Exception e) {
			}
		event.getClient().changePresence(StatusType.ONLINE, botInstance.getConfig().getActivityType(),
				botInstance.getConfig().getPlayinformation());
	}

	public void onTrackEnd(IGuild guild, AudioProvider audioProvider, AudioTrack track, AudioTrackEndReason endReason) {
		ServerConfig config = botInstance.getServerConfigById(guild.getLongID());
		if (audioProvider.getType().equals(ProviderType.LOOP))
			audioProvider.getQueue().add(track);
		AudioTrack next = audioProvider.getQueue().poll();
		if (next != null) {
			config.oldTrack = next.getIdentifier();
			audioProvider.getAudioPlayer().playTrack(next);
			if (checkApp(SHOW_TRACK_PLAY_APP, guild))
				guild.getDefaultChannel().sendMessage(
						botInstance.getServer().getLanguage("tools.showtrackplay.msg", next.getInfo().title));
		} else
			config.oldTrack = "";
	}

	@EventSubscriber
	public void onUserJoin(UserJoinEvent ev) {
		if (checkApp(SHOW_USER_JOIN_APP, ev.getGuild()))
			ev.getGuild().getDefaultChannel()
					.sendMessage(botInstance.getServer().getLanguage("tools.showjoin.msg", ev.getUser().mention()));
	}

	@EventSubscriber
	public void onUserLeave(UserLeaveEvent ev) {
		if (checkApp(SHOW_USER_LEAVE_APP, ev.getGuild()))
			ev.getGuild().getDefaultChannel()
					.sendMessage(botInstance.getServer().getLanguage("tools.showleave.msg", ev.getUser().mention()));
	}

	public void tryCommand(Runnable runnable, IChannel channel) {
		try {
			runnable.run();
		} catch (MissingPermissionsException e) {
			String s = "";
			for (Permissions p : e.getMissingPermissions())
				s += (s.isEmpty() ? "" : ", ") + botInstance.getServer().getLanguage("tools.guildmod.perm." + p.name());
			if (!e.getMissingPermissions().contains(Permissions.SEND_MESSAGES))
				channel.sendMessage(botInstance.getServer().getLanguage("needperm", s) + ".");
		} catch (Exception e) {
			if (!(e instanceof DiscordException))
				channel.sendMessage(e.getClass().getSimpleName() + ": " + e.getMessage() + ".");
			if (!(e instanceof IllegalArgumentException))
				e.printStackTrace();

		}
	}
}
