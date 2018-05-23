package fr.atesab.bot.handler;

import java.io.IOException;

import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;

public class PanelHandler extends WebHandler {
	public static final String PANEL_NAME = "WebPanel";
	public static final String[][] NAV_BAR = {{"panel.home","index.ap", null},
			{"config","config.ap","config"},
			{"panel.users","users.ap","users"},
			{"app","apps.ap","app"},
			{"webconfig","wconfig.ap","panel.webconfig"},
			{"tools","tools.ap","tools"},
			{"panel.usermod","usermod.ap",null}};
	public static String buildPanel(String text, WebInformation info) {
		String s = "<div id='header'>\n<table>\n<tr><td><a href='"+info.getHost()+"'><img src='"+info.getHost()+"/files/logo.png' alt='"+PANEL_NAME+"'/></a>"
				+ "</td><td>\n<div id='nav_bar'>\n";
		for (String[] links: NAV_BAR) {
			if(links.length==3) {
				if(info.getAccount().hasPerm(links[2])) {
					s+="<a href='"+links[1]+"'>"+info.getBotServer().getLanguage(links[0])+"</a>";
				} else continue;
			}
		}
		s+="<a href='javascript:document.getElementById(\"decoform\").submit();'>"+info.getBotServer().getLanguage("login.disconnect")+"</a><form id='decoform' method='post' action=''><input type='hidden' value='disconnect' name='action' /></form>";
		s+="</td></table></div>\n</div>\n<div id='body'>\n"+text+"\n</div>";
		return s;
	}
	public String handle(WebInformation info) throws IOException {
		String s = "<h2>"+info.getBotServer().getLanguage("panel.home")+"</h2>\n<p>"+info.getBotServer().getLanguage("panel.home.hello", info.getAccount().name)+"</p>\n"+info.getBotServer().getLanguage("panel.home.menu")+" : <br />\n"
				+ "<ul class='acc_tree'>\n";
		for (String[] links: NAV_BAR) {
			if(links.length==3) {
				if(info.getAccount().hasPerm(links[2])) {
					s+="<a href='"+links[1]+"'><li class='acc_tree_app'>"+info.getBotServer().getLanguage(links[0])+"</li></a>";
				} else continue;
			}
		}
		s+="</li>";
		return buildPanel("<div class='acc_body'>" + s + "</div>", info);
	}
	public boolean needConnection() {
		return true;
	}
	public String neededPermission() {
		return null;
	}
}
