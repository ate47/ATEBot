package fr.atesab.bot.handler.tools;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.io.IOException;

import fr.atesab.bot.BotServer;
import fr.atesab.bot.WebInformation;
import fr.atesab.bot.WebToolInformation;

public class AutoMessageHandler extends ToolHandler {
	public static enum EnumMessageElementType{
		equals(),
		contain(),
		startWith(),
		regex();
	}
	public static class MessageElement{
		public EnumMessageElementType type = EnumMessageElementType.equals;
		public String question = "";
		public String file = "";
		public String answer = "";
		public MessageElement() {}
		public MessageElement(EnumMessageElementType type, String question, String answer) {
			this(type,question,answer,"");
		}
		public MessageElement(EnumMessageElementType type, String question, String answer, String file) {
			this.type = type;
			this.question = question;
			this.answer = answer;
			this.file = file;
		}
	}
	private static AutoMessageHandler instance;
	public static AutoMessageHandler getInstance() {
		return instance==null?(instance=new AutoMessageHandler()):instance;
	}
	@SuppressWarnings("deprecation")
	public static String getMessageElement(WebInformation info, String id, MessageElement msg){
		if(msg == null) msg = new MessageElement(EnumMessageElementType.equals, info.getBotServer().getLanguage("tools.amessage.question.name"), info.getBotServer().getLanguage("tools.amessage.answer.name"));
		String s = "<div id='elm_"+id+"'>"+
					"<table class='am_elm'>"+
					"<tr><td>Type : </td><td><select name='"+id+"_type' >";
		EnumMessageElementType[] csts = EnumMessageElementType.class.getEnumConstants();
		for (int i = 0; i < csts.length; i++) {
			s+="<option value='"+i+"'";
			if(csts[i].equals(msg.type))s+=" selected";
			s+=">"+info.getBotServer().getLanguage("tools.amessage.type."+csts[i].toString())+"</option>";
		}
		s+="</select></td></tr>"+
				"<tr><td>"+info.getBotServer().getLanguage("tools.amessage.question")+" : </td><td><input type='text' value='"+escapeHtml4(msg.question).replaceAll("\\'", "&apos;")+"' name='"+id+"_name' /></td></tr>"+
				"<tr><td>"+info.getBotServer().getLanguage("tools.amessage.answer")+" : </td><td><textarea name='"+id+"_answer' class='massta'>"+escapeHtml4(msg.answer).replaceAll("'", "&apos;")+"</textarea></td></tr>"+
				"<tr><td></td>"
				+ "<tr><td>"+info.getBotServer().getLanguage("tools.amessage.file")+" : </td><td><input type='text' value='"+escapeHtml4(msg.file).replaceAll("\\'", "&apos;")+"' name='"+id+"_file' /></td></tr>"
					+ "<tr><td></td><td style='text-align:right'><a href='javascript:deleteE("+id+");' class='elm_del'>"+info.getBotServer().getLanguage("tools.amessage.del").replaceAll("\\'", "&apos;")+"</a></td></tr>"+
			"</table>"+
		"</div>";
		return s;
	}
	private AutoMessageHandler() {
	}
	public String handle(WebToolInformation info) throws IOException {
		String content="";
		info.setTitle("tools.amessage");
		if(info.getPost().containsKey("elm_number")){
			try {
				int number = Integer.valueOf((String) info.getPost().get("elm_number"));
				EnumMessageElementType[] csts = EnumMessageElementType.class.getEnumConstants();
				info.getServerConfig().messages.clear();
				for (int i = 0; i <= number; i++) {
					if(BotServer.mapContainKeys(info.getPost(), new String[]{i+"_type",i+"_answer",i+"_name"})){
						try {
							int type = Integer.valueOf((String) info.getPost().get(i+"_type"));
							if(type>=0 && type < csts.length){
								String file = "";
								if(info.getPost().containsKey(i+"_file") && info.getPost().get(i+"_file")!=null)
									file=(String) info.getPost().get(i+"_file");
								info.getServerConfig().messages.add(new MessageElement(csts[type], info.getPost().get(i+"_name").toString(), info.getPost().get(i+"_answer").toString(), file));
							}
						} catch (Exception e) {}
					}
				}
				info.setNotification(info.getBotServer().getLanguage("panel.save.msg"));
				info.getBotServer().saveConfig();
			} catch (Exception e) {}
		}
		content+="<script type='text/javascript'>\nvar uniqueId = "+(int)(info.getServerConfig().messages.size()+1)+";\r\n" + 
					"function getUniqueId(){\r\n" + 
					"uniqueId++;\r\n" + 
					"document.getElementById(\"elm_number\").value=uniqueId;\r\n" + 
					"return uniqueId;\r\n" + 
					"}\r\n" + 
					"function deleteE(elm){\r\n" + 
					"document.getElementById(\"elm_\"+elm).remove();\r\n" + 
					"}" +
					"function addE(elm){\n" +
					"var elm = document.getElementById(elm);\n" +
					"id=getUniqueId();\n" +
					"elm.insertAdjacentHTML(\"beforeend\",\""+getMessageElement(info, "\"+id+\"", null)+"\");\n}\n</script>" +
					"<form method='post' action=''>\n" +
					"<input type='hidden' value='"+(int)(info.getServerConfig().messages.size()+1)+"' id='elm_number' name='elm_number' />\n" +
					"<div id='elm_container'>\n";
		for (int i = 0; i < info.getServerConfig().messages.size(); i++) {
			content+=getMessageElement(info, String.valueOf(i), info.getServerConfig().messages.get(i));
		}
		content+="</div>\n"+
			"<input type='submit' value='"+info.getBotServer().getLanguage("tools.amessage.save")+"' class='pbutton' />\n"+
			"<a href=\"javascript:addE('elm_container');\" class='pbutton'>"+info.getBotServer().getLanguage("tools.amessage.add")+"</a>\n"+
			"</form>\n";
		return content;
	}
	public boolean needConnection() {
		return true;
	}
	public String neededPermission() {
		return "automessage";
	}
	public String toolName() {
		return "amessage";
	}
}
