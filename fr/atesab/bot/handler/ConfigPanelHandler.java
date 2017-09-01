package fr.atesab.bot.handler;

import java.io.IOException;

import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;

public class ConfigPanelHandler extends WebHandler {

	@Override
	public String handle(WebInformation info) throws IOException {
		
		return PanelHandler.buildPanel("", info);
	}

	@Override
	public String neededPermission() {
		return "config";
	}

	@Override
	public boolean needConnection() {
		return true;
	}

}
