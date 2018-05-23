package fr.atesab.bot.handler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.atesab.bot.BotServer;
import fr.atesab.bot.WebHandler;
import fr.atesab.bot.handler.tools.ToolHandler;

public abstract class ToolsHandler extends WebHandler {
	private List<ToolHandler> tools;
	protected BotServer server;
	public ToolsHandler(BotServer server) {
		this(server, new ArrayList<ToolHandler>());
	}
	public ToolsHandler(BotServer server, List<ToolHandler> tools) {
		this.tools = tools;
		this.server = server;
	}
	public ToolHandler getTool(String name) {
		for (ToolHandler tool: tools)
			if(tool.toolName().equalsIgnoreCase(name)) return tool;
		return null;
	}
	public List<ToolHandler> getTools(){
		return tools;
	}
	public ToolsHandler registerTool(ToolHandler... tools) {
		for (ToolHandler tool: tools)
			this.tools.add(tool);
		this.tools.sort(new Comparator<ToolHandler>() {
			@Override
			public int compare(ToolHandler o1, ToolHandler o2) {
				return server.getLanguage("tools."+o1.toolName()).compareToIgnoreCase(server.getLanguage("tools."+o2.toolName()));
			}
		});
		return this;
	}
}
