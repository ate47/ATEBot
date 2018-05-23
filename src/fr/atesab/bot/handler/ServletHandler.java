package fr.atesab.bot.handler;

import java.io.IOException;

import fr.atesab.bot.BotServlet;
import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;

public class ServletHandler extends WebHandler {
	private BotServlet servlet;
	public ServletHandler(BotServlet servlet) {
		this.servlet = servlet;
	}
	public String handle(WebInformation info) throws IOException {
		if(info.getContext().length()>servlet.getPath().length()) {
			info.setContext(info.getContext().substring(servlet.getPath().length()));
			info.setServlet(servlet);
			return servlet.servletProcessor(info);
		} else {
			return info.getServlet().getDefaultContext().handle(info);
		}
	}
	public boolean needConnection() {
		return false;
	}
	public String neededPermission() {
		return null;
	}

}
