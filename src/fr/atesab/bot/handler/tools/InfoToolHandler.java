package fr.atesab.bot.handler.tools;

import java.io.IOException;

import fr.atesab.bot.BotServer;
import fr.atesab.bot.WebInformation;
import fr.atesab.bot.WebToolInformation;

public class InfoToolHandler extends ToolHandler {
	public String getElement(WebInformation info, String name, boolean escape, String value) {
		String s = "<p id=\""+name+"\"> - <input type=\"text\" value=\""+value+"\" name=\""+name+"\" /> - "
				+ "<a href=\"javascript:deleteBlock(";
		if(escape)s+="\\";
			s+="'"+name+"";
		if(escape)s+="\\";
		return s+"')\">"+info.getBotServer().getLanguage("tools.messagetool.del")+"</a></p>";
	}
	public String handle(WebToolInformation info) throws IOException {
		if(BotServer.mapContainNoEmptyKeys(info.getPost(), new String[] {"banUID","ksUID","kcUID"})) {
			try {
				int bn = Integer.valueOf((String)info.getPost().get("banUID"));
				int ks = Integer.valueOf((String)info.getPost().get("ksUID"));
				int kc = Integer.valueOf((String)info.getPost().get("kcUID"));
				info.getServerConfig().banMessage.clear();
				info.getServerConfig().kickChannelMessage.clear();
				info.getServerConfig().kickServerMessage.clear();
				for (int i = 0; i < bn; i++)
					if(info.getPost().containsKey("mt_ban_"+i))
						info.getServerConfig().banMessage.add((String)info.getPost().get("mt_ban_"+i));
				for (int i = 0; i < ks; i++)
					if(info.getPost().containsKey("mt_ks_"+i))
						info.getServerConfig().kickServerMessage.add((String)info.getPost().get("mt_ks_"+i));
				for (int i = 0; i < kc; i++)
					if(info.getPost().containsKey("mt_kc_"+i))
						info.getServerConfig().kickChannelMessage.add((String)info.getPost().get("mt_kc_"+i));
			}catch (Exception e) {}
		}
		String s = "<script type='text/javascript'>\n"
				+ "var banUID = "+(info.getServerConfig().banMessage.size()+1)+";\n"
				+ "var ksUID = "+(info.getServerConfig().kickServerMessage.size()+1)+";\n"
				+ "var kcUID = "+(info.getServerConfig().kickChannelMessage.size()+1)+";\n"
				+ "function addBan(){"
				+ "  document.getElementById('mt_ban_list').insertAdjacentHTML('beforeend','"+getElement(info, "mt_ban_'+(banUID)+'", true, "")+"');banUID+=1;"
					+ "  document.getElementById('banUID').value=banUID;"
				+ "}\n"
				+ "function addKS(){"
				+ "  document.getElementById('mt_ks_list').insertAdjacentHTML('beforeend','"+getElement(info, "mt_ks_'+(ksUID)+'", true, "")+"');ksUID+=1;"
					+ "  document.getElementById('ksUID').value=ksUID;"
				+ "}\n"
				+ "function addKC(){"
				+ "  document.getElementById('mt_kc_list').insertAdjacentHTML('beforeend','"+getElement(info, "mt_kc_'+(kcUID)+'", true, "")+"');kcUID+=1;"
					+ "  document.getElementById('kcUID').value=kcUID;"
				+ "}\n</script>";
		//ban
		s+="<h3>"+info.getBotServer().getLanguage("tools.messagetool.ban")+"</h3>\n"
			+ "<form method='POST' action=''>\n<div id='mt_ban_list'>\n"
			+ "<input type='hidden' value='"+(info.getServerConfig().banMessage.size()+1)+"' name='banUID' id='banUID' />\n";
		for (int i = 0; i < info.getServerConfig().banMessage.size(); i++) {
			String msg = info.getServerConfig().banMessage.get(i);
			s+=getElement(info, "mt_ban_"+i, false, msg);
		}
		s+="</div>\n<p><a href='javascript:addBan()'>"+info.getBotServer().getLanguage("tools.messagetool.add")+"</a></p>\n<hr />";
		//ks
		s+="<h3>"+info.getBotServer().getLanguage("tools.messagetool.kick.server")+"</h3>\n"
			+ "<div id='mt_ks_list'>\n"
				+ "<input type='hidden' value='"+(info.getServerConfig().kickServerMessage.size()+1)+"' name='ksUID' id='ksUID' />\n";
		for (int i = 0; i < info.getServerConfig().kickServerMessage.size(); i++) {
			String msg = info.getServerConfig().kickServerMessage.get(i);
			s+=getElement(info, "mt_ks_"+i, false, msg);
		}
		s+="</div>\n<p><a href='javascript:addKS()'>"+info.getBotServer().getLanguage("tools.messagetool.add")+"</a></p>\n<hr />";
		//kc
		s+="<h3>"+info.getBotServer().getLanguage("tools.messagetool.kick.channel")+"</h3>\n"
			+ "<div id='mt_kc_list'>\n"
				+ "<input type='hidden' value='"+(info.getServerConfig().kickChannelMessage.size()+1)+"' name='kcUID' id='kcUID' />\n";
		for (int i = 0; i < info.getServerConfig().kickChannelMessage.size(); i++) {
			String msg = info.getServerConfig().kickChannelMessage.get(i);
			s+=getElement(info, "mt_kc_"+i, false, msg);
		}	
		s+="</div>\n<p><a href='javascript:addKC()'>"+info.getBotServer().getLanguage("tools.messagetool.add")+"</a></p>"
		+ "\n<input type='submit' value='"+info.getBotServer().getLanguage("tools.messagetool.save")+"' />\n</form>";
		
		return s;
	}
	public boolean needConnection() {
		return true;
	}
	public String neededPermission() {
		return "messagetool";
	}
	public String toolName() {
		return "messagetool";
	}

}
