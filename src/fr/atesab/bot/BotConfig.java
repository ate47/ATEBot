package fr.atesab.bot;

import java.util.HashMap;
import java.util.Map;

import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.obj.ActivityType;

public class BotConfig {
	private String name;
	private String token = "";
	private String playinformation;
	private ActivityType activityType = ActivityType.PLAYING;
	private Map<String, ServerConfig> config = new HashMap<String, ServerConfig>();
	public BotConfig(String name,String token) {
		this.token = token;
		this.name = name;
	}
	public BotConfig(String name,String token, Map<String, ServerConfig> config) {
		this.token = token;
		this.name = name;
		this.config = config;
	}
	public ActivityType getActivityType() {
		return activityType;
	}
	public Map<String, ServerConfig> getConfig() {
		return config;
	}
	public String getName() {
		return name;
	}
	public String getPlayinformation() {
		return playinformation;
	}
	public String getToken() {
		return token;
	}
	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}
	public void setPlayinformation(String playinformation) {
		this.playinformation = playinformation;
	}
}