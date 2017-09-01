package fr.atesab.bot.handler.tools;

import java.io.IOException;

import fr.atesab.bot.Main;
import fr.atesab.bot.WebInformation;

public class AutoMessageHandler extends ToolHandler {
	public static String getMessageElement(String id, MessageElement msg){
		if(msg == null) msg = new MessageElement(EnumMessageElementType.equals, Main.lang.getLangage("tools.amessage.question.name"), Main.lang.getLangage("tools.amessage.answer.name"));
		String s = "<div id='elm_"+id+"'>"+
					"<table class='am_elm'>"+
					"<tr><td>Type : </td><td><select name='"+id+"_type' >";
		EnumMessageElementType[] csts = EnumMessageElementType.class.getEnumConstants();
		for (int i = 0; i < csts.length; i++) {
			s+="<option value='"+i+"'";
			if(csts[i].equals(msg.type))s+=" selected";
			s+=">"+Main.lang.getLangage("tools.amessage.type."+csts[i].toString())+"</option>";
		}
		s+="</select></td></tr>"+
				"<tr><td>"+Main.lang.getLangage("tools.amessage.question")+" : </td><td><input type='text' value='"+msg.question+"' name='"+id+"_name' /></td></tr>"+
				"<tr><td>"+Main.lang.getLangage("tools.amessage.answer")+" : </td><td><textarea name='"+id+"_answer' class='massta'>"+msg.answer+"</textarea></td></tr>"+
				"<tr><td></td><td style='text-align:right'><a href='javascript:deleteE("+id+");' class='elm_del'>"+Main.lang.getLangage("tools.amessage.del")+"</a></td></tr>"+
			"</table>"+
		"</div>";
		return s;
	}
	public String handle(WebInformation info) throws IOException {
		String content="";
		info.setTitle("tools.amessage");
		if(info.getPost().containsKey("elm_number")){
			try {
				int number = Integer.valueOf((String) info.getPost().get("elm_number"));
				EnumMessageElementType[] csts = EnumMessageElementType.class.getEnumConstants();
				Main.messages.clear();
				for (int i = 0; i <= number; i++) {
					if(Main.mapContainKeys(info.getPost(), new String[]{i+"_type",i+"_answer",i+"_name"})){
						try {
							int type = Integer.valueOf((String) info.getPost().get(i+"_type"));
							if(type>=0 && type < csts.length){
								Main.messages.add(new MessageElement(csts[type], info.getPost().get(i+"_name").toString(), info.getPost().get(i+"_answer").toString()));
							}
						} catch (Exception e) {}
					}
				}
				info.setNotification(Main.lang.getLangage("panel.save.msg"));
				Main.saveConfig();
			} catch (Exception e) {}
		}
		content+="<script type='text/javascript'>\nvar uniqueId = "+(int)(Main.messages.size()+1)+";\r\n" + 
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
					"elm.insertAdjacentHTML(\"beforeend\",\""+getMessageElement("\"+id+\"", null)+"\");\n}\n</script>" +
					"<form method='post' action=''>\n" +
					"<input type='hidden' value='"+(int)(Main.messages.size()+1)+"' id='elm_number' name='elm_number' />\n" +
					"<div id='elm_container'>\n";
		for (int i = 0; i < Main.messages.size(); i++) {
			content+=getMessageElement(String.valueOf(i), Main.messages.get(i));
		}
		content+="</div>\n"+
			"<input type='submit' value='"+Main.lang.getLangage("tools.amessage.save")+"' class='pbutton' />\n"+
			"<a href=\"javascript:addE('elm_container');\" class='pbutton'>"+Main.lang.getLangage("tools.amessage.add")+"</a>\n"+
			"</form>\n";
		return content;
	}
	public String neededPermission() {
		return "automessage";
	}
	public boolean needConnection() {
		return true;
	}
	public static class MessageElement{
		public EnumMessageElementType type;
		public String question;
		public String answer;
		public MessageElement(EnumMessageElementType type, String question, String answer) {
			this.type = type;
			this.question = question;
			this.answer = answer;
		}
	}
	public static enum EnumMessageElementType{
		equals(),
		contain(),
		startWith();
	}
	public String toolName() {
		return "amessage";
	}
}
