package fr.atesab.bot.handler.tools;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import fr.atesab.bot.DListener;
import fr.atesab.bot.Main;
import fr.atesab.bot.WebInformation;
import fr.atesab.bot.command.Command;

public class CommandListHandler extends ToolHandler {
	public String handle(WebInformation info) throws IOException {
		String s = "<table class='table_list'>\n<tr class='table_list_top'><td>"+Main.lang.getLangage("tools.cmdlist.name")+"</td>"
				+ "<td>"+Main.lang.getLangage("tools.cmdlist.usage")+"</td>"
				+ "<td>"+Main.lang.getLangage("tools.cmdlist.title")+"</td>"
				+ "<td>"+Main.lang.getLangage("tools.cmdlist.perm")+"</td></tr>";
		for (Command cmd: Main.commands) {
			s+="\n<tr><td>"+DListener.COMMAND_PREFIX+cmd.getName()+"</td>"
					+ "<td>"+StringEscapeUtils.escapeHtml4(DListener.COMMAND_PREFIX+cmd.getUsage())+"</td>"
					+ "<td>"+Main.lang.getLangage("cmd."+cmd.getName())+"</td>"
					+ "<td>"+Main.lang.getLangage("perm."+cmd.neededPermission())+"</td></tr>";
		}
		return s+"\n</table>";
	}
	public String neededPermission() {
		return "cmdlist";
	}
	public boolean needConnection() {
		return true;
	}
	public String toolName() {
		return "cmdlist";
	}

}
