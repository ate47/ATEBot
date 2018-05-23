package fr.atesab.bot.handler;

import java.io.IOException;

import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;

public class LoginHandler extends WebHandler {
	public String icon;
	public LoginHandler(String icon) {
		this.icon = icon;
	}
	public String handle(WebInformation info) throws IOException {
		info.setTitle("panel.login");
		return "<div id='login_window_header'>\n<img src='"+info.getHost()+"/files/"+icon+"' alt='logo du bot'/>"
			+ "</div>\n"
			+ "<div id='login_window'>\n<form method='POST' action=''>\n"
			+ "<table>\n"
			+ "<tr><td>"+info.getBotServer().getLanguage("login.username")+" : </td><td><input type='text' name='log_us' /></td></tr>\n"
			+ "<tr><td>"+info.getBotServer().getLanguage("login.password")+" : </td><td><input type='password' name='log_ps' /></td></tr>\n"
			+ "</table>"
			+ "\n<input type='submit' value='"+info.getBotServer().getLanguage("login.connect")+"' />\n</form>\n</div>";
	}
	public boolean needConnection() {
		return false;
	}
	public String neededPermission() {
		return null;
	}
}
