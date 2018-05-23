package fr.atesab.bot;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.output.FileWriterWithEncoding;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import fr.atesab.bot.BotConfig;
import fr.atesab.bot.ServerConfig;
import fr.atesab.bot.command.*;
import fr.atesab.bot.console.Console;
import fr.atesab.bot.game.Game;
import fr.atesab.bot.game.TicTacToe;
import fr.atesab.bot.handler.*;
import fr.atesab.bot.handler.tools.*;
import fr.atesab.bot.utils.AudioProvider;
import fr.atesab.bot.utils.Language;
import fr.atesab.bot.utils.Permission;
import fr.atesab.web.BlogMessage;
import fr.atesab.web.IndexHandler;
import fr.atesab.web.InfoHandler;
import fr.atesab.web.InformationMessage;
import fr.atesab.web.UserInfoHandler;
import fr.atesab.web.WorkMessage;
import fr.atesab.web.WorksHandler;
import fr.atesab.web.tool.BlogConfigHandler;
import fr.atesab.web.tool.InfoConfigHandler;
import fr.atesab.web.tool.WebConfigHandler;
import fr.atesab.web.tool.WorksConfigHandler;
import javaxt.http.Server;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.api.internal.json.objects.EmbedObject.AuthorObject;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class BotServer {
	public static interface StringPattern {
		public String getPattern(String string);
	}

	public static final String BOT_VERSION = "1.5.2";
	public static final String BOT_AUTHOR = "ATE47";
	public static final String BOT_NAME = "ATEDiBot";

	public static final String[] APPS = { DiscordListener.USE_COMMAND_APP, DiscordListener.SHOW_USER_JOIN_APP,
			DiscordListener.SHOW_USER_LEAVE_APP, DiscordListener.SHOW_TRACK_PLAY_APP };
	public static final Permission[] perms = { new Permission("config", "panel"), new Permission("config", "panel"),
			new Permission("users", "panel"), new Permission("perm", "panel"), new Permission("blog", "panel"),
			new Permission("webconfig", "panel"), new Permission("app", "panel"), new Permission("works", "panel"),
			new Permission("info", "panel"),

			new Permission("tools", "tools"), new Permission("createbot", "tools", true),
			new Permission("deletebot", "tools"), new Permission("automessage", "tools", true),
			new Permission("deleteauto", "tools", true), new Permission("configserver", "tools", true),
			new Permission("cmdlist", "tools"), new Permission("files", "tools"),
			new Permission("messagetool", "tools", true), new Permission("localperm", "tools"),

			new Permission("game", "command"), new Permission("sond", "command"), new Permission("msgmod", "command"),
			new Permission("bot", "command"), new Permission("manageclients", "command"),
			new Permission("msg", "command"), new Permission("maths", "command"), new Permission("advmaths", "command"),
			new Permission("info", "command"), new Permission("audio", "command"),
			new Permission("savemessage", "command"), new Permission("tree", "command"),

			new Permission("audiogest", "guild"), new Permission("guildmod", "guild"),

			new Permission("ignorebotaccess", "panel"), new Permission("ignoreserveraccess", "panel"),

			new Permission("automessagev", "api"), new Permission("deleteautov", "api") };

	public static File createDir(String folder) {
		File d = new File(folder);
		if (!(d.exists() && d.isDirectory()))
			d.mkdirs();
		return d;
	}

	public static String listWriter(Iterable<String> iterable, String pattern) {
		String s = "";
		for (String str : iterable) {
			s += pattern.replaceAll("%s", str);
		}
		return s;
	}

	public static String listWriter(Iterable<String> iterable, StringPattern sp) {
		String s = "";
		for (String str : iterable) {
			s += sp.getPattern(str);
		}
		return s;
	}

	public static boolean mapContainKeys(Map<String, Object> map, String[] keys) {
		for (int i = 0; i < keys.length; i++) {
			if (!map.containsKey(keys[i]))
				return false;
		}
		return true;
	}

	public static boolean mapContainKeysS(Map<String, String> map, String[] keys) {
		for (int i = 0; i < keys.length; i++) {
			if (!map.containsKey(keys[i]))
				return false;
		}
		return true;
	}

	public static boolean mapContainNoEmptyKeys(Map<String, Object> map, String[] keys) {
		return mapContainKeys(map, keys) && mapNotEmpty(map, keys);
	}

	public static boolean mapEmpty(Map<String, Object> map, String[] keys) {
		for (int i = 0; i < keys.length; i++) {
			if (!map.get(keys[i]).toString().isEmpty())
				return false;
		}
		return true;
	}

	public static boolean mapNotEmpty(Map<String, Object> map, String[] keys) {
		for (int i = 0; i < keys.length; i++) {
			if (map.get(keys[i]).toString().isEmpty())
				return false;
		}
		return true;
	}

	public static String MD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}

	public static boolean permExist(String name) {
		for (Permission perm : perms) {
			if (perm.name.equals(name))
				return true;
		}
		return false;
	}

	public static String significantNumbers(double d, int n) {
		String s = String.format("%." + n + "G", d);
		if (s.contains("E+")) {
			s = String.format(Locale.US, "%.0f", Double.valueOf(String.format("%." + n + "G", d)));
		}
		return s;
	}

	private List<BotInstance> instances = new ArrayList<BotInstance>();
	private final List<String> defaultKickServerMessage;
	private final List<String> defaultBanMessage;
	private final List<String> defaultKickChannelMessage;

	private Console console;

	private int webPort;

	private int apiPort;

	private int webNumThreads;

	private String panelColor = "#00af00";

	private BotServlet servlet;

	private BotServlet webServlet;

	private Server webServer;

	private Map<String, Map<String, String>> sessions = new HashMap<String, Map<String, String>>();

	private List<BlogMessage> bMessages = new ArrayList<BlogMessage>();

	private List<WorkMessage> wMessages = new ArrayList<WorkMessage>();

	private InformationMessage nfoMessage;

	private Language lang;

	private List<Account> accounts = new ArrayList<Account>();

	private List<Command> commands = new ArrayList<Command>();

	private List<Game> games = new ArrayList<Game>();

	public BotServer(int webPort, int apiPort, int webNumThreads, String configFolder) {
		this.webPort = webPort;
		this.apiPort = apiPort;
		this.webNumThreads = webNumThreads;
		this.lang = new Language(configFolder);
		// CREATE DEFAULT CONFIG
		accounts.add(new Account("admin", "", "", new String[] { "admin" }, MD5("123456")));
		defaultKickServerMessage = new ArrayList<String>();
		defaultBanMessage = new ArrayList<String>();
		defaultKickChannelMessage = new ArrayList<String>();
		for (int i = 0; i < 10; i++)
			defaultKickChannelMessage.add(lang.getLanguage("tools.messagetool.kick.channel.default." + i));
		for (int i = 0; i < 10; i++)
			defaultKickServerMessage.add(lang.getLanguage("tools.messagetool.kick.server.default." + i));
		for (int i = 0; i < 10; i++)
			defaultBanMessage.add(lang.getLanguage("tools.messagetool.ban.server.default." + i));
		nfoMessage = new InformationMessage("", lang.getLanguage("webconfig.info.default.title"),
				lang.getLanguage("webconfig.info.default.text"));

		// REGISTER COMMAND
		registerCommand(new DecompoCommand(), new FactCommand(), new InformationCommand(), new KickCommand(),
				new ConnectCommand(), new APlayCommand(), new DisconnectCommand(), new TitleCommand(), new SayCommand(),
				new GCDCommand(), new NameCommand(), new SaveHistoryCommand(), new SondageCommand(),
				new UploadCommand(this), new InfoCommand(), new HugCommand(this), new PolynomialCommand(),
				new RandomCommand(), new EditMessageCommand(), new EvaluateCommand(), new GameCommand(),
				new SetPrefixCommand(), new TreeCommand(), new OlderCommand(), new HelpCommand());
		registerGame(new TicTacToe());

		servlet = new BotServlet(this, new LoginHandler("logo.png"), new PanelHandler(), "/bot");
		servlet.registerContext("users.ap", new AccountsPanelHandler());
		servlet.registerContext("usermod.ap", new AccountModHandler());
		servlet.registerContext("config.ap", new ConfigPanelHandler());
		ToolsHandler tools = new BotToolsHandler(this).registerTool(new ConfigServerHandler(),
				AutoMessageHandler.getInstance(), AutoDeleteMessageHandler.getInstance(), UserModHandler.getInstance(),
				GuildModHandler.getInstance(), new InfoToolHandler(), new MusicToolHandler(),
				new LocalPermissionHandler());
		ToolsHandler apps = new AppsHandler(this).registerTool(new FileHandler(), new CommandListHandler());
		ToolsHandler wconfig = new WebConfigHandler(this).registerTool(new BlogConfigHandler(),
				new WorksConfigHandler(), new InfoConfigHandler());
		servlet.registerContext("wconfig.ap", wconfig);
		servlet.registerContext("tools.ap", tools);
		servlet.registerContext("apps.ap", apps);

		List<ToolHandler> t = new ArrayList<>();
		t.addAll(tools.getTools());
		t.addAll(apps.getTools());
		t.addAll(wconfig.getTools());
		servlet.registerContext("ajax.ap", new AjaxHandler(t));

		webServlet = new BotServlet(this, new LoginHandler("logo.png"), new IndexHandler());
		webServlet.registerContext("bot", new ServletHandler(servlet));
		webServlet.registerContext("works.ap", new WorksHandler());
		webServlet.registerContext("help.ap", new InfoHandler());
		webServlet.registerContext("user.ap", new UserInfoHandler());
		
		loadConfig();
		
		webServer = new Server(this.webPort, this.webNumThreads, webServlet);

		webServer.start();

		(console = new Console(this, "ServerConsole")).start();
	}

	public Account connect(String username, String hash) {
		for (Account a : accounts) {
			if (a.name.equals(username) && a.hash.equals(hash))
				return a;
		}
		return null;
	}

	public boolean deleteAccount(String name) {
		for (int i = 0; i < accounts.size(); i++) {
			if (accounts.get(i).name.equals(name)) {
				accounts.remove(i);
				saveConfig();
				return true;
			}
		}
		return false;
	}

	public Account getAccountByName(String name) {
		for (Account a : accounts) {
			if (a.name.equals(name))
				return a;
		}
		return null;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public List<Account> getAccountsByUser(IUser user, IGuild guild) {
		List<Account> accounts = new ArrayList<Account>();
		for (Account a : accounts) {
			if (a.userId.equals(String.valueOf(user.getLongID()))) {
				accounts.add(a);
			} else {
				for (IRole r : user.getRolesForGuild(guild)) {
					if (a.groupId.equals(String.valueOf(r.getLongID())))
						accounts.add(a);
				}
			}
		}
		return accounts;
	}

	public int getApiPort() {
		return apiPort;
	}

	public List<BlogMessage> getbMessages() {
		return bMessages;
	}

	public BotInstance getBotInstanceByName(String name) {
		for (BotInstance instance : instances) {
			if (instance.getConfig().getName().equals(name))
				return instance;
		}
		return null;
	}

	public Command getCommandByName(String name) {
		for (Command cmd : commands) {
			if (cmd.getName().equals(name))
				return cmd;
			else if (cmd.getAliases() != null)
				for (String sb : cmd.getAliases())
					if (sb.equalsIgnoreCase(name))
						return cmd;
		}
		return null;
	}

	public List<Command> getCommands() {
		return commands;
	}

	public Console getConsole() {
		return console;
	}

	public List<String> getDefaultBanMessage() {
		return defaultBanMessage;
	}

	public List<String> getDefaultKickChannelMessage() {
		return defaultKickChannelMessage;
	}

	public List<String> getDefaultKickServerMessage() {
		return defaultKickServerMessage;
	}

	public EmbedObject getEmbedObject(IUser author, String title, String description) {
		EmbedObject eo = new EmbedObject();
		eo.color = Integer.valueOf(panelColor.substring(1, 3), 16) * 256 * 256
				+ Integer.valueOf(panelColor.substring(3, 5), 16) * 256
				+ Integer.valueOf(panelColor.substring(5, 7), 16);
		eo.author = new AuthorObject(author.getName(), null, author.getAvatarURL(), author.getAvatarURL());
		eo.title = title;
		eo.description = description;
		return eo;
	}

	public Game getGameByName(String name) {
		for (Game game : games) {
			if (game.getName().equalsIgnoreCase(name))
				return game;
		}
		return null;
	}

	public List<Game> getGames() {
		return games;
	}

	public List<BotInstance> getInstances() {
		return instances;
	}

	public Language getLang() {
		return lang;
	}

	public String getLanguage(String lang, Object... params) {
		return this.lang.getLanguage(lang, params);
	}

	public InformationMessage getNfoMessage() {
		return nfoMessage;
	}

	public String getPanelColor() {
		return panelColor;
	}

	public BotServlet getServlet() {
		return servlet;
	}

	public Map<String, String> getSession(String sessid) {
		return sessions.getOrDefault(sessid, new HashMap<String, String>());
	}

	public Map<String, Map<String, String>> getSessions() {
		return sessions;
	}

	public String getSessionValue(String sessid, String key) {
		return getSessionValueOrDefault(sessid, key, null);
	}

	public String getSessionValueOrDefault(String sessid, String key, String defaultValue) {
		Map<String, String> map = sessions.getOrDefault(sessid, new HashMap<String, String>());
		return map.getOrDefault(key, defaultValue);
	}

	public int getWebNumThreads() {
		return webNumThreads;
	}

	public int getWebPort() {
		return webPort;
	}

	public Server getWebServer() {
		return webServer;
	}

	public BotServlet getWebServlet() {
		return webServlet;
	}

	public List<WorkMessage> getwMessages() {
		return wMessages;
	}

	@SuppressWarnings("unchecked")
	public void loadConfig() {
		try {
			final Gson gson = new GsonBuilder().create();
			Reader reader = new InputStreamReader(new FileInputStream("botsetting.json"), Charset.forName("UTF-8"));
			Map<String, Object> hm = gson.fromJson(reader, HashMap.class);
			webPort = ((Double) (hm.getOrDefault("webPort", (double) webPort))).intValue();
			apiPort = ((Double) (hm.getOrDefault("apiPort", (double) apiPort))).intValue();
			ArrayList<LinkedTreeMap<String, Object>> postAcc = gson
					.fromJson((gson.toJson(hm.getOrDefault("accounts", accounts))), ArrayList.class);
			accounts.clear();
			for (LinkedTreeMap<String, Object> ltm : postAcc)
				accounts.add(gson.fromJson((gson.toJson(ltm)), Account.class));
			ArrayList<LinkedTreeMap<String, Object>> postBMsg = gson
					.fromJson((gson.toJson(hm.getOrDefault("bMessages", bMessages))), ArrayList.class);
			bMessages.clear();
			for (LinkedTreeMap<String, Object> ltm : postBMsg)
				bMessages.add(gson.fromJson((gson.toJson(ltm)), BlogMessage.class));

			ArrayList<LinkedTreeMap<String, Object>> postWMsg = gson
					.fromJson((gson.toJson(hm.getOrDefault("wMessages", wMessages))), ArrayList.class);
			wMessages.clear();
			for (LinkedTreeMap<String, Object> ltm : postWMsg)
				wMessages.add(gson.fromJson((gson.toJson(ltm)), WorkMessage.class));
			nfoMessage = gson.fromJson((gson.toJson(hm.getOrDefault("nfoMessage", nfoMessage))),
					InformationMessage.class);

			List<BotConfig> botConfigs = new ArrayList<BotConfig>();
			for (BotInstance instance : instances)
				botConfigs.add(instance.getConfig());
			ArrayList<LinkedTreeMap<String, Object>> postInstanceConfig = gson
					.fromJson((gson.toJson(hm.getOrDefault("botConfigs", botConfigs))), ArrayList.class);
			botConfigs.clear();
			instances.clear();
			for (int i = 0; i < postInstanceConfig.size(); i++) {
				try {
					LinkedTreeMap<String, Object> ltm = postInstanceConfig.get(i);
					String name = (String) ltm.getOrDefault("name", "");
					String token = (String) ltm.getOrDefault("token", "");
					Map<String, LinkedTreeMap<String, Object>> config = (Map<String, LinkedTreeMap<String, Object>>) ltm
							.getOrDefault("config", new HashMap<String, LinkedTreeMap<String, Object>>());
					Map<String, ServerConfig> scs = new HashMap<String, ServerConfig>();
					for (String key : config.keySet()) {
						scs.put(key, gson.fromJson((gson.toJson(config.get(key))), ServerConfig.class));
					}
					BotConfig bc = new BotConfig(name, token, scs);
					bc.setPlayinformation((String) ltm.getOrDefault("playinformation", ""));
					ActivityType type = ActivityType.PLAYING;
					String s = ltm.getOrDefault("activityType", ActivityType.PLAYING.name()).toString();
					for (ActivityType at : ActivityType.class.getEnumConstants())
						if (at.name().equals(s)) {
							type = at;
							break;
						}
					bc.setActivityType(type);
					botConfigs.add(bc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (BotConfig config : botConfigs)
				instances.add(new BotInstance(this, config));
		} catch (Exception e) {
			e.printStackTrace();
		}
		saveConfig();
		startClients();
	}

	public void registerCommand(Command... cmds) {
		for (Command cmd : cmds) {
			if (!commands.contains(cmd))
				commands.add(cmd);
		}
		commands.sort(new Comparator<Command>() {
			public int compare(Command o1, Command o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});
	}

	public void registerGame(Game... gams) {
		for (Game game : gams) {
			if (!games.contains(game))
				games.add(game);
		}
	}

	public void removeSession(String sessid) {
		if (sessions.containsKey(sessid))
			sessions.remove(sessid);
	}

	public void removeSessionValue(String sessid, String key) {
		Map<String, String> map = sessions.getOrDefault(sessid, new HashMap<String, String>());
		if (map.containsKey(key))
			map.remove(key);
		sessions.put(sessid, map);
	}

	public void saveConfig() {
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
			Map<String, Object> hm = new HashMap<String, Object>();
			hm.put("webNumThreads", webNumThreads);
			hm.put("webPort", webPort);
			hm.put("apiPort", apiPort);
			hm.put("accounts", accounts);
			hm.put("bMessages", bMessages);
			hm.put("wMessages", wMessages);
			hm.put("nfoMessage", nfoMessage);
			List<BotConfig> botConfigs = new ArrayList<BotConfig>();
			for (BotInstance instance : instances)
				botConfigs.add(instance.getConfig());
			hm.put("botConfigs", botConfigs);
			Writer writer = new FileWriterWithEncoding(new File("botsetting.json"), Charset.forName("UTF-8"));
			gson.toJson(hm, writer);
			writer.close();
		} catch (Exception e) {
		}
	}

	public void setPanelColor(String panelColor) {
		this.panelColor = panelColor;
	}

	public void setSessionValue(String sessid, String key, String value) {
		Map<String, String> map = sessions.getOrDefault(sessid, new HashMap<String, String>());
		map.put(key, value);
		sessions.put(sessid, map);
	}

	public void startClients() {
		for (BotInstance instance : instances) {
			if (instance.getClient() == null && instance.getConfig().getToken() != null) {
				try {
					instance.setClient(new ClientBuilder().withToken(instance.getConfig().getToken()).login())
							.getClient();
					DiscordListener listener = new DiscordListener(instance);
					instance.getClient().getDispatcher().registerListener(listener);
					instance.setListener(listener);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}

	public void stopClients() {
		for (BotInstance instance : instances) {
			if (instance.getClient() != null) {
				for (String id : instance.getConfig().getConfig().keySet()) {
					instance.getConfig().getConfig().get(id).oldTrack = "";
					instance.getConfig().getConfig().get(id).oldVoiceChannelId = 0;
				}
				for (IVoiceChannel voiceChannel : instance.getClient().getConnectedVoiceChannels()) {
					long gid = voiceChannel.getGuild().getLongID();
					ServerConfig config = instance.getServerConfigById(gid);
					config.oldVoiceChannelId = voiceChannel.getLongID();
					if (instance.getListener().getAudioPlayers().containsKey(gid)) {
						AudioProvider provider = instance.getListener().getAudioPlayers().get(gid);
						AudioTrack track = provider.getAudioPlayer().getPlayingTrack();
						if (track != null) {
							config.oldTrack = track.getIdentifier();
							config.oldQueues = new ArrayList<>(provider.getQueue().size());
							for (AudioTrack qt : provider.getQueue())
								config.oldQueues.add(qt.getIdentifier());
							break;
						}
					}
				}
			}
		}
	}

	public boolean userHasPerm(IUser user, IGuild guild, String perm, BotInstance botInstance) {
		if (perm == null)
			return true;
		String userid = String.valueOf(user.getLongID());
		ArrayList<String> rolesid = new ArrayList<>();
		if (guild != null) {
			for (IRole r : user.getRolesForGuild(guild)) {
				rolesid.add(String.valueOf(r.getLongID()));
			}
		}
		if (botInstance != null && guild != null) {
			ServerConfig serverConfig = botInstance.getServerConfigById(guild.getLongID());
			if (serverConfig != null) {
				for (ServerPermission serverPermission : serverConfig.getRolePermissions())
					for (String roleid : rolesid)
						if (serverPermission.getIdentifier().equals(roleid) && serverPermission.hasPermission(perm))
							return true;
				for (ServerPermission serverPermission : serverConfig.getUserPermissions())
					if (serverPermission.getIdentifier().equals(userid) && serverPermission.hasPermission(perm))
						return true;
			}
		}
		for (Account a : accounts) {
			if (a.userId.equals(userid) && a.hasPerm(perm))
				return true;
			for (String s : rolesid)
				if (a.groupId.equals(s) && a.hasPerm(perm))
					return true;
		}
		return false;
	}
}
