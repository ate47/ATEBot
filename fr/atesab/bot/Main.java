package fr.atesab.bot;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import fr.atesab.bot.command.*;
import fr.atesab.bot.handler.*;
import fr.atesab.bot.handler.tools.AutoMessageHandler;
import fr.atesab.bot.handler.tools.CommandListHandler;
import fr.atesab.bot.handler.tools.GuildModHandler;
import fr.atesab.bot.handler.tools.MusicUploadHandler;
import fr.atesab.bot.handler.tools.UserModHandler;
import javaxt.http.Server;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

public class Main {
	public static long afMsgGain=20;
	public static long afBlockMessage=100;
	public static long afPointLosePerSecond=50;
	public static int webPort = 2201;
	public static int webNumThreads = 50;
	public static String PANEL_COLOR = "#00af00";
	public static BotServlet servlet;
	public static Server webServer;
	public static String token = "";
	public static String playinformation = "working fine...";
	public static long appid;
	public static Map<String, Map<String, String>> sessions = new HashMap<String,Map<String, String>>();
	public static Langage lang;
	public static ArrayList<Account> accounts = new ArrayList<Account>();
	public static ArrayList<Command> commands = new ArrayList<Command>();
	public static ArrayList<AutoMessageHandler.MessageElement> messages = new ArrayList<AutoMessageHandler.MessageElement>();
	public static IDiscordClient client;
	public static String[][] perms = {
			{"config","panel"},
			{"users","panel"},
			{"perm","panel"},
			{"tools","tools"},
			{"automessage","tools"},
			{"cmdlist","tools"},
			{"bot","command"},
			{"manageclients","command"},
			{"maths","command"},
			{"info","command"},
			{"audio","command"},
			{"audiogest","panel"},
			{"guildmod","guild"}
		};
	public static void setSessionValue(String sessid, String key, String value){
		Map<String,String> map = sessions.getOrDefault(sessid, new HashMap<String,String>());
		map.put(key, value);
		sessions.put(sessid, map);
	}
	public static Map<String,String> getSession(String sessid){
		return sessions.getOrDefault(sessid, new HashMap<String,String>());
	}
	public static String getSessionValue(String sessid, String key){
		return getSessionValueOrDefault(sessid, key, null);
	}
	public static String getSessionValueOrDefault(String sessid, String key, String defaultValue){
		Map<String,String> map = sessions.getOrDefault(sessid, new HashMap<String,String>());
		return map.getOrDefault(key, defaultValue);
	}
	public static void removeSessionValue(String sessid, String key){
		Map<String,String> map = sessions.getOrDefault(sessid, new HashMap<String,String>());
		if(map.containsKey(key))map.remove(key);
		sessions.put(sessid, map);
	}
	public static void removeSession(String sessid){
		if(sessions.containsKey(sessid))sessions.remove(sessid);
	}
	public static List<Account> getAccountsByUser(IUser user, IGuild guild){
		List<Account> accounts = new ArrayList<Account>();
		for (Account a: Main.accounts) {
			if(a.userId.equals(String.valueOf(user.getLongID()))) {
				accounts.add(a);
			} else {
				for (IRole r: user.getRolesForGuild(guild)) {
					if(a.groupId.equals(String.valueOf(r.getLongID())))accounts.add(a);
				}
			}
		}
		return accounts;
	}
	public static Account getAccountByName(String name){
		for (Account a: accounts){
			if(a.name.equals(name)) return a;
		}
		return null;
	}
	public static boolean deleteAccount(String name) {
		for (int i = 0; i < accounts.size(); i++) {
			if(accounts.get(i).name.equals(name)) {
				accounts.remove(i);
				saveConfig();
				return true;
			}
		}
		return false;
	}
	public static Account connect(String username, String hash){
		for (Account a: accounts){
			if(a.name.equals(username) && a.hash.equals(hash)) return a;
		}
		return null;
	}
	public static boolean userHasPerm(IUser user, IGuild guild, String perm) {
		if(perm==null) return true;
		String userid = String.valueOf(user.getLongID());
		ArrayList<String> rolesid = new ArrayList<String>();
		if(guild!=null) {
			for (IRole r :user.getRolesForGuild(guild)) {
				rolesid.add(String.valueOf(r.getLongID()));
			}
		}
		for (Account a:accounts) {
			if(a.userId.equals(userid) && a.hasPerm(perm)) return true;
			for (String s: rolesid) if(a.groupId.equals(s) && a.hasPerm(perm)) return true;
		}
		return false;
	}
	public static void registerCommand(Command... cmds) {
		for (Command cmd : cmds) {
			if(!commands.contains(cmd)) commands.add(cmd);
		}
		commands.sort(new Comparator<Command>() {
			public int compare(Command o1, Command o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});
	}
	public static Command getCommandByName(String name) {
		for (Command cmd: commands) if(cmd.getName().equals(name)) return cmd;
		return null;
	}
	public static void main(String[] args) {
		System.setProperty("file.encoding", "UTF-8");
		//CREATE DEFAULT CONFIG
		accounts.add(new Account("admin", "", "", new String[]{"admin"}, MD5("123456")));
		//REGISTER COMMAND
		registerCommand(new DecompoCommand(), new FactCommand(), new InformationCommand(), new KickCommand(), new ConnectCommand(),
				new APlayCommand(), new DisconnectCommand(), new TitleCommand(), new SayCommand());
		//LOAD LANGAGE
		lang = new Langage("atebot");
		//\u534d
		loadConfig();
		client = createClient(token, true);
		if(client == null){
			System.out.println("Client can't be create, check token or try again.");
			return;
		}
		appid = Long.valueOf(client.getApplicationClientID()).longValue();
		(new Thread(new BotRunnable())).start();
		client.getDispatcher().registerListener(new DListener());
		servlet = new BotServlet(new LoginHandler(), new PanelHandler());
		servlet.registerContext("/users.ap", new AccountsPanelHandler());
		servlet.registerContext("/config.ap", new ConfigPanelHandler());
		servlet.registerContext("/tools.ap", new ToolsHandler()
				.registerTool(new AutoMessageHandler(),
						new CommandListHandler(),
						new MusicUploadHandler(),
						new UserModHandler(),
						new GuildModHandler())
				);
		webServer = new Server(webPort, webNumThreads, servlet);
		webServer.start();
	}
    public static IDiscordClient createClient(String token, boolean login) { // Returns a new instance of the Discord client
        ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
        clientBuilder.withToken(token); // Adds the login info to the builder
        try {
            if (login) {
                return clientBuilder.login(); // Creates the client instance and logs the client in
            } else {
                return clientBuilder.build(); // Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
            }
        } catch (DiscordException e) { // This is thrown if there was a problem building the client
            e.printStackTrace();
            return null;
        }
    }

	public static boolean mapContainNoEmptyKeys(Map<String, Object> map, String[] keys){
		return mapContainKeys(map, keys) && mapNotEmpty(map, keys);
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
	public static boolean mapEmpty(Map<String, Object> map, String[] keys) {
		for (int i = 0; i < keys.length; i++) {
			if(!map.get(keys[i]).toString().isEmpty()) return false;
		}
		return true;
	}
	public static boolean mapNotEmpty(Map<String, Object> map, String[] keys) {
		for (int i = 0; i < keys.length; i++) {
			if(map.get(keys[i]).toString().isEmpty()) return false;
		}
		return true;
	}
	public static String MD5(String md5) {
		   try {
		        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		        byte[] array = md.digest(md5.getBytes());
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < array.length; ++i) {
		          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
		       }
		        return sb.toString();
		    } catch (java.security.NoSuchAlgorithmException e) {
		    }
		    return null;
		}
    public static void saveConfig(){
    	try{
	    	GsonBuilder builder = new GsonBuilder();
	    	Gson gson = builder.setPrettyPrinting().enableComplexMapKeySerialization().create();
			Map<String, Object> hm = new HashMap<String, Object>();
			hm.put("token", token);
			hm.put("webNumThreads", webNumThreads);
			hm.put("playinformation", playinformation);
			hm.put("webPort", webPort);
			hm.put("accounts", accounts);
			hm.put("messages", messages);
	    	
			Writer writer = new FileWriter("botsetting.json");
			gson.toJson(hm, writer);
			writer.close();
    	}catch (Exception e) {}
    }
    @SuppressWarnings("unchecked")
	public static void loadConfig(){
    	try {
    		final GsonBuilder builder = new GsonBuilder();
    		final Gson gson = builder.create();
    		Reader reader = new FileReader("botsetting.json");
    		Map<String, Object> hm = gson.fromJson(reader, HashMap.class);

    		webPort = ((Double) (hm.getOrDefault("webPort", (double) webPort))).intValue();
    		token = (String) hm.getOrDefault("token", token);
    		playinformation = (String) hm.getOrDefault("playinformation", playinformation);
    		
    		ArrayList<LinkedTreeMap<String, Object>> postAcc = gson.fromJson((gson.toJson(hm.getOrDefault("accounts", accounts))), ArrayList.class);
    		accounts.clear();
    		for (LinkedTreeMap<String, Object> ltm: postAcc) {
    			Account elm = gson.fromJson((gson.toJson(ltm)), Account.class);
    			accounts.add(elm);
    		}
    		ArrayList<LinkedTreeMap<String, Object>> postMsg = gson.fromJson((gson.toJson(hm.getOrDefault("messages", messages))), ArrayList.class);
			messages.clear();
			for (int i = 0; i < postMsg.size(); i++) {
				LinkedTreeMap<String, Object> ltm = postMsg.get(i);
				AutoMessageHandler.MessageElement elm = gson.fromJson((gson.toJson(ltm)), AutoMessageHandler.MessageElement.class);
				messages.add(elm);
			}
		} catch (Exception e) {}
    	saveConfig();
    }
    public static String listWriter(ArrayList<String> list, String pattern) {
    	String s = "";
    	for(String str:list) {
    		s+=pattern.replaceAll("%s", str);
    	}
    	return s;
    }
    public static String listWriter(ArrayList<String> list, StringPattern sp) {
    	String s = "";
    	for(String str:list) {
    		s+=sp.getPattern(str);
    	}
    	return s;
    }
    public static interface StringPattern{
    	public String getPattern(String string);
    }
    
}
