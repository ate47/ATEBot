package fr.atesab.web.tool;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.io.IOException;

import fr.atesab.bot.BotServer;
import fr.atesab.bot.WebToolInformation;
import fr.atesab.bot.handler.tools.ToolHandler;

public class InfoConfigHandler extends ToolHandler {
	public String handle(WebToolInformation info) throws IOException {
		if(BotServer.mapContainNoEmptyKeys(info.getPost(), new String[] {"nfo_title","nfo_text"}) 
				&& BotServer.mapContainKeys(info.getPost(), new String[] {"nfo_img"})) {
			info.getBotServer().getNfoMessage().title = (String) info.getPost().get("nfo_title");
			info.getBotServer().getNfoMessage().text = (String) info.getPost().get("nfo_text");
			if(info.getPost().get("nfo_img")!=null)
				info.getBotServer().getNfoMessage().img = (String) info.getPost().get("nfo_img");
			else info.getBotServer().getNfoMessage().img = "";
			info.getBotServer().saveConfig();
			info.setNotification(info.getBotServer().getLanguage("webconfig.info.save"));
		}
		@SuppressWarnings("deprecation")
		String s = "<form method='POST' action=''>\n<table>\n"
				+ "<tr>\n<td>"+info.getBotServer().getLanguage("web.blog.edit.title")+" : </td>\n"
						+ "<td><input type='text' value='"+escapeHtml4(info.getBotServer().getNfoMessage().title).replaceAll("\\'", "&apos;")+"' name='nfo_title' /></td>\n</tr>\n"
				+ "<tr>\n<td>"+info.getBotServer().getLanguage("web.blog.edit.img")+" : </td>\n"
						+ "<td><input type='text' value='"+escapeHtml4(info.getBotServer().getNfoMessage().img).replaceAll("\\'", "&apos;")+"' name='nfo_img' /></td>\n</tr>\n"
				+ "</table>\n<p>"+info.getBotServer().getLanguage("web.blog.edit.content")+" : </p>\n"
				+ "<textarea class='blog_message_edit' name='nfo_text'>"+escapeHtml4(info.getBotServer().getNfoMessage().text)+"</textarea>\n"
				+ "<p><input type='submit' value='"+info.getBotServer().getLanguage("web.blog.edit.save")+"' /></p>\n</form>";
		
		return s;
	}
	public boolean needConnection() {
		return true;
	}
	public String neededPermission() {
		return "info";
	}
	public String toolName() {
		return "info";
	}

}
