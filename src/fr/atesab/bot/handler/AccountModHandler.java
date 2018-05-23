package fr.atesab.bot.handler;

import java.io.IOException;

import fr.atesab.bot.BotServer;
import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;

public class AccountModHandler extends WebHandler {
	public String handle(WebInformation info) throws IOException {
		if(BotServer.mapContainNoEmptyKeys(info.getPost(), new String[] {"psu_mod_ps1","psu_mod_ps2","psu_mod_ps3"})) {
			String pass1 = BotServer.MD5((String)info.getPost().get("psu_mod_ps1"));
			if(info.getAccount().hash.equals(pass1)) {
				String pass2 = BotServer.MD5((String)info.getPost().get("psu_mod_ps2"));
				String pass3 = BotServer.MD5((String)info.getPost().get("psu_mod_ps3"));
				if(pass2.equals(pass3)) {
					info.getAccount().hash = pass2;
					info.getSessions().put("log_ps", pass2);
					info.setNotification(info.getBotServer().getLanguage("panel.users.error.create.pw"));
					info.getBotServer().saveConfig();
				} else {
					info.setNotification(info.getBotServer().getLanguage("panel.users.msg.ps"));
				}
			} else {
				info.setNotification(info.getBotServer().getLanguage("panel.users.error.create.pw2"));
			}
		}
		info.setTitle(info.getBotServer().getLanguage("panel.usermod"));
		String s = "<div class='acc_body'><h2>"+info.getTitle()+"</h2>"
				+ "\n<form method='POST' action=''>\n<table>"
				+ "\n<tr><td>"+info.getBotServer().getLanguage("panel.password")+" : </td><td><input type='password' name='psu_mod_ps1' /></td></tr>"
				+ "\n<tr><td>"+info.getBotServer().getLanguage("panel.nPassword")+" : </td><td><input type='password' name='psu_mod_ps2' /></td></tr>"
				+ "\n<tr><td>"+info.getBotServer().getLanguage("panel.nPassword2")+" : </td><td><input type='password' name='psu_mod_ps3' /></td></tr>"
				+ "\n</table>\n<input type='submit' value='"+info.getBotServer().getLanguage("panel.save")+"' />\n</form></div>";
		return PanelHandler.buildPanel(s, info);
	}
	public boolean needConnection() {
		return true;
	}
	public String neededPermission() {
		return null;
	}

}
