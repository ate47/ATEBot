package fr.atesab.bot.handler.tools;

import java.io.IOException;

import fr.atesab.bot.WebToolInformation;

public class LocalPermissionHandler extends ToolHandler {
	public static final String TOOL_NAME = "localperm";
	@Override
	public String handle(WebToolInformation info) throws IOException {
		String s = "test";
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
		return TOOL_NAME;
	}

}
