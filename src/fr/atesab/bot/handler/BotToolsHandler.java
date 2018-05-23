package fr.atesab.bot.handler;

import java.io.IOException;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.BotServer;
import fr.atesab.bot.WebInformation;
import fr.atesab.bot.WebToolInformation;
import fr.atesab.bot.handler.tools.ToolHandler;
import sx.blah.discord.handle.obj.IGuild;

public class BotToolsHandler extends ToolsHandler {
	public BotToolsHandler(BotServer server) {
		super(server);
	}
	public String handle(WebInformation info) throws IOException {
		String s = "<table class='acc'>\n"
				+ "<tr>\n<td class='acc_tree acc_left'>\n"
				+ "<ul>\n<a href='?'><li class='acc_tree_addbot'>"+info.getBotServer().getLanguage("tools.createbot")+"</li></a>\n";
		String title = info.getBotServer().getLanguage("tools");
		String title2 = "<a href='?'>"+info.getBotServer().getLanguage("tools")+"</a>";
		info.setTitle("tools");
		String content = "";
		if(info.getBotInstance()==null) {
			if(BotServer.mapContainNoEmptyKeys(info.getPost(), new String[] {"bot_name","bot_token"})) {
				String name = (String)info.getPost().get("bot_name");
				if(info.getBotServer().getBotInstanceByName(name)==null) {
					if(name.matches("[a-zA-Z0-9 ]+")) {
						BotInstance instance = new BotInstance(info.getBotServer(), name, (String)info.getPost().get("bot_token"));
						info.getBotServer().getInstances().add(instance);
						info.getBotServer().startClients();
						info.getBotServer().saveConfig();
						info.setBotInstance(instance);
						info.getSessions().put("botId", String.valueOf(info.getBotServer().getInstances().size()-1));
					} else {
						info.setNotification(info.getBotServer().getLanguage("tools.createbot.error"));
					}
				} else {
					info.setNotification(info.getBotServer().getLanguage("tools.createbot.error.already"));
				}
			}
			title+=" - "+info.getBotServer().getLanguage("tools.botList");
			if(info.getAccount().hasPerm("createbot"))
				content = "<p>"+info.getBotServer().getLanguage("tools.createbot")+" : </p>\n"
						+ "<form method='POST' action=''><table>\n"
						+ "<tr><td>"+info.getBotServer().getLanguage("tools.createbot.name")+"</td><td><input type='text' name='bot_name' value='' /></td></tr>\n"
						+ "<tr><td>"+info.getBotServer().getLanguage("tools.createbot.token")+"</td><td><input type='text' name='bot_token' value='' /></td></tr>\n"
						+ "</table>\n<input type='submit' value='"+info.getBotServer().getLanguage("tools.createbot")+"' /> </form>";
		}
		for (int i = 0; i < info.getBotServer().getInstances().size(); i++) {
			BotInstance instance = info.getBotServer().getInstances().get(i);
			if(info.getAccount().hasBotAccess(instance.getConfig().getName()))
				s+="<a href='?botid="+i+"'><li class='acc_tree_bot'>"+instance.getConfig().getName()+"</li></a>\n";
		}
		if(info.getBotInstance()!=null) {
			title2+=" >> "+info.getBotInstance().getConfig().getName();
			s+="<hr />";
			if(info.getBotInstance().getClient()!=null)
				for (IGuild g: info.getBotInstance().getClient().getGuilds()) {
					try {
						if(info.getAccount().hasServerAccess(info.getBotInstance().getConfig().getName(), g.getLongID()))
							s+="<a href='?botid="+info.getBotId()+"&serverid="+g.getLongID()+"'><li class='acc_tree_server'>"+g.getName()+"</li></a>\n";
					} catch (Exception e) {}
				}
			if(info.getServerConfig()==null) {
				if(info.getBotInstance().getClient()!=null)
				content = "<p><a href='https://discordapp.com/oauth2/authorize?client_id="+info.getBotInstance().getClient().getApplicationClientID()+"&scope=bot&permissions=0'>"
						+ info.getBotServer().getLanguage("tools.createbot.addtoserver")+"</a></p>";
				if(info.getAccount().hasPerm("deletebot"))
					content+= "<p><a href='tools.ap?deletebot="+info.getBotInstance().getConfig().getName()+"'>"
							+ info.getBotServer().getLanguage("tools.createbot.delete")+"</a></p>";
			} else if(info.getAccount().hasServerAccess(info.getBotInstance().getConfig().getName(), info.getServer().getLongID())) {
				if(info.getBotInstance().getClient()!=null) {
					title2+=" >> "+info.getServer().getName();
					ToolHandler app;
					if(info.getGet().containsKey("app")) {
						String appName = info.getGet().get("app").toString();
						if((info.getServerConfig().tools.contains(appName) || appName.equals("serverconfig")) && 
								(app=getTool(appName))!=null) {
							content = info.getServlet().getHandler(app, new WebToolInformation(info, this));
							title+=" - "+info.getBotServer().getLanguage("tools."+info.getGet().get("app").toString().toLowerCase());
							title2+=" >> "+info.getBotServer().getLanguage("tools."+info.getGet().get("app").toString().toLowerCase());
						}
					}
					s+="<hr />";
					for (ToolHandler tool: getTools()) {
						if((info.getServerConfig().tools.contains(tool.toolName()) || tool.toolName().equalsIgnoreCase("serverconfig"))
								&& info.getAccount().hasPerm(tool.neededPermission()))
							s+="<a href='"+info.getCurrentLink()+"&app="+tool.toolName()+"'><li class='acc_tree_tools'>"+info.getBotServer().getLanguage("tools."+tool.toolName())+"</li></a>\n";
					}
					info.setTitle(title);
					if(content==null || content.isEmpty()) {
						content = "<p>"+info.getBotServer().getLanguage("tools.optList")+" : </p>\n"
								+ "\n<ul class='acc_tree'>\n";
						for (ToolHandler tool: getTools())
							if((info.getServerConfig().tools.contains(tool.toolName()) || tool.toolName().equalsIgnoreCase("serverconfig")) 
									&& info.getAccount().hasPerm(tool.neededPermission()))
								content+="<a href='"+info.getCurrentLink()+"&app="+tool.toolName()+"'><li class='acc_tree_tools'>"+info.getBotServer().getLanguage("tools."+tool.toolName())+"</li></a>\n";
							
						content+="</ul>\n</div>";
					}
				}
			}
		}
		s+="</ul>"
				+ "</td>\n<td class='acc_body'><h3>"+title2+"</h3>\n"+content+"\n</td>\n</tr>\n</table>";
		return PanelHandler.buildPanel(s, info);
	}
	public boolean needConnection() {
		return true;
	}
	public String neededPermission() {
		return "tools";
	}
}
