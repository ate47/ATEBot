package fr.atesab.web.tool;

import java.io.IOException;

import fr.atesab.bot.BotServer;
import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;
import fr.atesab.bot.WebToolInformation;
import fr.atesab.bot.handler.PanelHandler;
import fr.atesab.bot.handler.ToolsHandler;
import fr.atesab.bot.handler.tools.ToolHandler;

public class WebConfigHandler extends ToolsHandler {

	public WebConfigHandler(BotServer server) {
		super(server);
	}
	public String handle(WebInformation info) throws IOException {
		String s = "<table class='acc'>\n"
				+ "<tr>\n<td class='acc_tree acc_left'>\n"
				+ "<ul>\n";
		String title = info.getBotServer().getLanguage("webconfig");
		String title2 = "<a href='?'>"+info.getBotServer().getLanguage("webconfig")+"</a>";
		info.setTitle("webconfig");
		for (ToolHandler tool: getTools())
			if(info.getAccount().hasPerm(tool.neededPermission()))
				s+="<a href='?app="+tool.toolName()+"'><li class='acc_tree_tools'>"+info.getBotServer().getLanguage("webconfig."+tool.toolName())+"</li></a>\n";
		String content = null;
		WebHandler app;
		if(info.getGet().containsKey("app") && (app=getTool(info.getGet().get("app").toString().toLowerCase()))!=null) {
			content = info.getServlet().getHandler(app, new WebToolInformation(info, this));
			title+=" - "+info.getBotServer().getLanguage("webconfig."+info.getGet().get("app").toString().toLowerCase());
			title2+=" >> "+info.getBotServer().getLanguage("webconfig."+info.getGet().get("app").toString().toLowerCase());
		}
		info.setTitle(title);
		if(content==null) {
			content = "<p>"+info.getBotServer().getLanguage("tools.optList")+" : </p>\n"
					+ "\n<ul class='acc_tree'>\n";
			for (ToolHandler tool: getTools())
				if(info.getAccount().hasPerm(tool.neededPermission()))
					content+="<a href='?app="+tool.toolName()+"'><li class='acc_tree_tools'>"+info.getBotServer().getLanguage("webconfig."+tool.toolName())+"</li></a>\n";
				
			content+="</ul>\n</div>";
		}
		s+="</ul>"
				+ "</td>\n<td class='acc_body'><h3>"+title2+"</h3>\n"+content+"\n</td>\n</tr>\n</table>";
		return PanelHandler.buildPanel(s, info);
	}
	public boolean needConnection() {
		return true;
	}
	public String neededPermission() {
		return "webconfig";
	}
}
