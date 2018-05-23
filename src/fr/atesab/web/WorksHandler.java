package fr.atesab.web;

import java.io.IOException;

import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;

public class WorksHandler extends WebHandler {
	public String handle(WebInformation info) throws IOException {
		String s = "<div class='acc_body'><h2>"+info.getBotServer().getLanguage("web.works")+"</h2>\n<table>\n";
		for (int i = info.getBotServer().getwMessages().size()-1; i >= 0; i--) {
			WorkMessage w = info.getBotServer().getwMessages().get(i);
			s+="<tr id='w"+i+"'>\n<td>";
			if(w.img!=null && !w.img.isEmpty())s+="<img class='work_image' src='"+w.img+"' alt='"+w.title+"' />";
			s+="</td>\n<td>\n<h3>"+w.title+"</h3>\n<p>"+w.text+"</p>\n"
					+ "<p>"+info.getBotServer().getLanguage("web.works.author")+" : "+IndexHandler.getAccount(w.author, info)+"</p>"
					+ "<p><a href='"+w.link+"' title='"+info.getBotServer().getLanguage("web.works.link")+"'>"+info.getBotServer().getLanguage("web.works.link")+"</a></p>"
					+ "\n</td>\n</tr>";
		}
		s+="</table></div>";
		return IndexHandler.buildIndex(s, info.getBotServer().getLanguage("web.works"), info);
	}
	public boolean needConnection() {
		return false;
	}
	public String neededPermission() {
		return null;
	}
	
}
