package fr.atesab.web;

import java.io.IOException;

import fr.atesab.bot.Account;
import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;

public class UserInfoHandler extends WebHandler {
	public String handle(WebInformation info) throws IOException {
		String name = null;
		String s = "";
		Object user;
		Account acc;
		if((user=info.getGet().getOrDefault("u", null))!=null && (acc=info.getBotServer().getAccountByName((String)user))!=null) {
			s+="<h2>"+acc.name+"</h2>";
		}
		return IndexHandler.buildIndex(s, name, info);
	}
	public boolean needConnection() {
		return false;
	}
	public String neededPermission() {
		return null;
	}
}
