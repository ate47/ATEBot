package fr.atesab.bot.handler.tools;

import java.io.IOException;

import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;

public abstract class ToolHandler extends WebHandler {
	public abstract String handle(WebInformation info) throws IOException;
	public abstract String neededPermission();
	public abstract String toolName();
	public abstract boolean needConnection();
}
