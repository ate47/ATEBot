package fr.atesab.bot.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import fr.atesab.bot.Main;
import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;
import fr.atesab.bot.handler.tools.ToolHandler;

public class ToolsHandler extends WebHandler {
	private Map<String, ToolHandler> tools = new HashMap<String, ToolHandler>();
	public String handle(WebInformation info) throws IOException {
		String s = "<table class='acc'>\n"
				+ "<tr>\n<td class='acc_tree acc_left'>\n"
				+ "<ul>\n";
		String title = Main.lang.getLangage("tools");
		String title2 = "<a href='?'>"+Main.lang.getLangage("tools")+"</a>";
		info.setTitle("tools");
		for (String key: tools.keySet()) {
			if(info.getAccount().hasPerm(tools.get(key).neededPermission()))
				s+="<a href='?app="+key+"'><li class='acc_tree_tools'>"+Main.lang.getLangage("tools."+key)+"</li></a>\n";
		}
		String content = null;
		if(info.getGet().containsKey("app") && tools.containsKey(info.getGet().get("app").toString().toLowerCase())) {
			WebHandler handler = tools.get(info.getGet().get("app").toString().toLowerCase());
			content = info.getServlet().getHandler(handler, info);
			title+=" - "+Main.lang.getLangage("tools."+info.getGet().get("app").toString().toLowerCase());
			title2+=" >> "+Main.lang.getLangage("tools."+info.getGet().get("app").toString().toLowerCase());
		}
		info.setTitle(title);
		if(content==null) {
			content = "<p>"+Main.lang.getLangage("tools.optList")+" : </p>\n"
					+ "\n<ul class='acc_tree'>\n";
			for (String key: tools.keySet())
				if(info.getAccount().hasPerm(tools.get(key).neededPermission()))
					content+="<a href='?app="+key+"'><li class='acc_tree_tools'>"+Main.lang.getLangage("tools."+key)+"</li></a>\n";
				
			content+="</ul>\n</div>";
		}
		s+="</ul>"
				+ "</td>\n<td class='acc_body'><h3>"+title2+"</h3>\n"+content+"\n</td>\n</tr>\n</table>";
		return PanelHandler.buildPanel(s, info);
	}
	public String neededPermission() {
		return "tools";
	}
	public boolean needConnection() {
		return true;
	}
	public ToolsHandler registerTool(ToolHandler... tools) {
		for (ToolHandler tool: tools)
			this.tools.put(tool.toolName().toLowerCase(), tool);
		this.tools = new TreeMap<String, ToolHandler>(this.tools);
		return this;
	}
}
