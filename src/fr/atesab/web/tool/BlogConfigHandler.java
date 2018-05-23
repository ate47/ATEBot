package fr.atesab.web.tool;

import java.io.IOException;

import fr.atesab.bot.BotServer;
import fr.atesab.bot.WebToolInformation;
import fr.atesab.bot.handler.tools.ToolHandler;
import fr.atesab.web.IndexHandler;
import fr.atesab.web.BlogMessage;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public class BlogConfigHandler extends ToolHandler {
	public static final int BLOG_PER_PAGE = 20;
	@SuppressWarnings("deprecation")
	public String handle(WebToolInformation info) throws IOException {
		String s = "";
		int blog;
		try {
			if((blog = Integer.valueOf((String) info.getGet().getOrDefault("delete", "-1"))) >= 0 && blog < info.getBotServer().getbMessages().size()){
			info.getBotServer().getbMessages().remove(blog);
			info.setNotification(info.getBotServer().getLanguage("web.blog.edit.delete.notification"));
		}} catch (Exception e) {}
		try{if((blog = Integer.valueOf((String) info.getGet().getOrDefault("blog", "-1"))) >= 0 && blog < info.getBotServer().getbMessages().size()){
			BlogMessage bmsg = info.getBotServer().getbMessages().get(blog);
			if(BotServer.mapContainNoEmptyKeys(info.getPost(), new String[] {"b_text","b_title"})) {
				bmsg.title = (String) info.getPost().get("b_title");
				bmsg.text = (String) info.getPost().get("b_text");
				info.getBotServer().getbMessages().set(blog, bmsg);
				info.getBotServer().saveConfig();
				info.setNotification(info.getBotServer().getLanguage("web.blog.edit.save.notification"));
			}
			s+="<h3>"+info.getBotServer().getLanguage("web.blog.edit")+"</h3>\n<form action='' method='post'>\n"
					+ "<p>"+info.getBotServer().getLanguage("web.blog.edit.title")+" : <input type='text' name='b_title' value='"+escapeHtml4(bmsg.title).replaceAll("\\'", "&apos;")+"' /></p>\n"
					+ "<p>"+info.getBotServer().getLanguage("web.blog.edit.content")+" : </p>"
					+ "<textarea class='blog_message_edit' name='b_text'>"+escapeHtml4(bmsg.text).replaceAll("\\'", "&apos;")+"</textarea><br />\n"
					+ "<input value='"+info.getBotServer().getLanguage("web.blog.edit.save")+"' type='submit' /></form>";
		} else throw new Exception();}catch (Exception e) {
			if(BotServer.mapContainNoEmptyKeys(info.getPost(), new String[] {"b_text","b_title"})) {
				info.getBotServer().getbMessages().add(new BlogMessage(info.getAccount().name, (String) info.getPost().get("b_title"), 
						(String) info.getPost().get("b_text"), System.currentTimeMillis()));
				info.getBotServer().saveConfig();
			info.setNotification(info.getBotServer().getLanguage("web.blog.edit.create.notification"));
			}
			s+="<h3>"+info.getBotServer().getLanguage("web.blog.new")+"</h3>\n<form action='' method='post'>\n"
					+ "<p>"+info.getBotServer().getLanguage("web.blog.edit.title")+" : <input type='text' name='b_title' value='' /></p>\n"
					+ "<p>"+info.getBotServer().getLanguage("web.blog.edit.content")+" : </p>"
					+ "<textarea class='blog_message_edit' name='b_text'></textarea><br />\n"
					+ "<input value='"+info.getBotServer().getLanguage("web.blog.edit.create")+"' type='submit' /></form>";
		}
		int page=0;
		try{page=Integer.valueOf((String) info.getGet().getOrDefault("page", "0"));}catch (Exception e) {}
		int maxPage = info.getBotServer().getbMessages().size() / BLOG_PER_PAGE + 1;
			s+="\n<hr />\n<div class='blog_message_list'>>> <a href='wconfig.ap?app="+this.toolName()+"'>"+info.getBotServer().getLanguage("web.blog.new")+"</a></div>";
		for (int i = Math.min((page+1)*BLOG_PER_PAGE,info.getBotServer().getbMessages().size())-1; i >= page*BLOG_PER_PAGE; i--) {
			BlogMessage b = info.getBotServer().getbMessages().get(i);
			s+="\n<hr />\n<div class='blog_message_list'>"+
			info.getBotServer().getLanguage("web.blog.title", "<a href='"+info.getHost()+"/index.ap?blog="+i+"'>"+b.title+"</a>"
					, IndexHandler.getAccount(b.author, info), IndexHandler.getDate(info, b.date))+", "
							+ "<a href='wconfig.ap?app=blog&blog="+i+"'>"+info.getBotServer().getLanguage("web.blog.edit")+"</a> / "
							+ "<a href='wconfig.ap?app=blog&delete="+i+"'>"+info.getBotServer().getLanguage("web.blog.edit.delete")+"</a></div>";
		}
		if(page-1>=0)s+="<a href='wconfig.ap?app="+this.toolName()+"&page="+(page-1)+"'>"+info.getBotServer().getLanguage("web.blog.lastpage")+"</a> | ";
		s+=info.getBotServer().getLanguage("web.blog.page")+" "+(page+1)+" / "+maxPage;
		if(page+1<maxPage)s+=" | <a href='wconfig.ap?app="+this.toolName()+"&page="+(page+1)+"'>"+info.getBotServer().getLanguage("web.blog.nextpage")+"</a>";
		return s;
	}
	public boolean needConnection() {
		return true;
	}
	public String neededPermission() {
		return "blog";
	}
	public String toolName() {
		return "blog";
	}

}
