package fr.atesab.bot.handler;

import java.io.IOException;

import fr.atesab.bot.Main;
import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;

public class PanelHandler extends WebHandler {
	public String handle(WebInformation info) throws IOException {
		return buildPanel("Hello", info);
	}
	public String neededPermission() {
		return null;
	}
	public boolean needConnection() {
		return true;
	}
	public static final String PANEL_NAME = "WebPanel";
	public static final String[][] NAV_BAR = {{"panel.home","index.ap", null},
			{"config","config.ap","config"},
			{"panel.users","users.ap","users"},
			{"tools","tools.ap","tools"}};
	public static String buildPanel(String text, WebInformation info) {
		String s = "<div id='header'>\n<table>\n<tr><td><a href='"+info.getHost()+"'><img src='"+info.getHost()+"/files/logo.png' alt='Logo de "+PANEL_NAME+"'/></a>"
				+ "</td><td>\n<div id='nav_bar'>\n";
		for (String[] links: NAV_BAR) {
			if(links.length==3) {
				if(info.getAccount().hasPerm(links[2])) {
					s+="<a href='"+links[1]+"'>"+Main.lang.getLangage(links[0])+"</a>";
				} else continue;
			}
		}
		s+="<a href='javascript:document.getElementById(\"decoform\").submit();'>"+Main.lang.getLangage("login.disconnect")+"</a><form id='decoform' method='post' action=''><input type='hidden' value='disconnect' name='action' /></form>";
		s+="</td></table></div>\n</div>\n<div id='body'>\n"+text+"\n</div>";
		return s;
	}
}
