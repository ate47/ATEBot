package fr.atesab.bot.handler.tools;

import java.io.IOException;
import java.util.Map;

import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;
import fr.atesab.bot.WebToolInformation;

public abstract class ToolHandler extends WebHandler {
	public void ajax(WebInformation servlet, Map<String,Object> map){ }
	@Override
	public String handle(WebInformation info) throws IOException {
		return handle((info instanceof WebToolInformation)?((WebToolInformation)info):new WebToolInformation(info, null));
	}
	public abstract String handle(WebToolInformation info) throws IOException;
	public abstract String toolName();
	@Override
	public String toString() {
		return "ToolHandler [toolName()=" + toolName() + ", needConnection()=" + needConnection()
				+ ", neededPermission()=" + neededPermission() + "]";
	}
	
	
}
