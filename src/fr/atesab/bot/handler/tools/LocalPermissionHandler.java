package fr.atesab.bot.handler.tools;

import java.io.IOException;

import fr.atesab.bot.WebToolInformation;

public class LocalPermissionHandler extends ToolHandler {
	@Override
	public String handle(WebToolInformation info) throws IOException {
		String s = "";
		return s;
	}
	@Override
	public boolean needConnection() {
		return true;
	}
	@Override
	public String neededPermission() {
		return "localperm";
	}
	@Override
	public String toolName() {
		return "perm";
	}

}
