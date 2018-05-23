package fr.atesab.bot.handler.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.WebInformation;
import fr.atesab.bot.WebToolInformation;
import fr.atesab.bot.utils.AudioProvider;

public class MusicToolHandler extends ToolHandler {
	private static final Gson gson = new Gson();
	@Override
	public void ajax(WebInformation info, Map<String, Object> map) {
		if(map.containsKey("serverid") && map.containsKey("bot")) {
			BotInstance botInstance;
			AudioProvider audioPlayer;
			try {
				botInstance = info.getBotServer().getBotInstanceByName((String) map.get("bot"));
				audioPlayer = botInstance.getListener().getAudioPlayers().get(Long.valueOf((String) map.get("serverid")));
			} catch (Exception e) {
				map.put("error", 1);
				map.put("msg", "Unknow bot or server");
				return;
			}
			if(map.containsKey("app")) {
				switch ((String) map.get("app")) {
				case "queue":
					List<String> queue = new ArrayList<>();
					if(audioPlayer.getAudioPlayer().getPlayingTrack()!=null) {
						queue.add(gson.toJson(audioPlayer.getAudioPlayer().getPlayingTrack().getInfo()));
					}
					for (AudioTrack track: audioPlayer.getQueue())
						queue.add(gson.toJson(track.getInfo()));
					map.put("queue", queue);
					break;
				case "current":
					AudioTrack track = audioPlayer.getAudioPlayer().getPlayingTrack();
					if(track!=null) {
						map.put("current", track.getInfo());
						map.put("position", audioPlayer.getAudioPlayer().getPlayingTrack().getPosition());
						map.put("vol", audioPlayer.getAudioPlayer().getVolume());
					}
					break;
				default:
					map.put("error", 1);
					map.put("msg", "Unknow app");
					break;
				}
			} else {
				map.put("error", 1);
				map.put("msg", "No app");
			}
		} else {
			map.put("error", 1);
			map.put("msg", "No serverid and bot");
		}
	}
	public String handle(WebToolInformation info) throws IOException {
		String s = "";
		
		return s;
	}
	public boolean needConnection() {
		return true;
	}
	public String neededPermission() {
		return "audiogest";
	}
	public String toolName() {
		return "audio";
	}

}
