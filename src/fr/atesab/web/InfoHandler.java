package fr.atesab.web;

import java.io.IOException;

import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;

public class InfoHandler extends WebHandler {
	public String handle(WebInformation info) throws IOException {
		String s = "<div class='acc_body'>\n<h2>"+info.getBotServer().getNfoMessage().title+"</h2>\n<table>\n<tr>\n";
		if(info.getBotServer().getNfoMessage().img!=null && !info.getBotServer().getNfoMessage().img.isEmpty())
			s+="<td><img alt='"+info.getBotServer().getNfoMessage().title+"' src='"+info.getBotServer().getNfoMessage().img+"'/></td>\n";
		s+="<td>"+info.getBotServer().getNfoMessage().text+"</td>\n</tr>\n<tr>\n</table>\n</div>";
		return IndexHandler.buildIndex(s, info.getBotServer().getLanguage("web.info"), info);
	}
	public boolean needConnection() {
		return false;
	}
	public String neededPermission() {
		return null;
	}
}
