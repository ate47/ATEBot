package fr.atesab.bot.handler;

import java.io.IOException;

import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;

public class MessageHandler extends WebHandler {
	private String message;
	private String title;
	public MessageHandler(String message, String title){
		this.message = message;
		this.title = title;
	}
	public String neededPermission(){return null;}
	public boolean needConnection(){return false;}
	public String handle(WebInformation info) throws IOException {
		info.setTitle(title);
		return PanelHandler.buildPanel(message, info);
		}

}
