package fr.atesab.bot.handler.tools;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import fr.atesab.bot.DiscordListener;
import fr.atesab.bot.WebToolInformation;
import fr.atesab.bot.command.Command;

@SuppressWarnings("deprecation")
public class CommandListHandler extends ToolHandler {
	public String handle(WebToolInformation info) throws IOException {
		String s = "<table class='table_list'>\n<tr class='table_list_top'><td>"+info.getBotServer().getLanguage("tools.cmdlist.name")+"</td>"
				+ "<td>"+info.getBotServer().getLanguage("tools.cmdlist.usage")+"</td>"
				+ "<td>"+info.getBotServer().getLanguage("tools.cmdlist.title")+"</td>"
				+ "<td>"+info.getBotServer().getLanguage("tools.cmdlist.perm")+"</td></tr>";
		for (Command cmd: info.getBotServer().getCommands()) {
			if(!info.getAccount().hasPerm(cmd.neededPermission())) continue;
			s+="\n<tr><td>"+DiscordListener.DEFAULT_COMMAND_PREFIX+cmd.getName()+"</td>"
					+ "<td>"+StringEscapeUtils.escapeHtml4(DiscordListener.DEFAULT_COMMAND_PREFIX+cmd.getUsage()).replaceAll("(\\\\n)|(\\\\r)", "<br />")+"</td>"
					+ "<td>"+info.getBotServer().getLanguage("cmd."+cmd.getName())+"</td>"
					+ "<td>"+info.getBotServer().getLanguage("perm."+cmd.neededPermission())+"</td></tr>";
		}
		return s+"\n</table>";
	}
	public boolean needConnection() {
		return true;
	}
	public String neededPermission() {
		return "cmdlist";
	}
	public String toolName() {
		return "cmdlist";
	}

}
