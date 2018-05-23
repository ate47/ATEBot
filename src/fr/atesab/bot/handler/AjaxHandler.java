package fr.atesab.bot.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;
import fr.atesab.bot.handler.tools.ToolHandler;

public class AjaxHandler extends WebHandler {
	private List<ToolHandler> tools;

	public AjaxHandler(List<ToolHandler> toolsl) {
		this.tools = new ArrayList<>();
		this.tools.addAll(toolsl);
	}

	public ToolHandler getTool(String name) {
		for (ToolHandler tool : tools)
			if (tool.toolName().equalsIgnoreCase(name))
				return tool;
		return null;
	}

	public String handle(WebInformation info) throws IOException {
		Gson g = new GsonBuilder().create();
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("error", 0);
		content.put("msg", "");
		if (info.getPost().containsKey("type")) {
			ToolHandler tool = getTool((String) info.getPost().get("type"));
			if (tool != null) {
				if (((info.getAccount() != null && tool.needConnection()) || !tool.needConnection())
						&& ((info.getAccount().hasPerm(tool.neededPermission()) && tool.neededPermission() != null)
								|| (tool.neededPermission() == null))) {
					tool.ajax(info, content);
				} else {
					content.put("msg", "access denied");
					content.put("error", 2);
				}
			} else {
				content.put("msg", "unknow type");
				content.put("error", 1);
			}
		} else {
			content.put("msg", "no type");
			content.put("error", 1);
		}
		info.getResponse().write(g.toJson(content));
		return null;
	}

	public boolean needConnection() {
		return false;
	}

	public String neededPermission() {
		return null;
	}
}
