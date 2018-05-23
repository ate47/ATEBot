package fr.atesab.web.tool;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.io.IOException;

import fr.atesab.bot.BotServer;
import fr.atesab.bot.WebToolInformation;
import fr.atesab.bot.handler.tools.ToolHandler;
import fr.atesab.web.WorkMessage;
import fr.atesab.web.IndexHandler;

public class WorksConfigHandler extends ToolHandler {
	@SuppressWarnings("deprecation")
	public String handle(WebToolInformation info) throws IOException {
		String s = "";
		int blog;
		try {
			if((blog = Integer.valueOf((String) info.getGet().getOrDefault("delete", "-1"))) >= 0 && blog < info.getBotServer().getwMessages().size()){
			info.getBotServer().getwMessages().remove(blog);
			info.setNotification(info.getBotServer().getLanguage("webconfig.works.delete"));
		}} catch (Exception e) {}
		try{if((blog = Integer.valueOf((String) info.getGet().getOrDefault("blog", "-1"))) >= 0 && blog < info.getBotServer().getwMessages().size()){
			WorkMessage bmsg = info.getBotServer().getwMessages().get(blog);
			if(BotServer.mapContainNoEmptyKeys(info.getPost(), new String[] {"b_text","b_title", "b_img", "b_link"})) {
				bmsg.title = (String) info.getPost().get("b_title");
				bmsg.text = (String) info.getPost().get("b_text");
				bmsg.img = (String) info.getPost().get("b_img");
				bmsg.link = (String) info.getPost().get("b_link");
				info.getBotServer().getwMessages().set(blog, bmsg);
				info.getBotServer().saveConfig();
				info.setNotification(info.getBotServer().getLanguage("webconfig.works.save"));
			}
			s+="<h3>"+info.getBotServer().getLanguage("web.blog.edit")+"</h3>\n<form action='' method='post'>\n"
					+ "<p>"+info.getBotServer().getLanguage("web.blog.edit.title")+" : <input type='text' name='b_title' value='"+escapeHtml4(bmsg.title).replaceAll("\\'", "&apos;")+"' /></p>\n"
					+ "<p>"+info.getBotServer().getLanguage("web.blog.edit.img")+" : <input type='text' name='b_img' value='"+escapeHtml4(bmsg.img).replaceAll("\\'", "&apos;")+"' /></p>\n"
					+ "<p>"+info.getBotServer().getLanguage("web.blog.edit.link")+" : <input type='text' name='b_link' value='"+escapeHtml4(bmsg.link).replaceAll("\\'", "&apos;")+"' /></p>\n"
					+ "<p>"+info.getBotServer().getLanguage("web.blog.edit.content")+" : </p>"
					+ "<textarea class='blog_message_edit' name='b_text'>"+escapeHtml4(bmsg.text).replaceAll("\\'", "&apos;")+"</textarea><br />\n"
					+ "<input value='"+info.getBotServer().getLanguage("web.blog.edit.save")+"' type='submit' /></form>";
		} else throw new Exception();}catch (Exception e) {
			if(BotServer.mapContainNoEmptyKeys(info.getPost(), new String[] {"b_text","b_title", "b_img", "b_link"})) {
				info.getBotServer().getwMessages().add(new WorkMessage(info.getAccount().name, (String) info.getPost().get("b_title"), (String) info.getPost().get("b_text")
						, (String) info.getPost().get("b_img"), (String) info.getPost().get("b_link")));
				info.getBotServer().saveConfig();
				info.setNotification(info.getBotServer().getLanguage("webconfig.works.create"));
			}
			s+="<h3>"+info.getBotServer().getLanguage("webconfig.works.new")+"</h3>\n<form action='' method='post'>\n"
					+ "<p>"+info.getBotServer().getLanguage("web.blog.edit.title")+" : <input type='text' name='b_title' value='' /></p>\n"
					+ "<p>"+info.getBotServer().getLanguage("web.blog.edit.img")+" : <input type='text' name='b_img' value='' /></p>\n"
					+ "<p>"+info.getBotServer().getLanguage("web.blog.edit.link")+" : <input type='text' name='b_link' value='' /></p>\n"
					+ "<p>"+info.getBotServer().getLanguage("web.blog.edit.content")+" : </p>"
					+ "<textarea class='blog_message_edit' name='b_text'></textarea><br />\n"
					+ "<input value='"+info.getBotServer().getLanguage("web.blog.edit.create")+"' type='submit' /></form>";
		}
			s+="\n<hr />\n<div class='blog_message_list'>>> <a href='wconfig.ap?app="+this.toolName()+"'>"+info.getBotServer().getLanguage("webconfig.works.new")+"</a></div>";
		for (int j = 0; j < info.getBotServer().getwMessages().size(); j++) {
			WorkMessage b = info.getBotServer().getwMessages().get(j);
			s+="\n<hr />\n<div class='blog_message_list'>"+ "<a href='"+info.getHost()+"/works.ap#w"+j+"'>"+b.title+"</a> / "
					+ IndexHandler.getAccount(b.author, info) + " / "
							+ "<a href='wconfig.ap?app=works&blog="+j+"'>"+info.getBotServer().getLanguage("web.blog.edit")+"</a> / "
							+ "<a href='wconfig.ap?app=works&delete="+j+"'>"+info.getBotServer().getLanguage("web.blog.edit.delete")+"</a></div>";
		}
		return s;
	}
	public boolean needConnection() {
		return true;
	}
	public String neededPermission() {
		return "works";
	}
	public String toolName() {
		return "works";
	}

}
