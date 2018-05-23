package fr.atesab.bot.handler;

import java.io.IOException;

import fr.atesab.bot.BotServer;
import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;

public class ConfigPanelHandler extends WebHandler {

	public static double getMegaValue(long value) {
		long l = value / (1024L * 1024L) * 100L;
		return l / 100D;
	}
	@Override
	public String handle(WebInformation info) throws IOException {
		long maxMemory = Runtime.getRuntime().maxMemory();
		long memorySize = maxMemory - Runtime.getRuntime().freeMemory();
		info.setTitle(info.getBotServer().getLanguage("config"));
		return PanelHandler.buildPanel("<div class='acc_body'>\n<h2>"+info.getTitle()+"</h2>\n<table id='table_list'>"
				+ "\n<tr><td>"+info.getBotServer().getLanguage("config.mem")+" : </td>"
				+ "<td>"+getMegaValue(memorySize)+" / "+getMegaValue(maxMemory)+" "+info.getBotServer().getLanguage("config.megabyte")+"</td></tr>"
				+ "\n<tr><td>"+info.getBotServer().getLanguage("config.bot")+" : </td><td>"+info.getBotServer().getInstances().size()+"</td></tr>"
				+ "\n<tr><td>"+info.getBotServer().getLanguage("config.version")+" : </td><td>"+BotServer.BOT_VERSION+"</td></tr>"
				+ "\n<tr><td>"+info.getBotServer().getLanguage("config.language")+" : </td><td>"+info.getBotServer().getLang().getLangName()+" ("+info.getBotServer().getLanguage("html_lang")+")</td></tr>"
				+ "\n</table></div>", info);
	}
	@Override
	public boolean needConnection() {
		return true;
	}

	@Override
	public String neededPermission() {
		return "config";
	}

}
