package fr.atesab.bot.handler.tools;

import java.io.IOException;

import fr.atesab.bot.BotServer;
import fr.atesab.bot.WebToolInformation;
import fr.atesab.bot.handler.AccountsPanelHandler;

public class ConfigServerHandler extends ToolHandler {

	@Override
	public String handle(WebToolInformation info) throws IOException {
		if (info.getPost().containsKey("cs_mod")) {
			info.getServerConfig().tools.clear();
			for (ToolHandler tool : info.getToolsHandler().getTools()) {
				if (!tool.toolName().equals(this.toolName())
						&& info.getPost().getOrDefault(tool.toolName(), "off").toString().equals("on"))
					info.getServerConfig().tools.add(tool.toolName());
			}
			for (String name : BotServer.APPS)
				if (info.getPost().getOrDefault(name, "off").toString().equals("on"))
					info.getServerConfig().tools.add(name);
			info.getBotServer().saveConfig();
			info.setNotification(info.getBotServer().getLanguage("tools.serverconfig.save"));
		}
		String content = "<form action='' method='post'>\n<input type='hidden' name='cs_mod' value='true' />\n";
		for (ToolHandler tool : info.getToolsHandler().getTools())
			if (!tool.toolName().equals(this.toolName())) {
				content += "\n<input type='checkbox' name='" + tool.toolName() + "' "
						+ AccountsPanelHandler.check(info.getServerConfig().tools.contains(tool.toolName())) + " />"
						+ " - " + info.getBotServer().getLanguage("tools." + tool.toolName()) + "<br />";
			}
		content += "\n<hr />";
		for (String name : BotServer.APPS)
			content += "\n<input type='checkbox' name='" + name + "' "
					+ AccountsPanelHandler.check(info.getServerConfig().tools.contains(name)) + " />" + " - "
					+ info.getBotServer().getLanguage("tools." + name) + "<br />";
		return content + "\n<p><input type='submit' value='" + info.getBotServer().getLanguage("panel.save")
				+ "' /></p></form>";
	}

	@Override
	public boolean needConnection() {
		return true;
	}

	@Override
	public String neededPermission() {
		return "configserver";
	}

	@Override
	public String toolName() {
		return "serverconfig";
	}

}
