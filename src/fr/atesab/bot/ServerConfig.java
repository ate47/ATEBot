package fr.atesab.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import fr.atesab.bot.handler.tools.AutoMessageHandler;
import fr.atesab.bot.utils.AudioProvider;
import fr.atesab.bot.utils.AudioProvider.ProviderType;

public class ServerConfig {
	public long afMsgGain = 20;
	public long afBlockMessage = 100;
	public long afPointLosePerSecond = 50;
	public long oldVoiceChannelId = 0;
	public int vol = 100;
	public AudioProvider.ProviderType providerType = ProviderType.NONE;
	public String oldTrack = "";
	public List<String> oldQueues = new ArrayList<>();
	public String ttsTool = "";
	public ConcurrentHashMap<Long, Long> originBind = new ConcurrentHashMap<Long, Long>();
	public List<String> kickServerMessage = new ArrayList<String>();
	public List<String> banMessage = new ArrayList<String>();
	public List<String> kickChannelMessage = new ArrayList<String>();
	public List<String> tools = new ArrayList<String>();
	public List<AutoMessageHandler.MessageElement> messages = new ArrayList<>();
	public List<AutoMessageHandler.MessageElement> deleteMessages = new ArrayList<>();
	public List<ServerPermission> userPermissions = new ArrayList<>();
	public List<ServerPermission> rolePermissions = new ArrayList<>();
	public String commandPrefix = DiscordListener.DEFAULT_COMMAND_PREFIX;
	public ServerConfig(BotServer server) {
		kickServerMessage = server.getDefaultKickServerMessage();
		banMessage = server.getDefaultBanMessage();
		kickChannelMessage = server.getDefaultKickChannelMessage();
	}
	public ConcurrentHashMap<Long, Long> getOriginBind() {
		return originBind==null?(originBind=new ConcurrentHashMap<Long, Long>()):originBind;
	}
	public List<ServerPermission> getRolePermissions() {
		return rolePermissions==null?(rolePermissions=new ArrayList<>()):rolePermissions;
	}
	public List<ServerPermission> getUserPermissions() {
		return userPermissions==null?(userPermissions=new ArrayList<>()):userPermissions;
	}
}
