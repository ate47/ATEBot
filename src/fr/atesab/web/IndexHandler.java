package fr.atesab.web;

import java.io.IOException;
import java.util.Date;

import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;
import fr.atesab.web.BlogMessage;
import fr.atesab.web.tool.BlogConfigHandler;

public class IndexHandler extends WebHandler {
	public static final String PANEL_NAME = "ATESAB";
	public static final String[][] NAV_BAR = {{"web.home","index.ap", null},
			{"web.works","works.ap",null},
			{"web.info","help.ap",null}};
	public static String buildIndex(String content, String title, WebInformation info) {
		String s = "<div id='header'>"
				+ "\n<table>"
				+ "\n<tr><td><a href='"+info.getHost()+"'><img src='"+info.getHost()+"/files/logo.png' alt='Logo de "+PANEL_NAME+"'/></a></td><td>"
				+ "\n<div id='nav_bar'>";
		for (String[] links: NAV_BAR) {
			if(links.length==3) {
				if((links[2]!=null && info.getAccount()!=null && info.getAccount().hasPerm(links[2])) || links[2]==null) {
					s+="<a href='"+links[1]+"'>"+info.getBotServer().getLanguage(links[0])+"</a>";
				} else continue;
			}
		}
		s+="\n</div></td>\n</table>\n</div>\n</div>\n<div id='body'>\n"+content+"\n</div>";
		info.setTitle(title);
		info.setGlobalTitle(PANEL_NAME);
		return s;
	}
	public static String getAccount(String name, WebInformation info) {
		String s = "";
		/*Account a = Main.getAccountByName(name);
		if(a!=null) {
			s = "<a href='"+info.getHost()+"/user.ap?u="+name+"'>"+name+"</a>";
		} else */
			s = name;
		return s;
	}
	@SuppressWarnings("deprecation")
	public static String getDate(WebInformation info, long date) {
		Date d = new Date(date);
		return info.getBotServer().getLanguage("web.blog.date")
				.replaceAll("%day", info.getBotServer().getLanguage("web.blog.day."+d.getDay()))
				.replaceAll("%date", String.valueOf(d.getDate()))
				.replaceAll("%month", info.getBotServer().getLanguage("web.blog.month."+d.getMonth()))
				.replaceAll("%year", String.valueOf(d.getYear()+1900))
				.replaceAll("%hour", String.valueOf(d.getHours()))
				.replaceAll("%minute", String.valueOf(d.getMinutes()))
				.replaceAll("%second", String.valueOf(d.getSeconds()));
	}
	public String handle(WebInformation info) throws IOException {
		String s = "";
		try{
			int blog;
			if(info.getGet().containsKey("blog") && (blog=Integer.valueOf((String)info.getGet().get("blog")))>=0 && blog<info.getBotServer().getbMessages().size()) {
				s+="<div class='acc_body'>";
				BlogMessage b=info.getBotServer().getbMessages().get(blog);
				info.setTitle(b.title);
				s+="\n<div class='blog_message_header'>" + info.getBotServer().getLanguage("web.blog.title", "<a href='index.ap?blog="+blog+"'>"+b.title+"</a>", getAccount(b.author, info), getDate(info, b.date))
						+ "\n</div>\n<div class='blog_message'>\n"+b.text+"\n</div></div>";
			} else {
				s="<div class='blog_message_container'>"
				+ "\n";
				int page=0;
				try{page=Integer.valueOf((String) info.getGet().getOrDefault("page", "0"));}catch (Exception e) {}
				int maxPage = info.getBotServer().getbMessages().size() / BlogConfigHandler.BLOG_PER_PAGE + 1;
				for (int i = Math.min((page+1)*BlogConfigHandler.BLOG_PER_PAGE,info.getBotServer().getbMessages().size())-1; i >= page*BlogConfigHandler.BLOG_PER_PAGE; i--) {
					BlogMessage b = info.getBotServer().getbMessages().get(i);
					s+="\n<div class='blog_message_header'>" + info.getBotServer().getLanguage("web.blog.title", "<a href='index.ap?blog="+i+"'>"+b.title+"</a>", getAccount(b.author, info), getDate(info, b.date))
						+ "\n</div>\n<div class='blog_message'>\n"+b.text+"\n</div>";
				}
				s+="\n</div>";
				if(page-1>=0)s+="<a href='index.ap?page="+(page-1)+"'>"+info.getBotServer().getLanguage("web.blog.lastpage")+"</a> | ";
				s+=info.getBotServer().getLanguage("web.blog.page")+" "+(page+1)+" / "+maxPage;
				if(page+1<maxPage)s+=" | <a href='index.ap?page="+(page+1)+"'>"+info.getBotServer().getLanguage("web.blog.nextpage")+"</a>";
			}
		} catch (Exception e) {
			s="<div class='acc_body'>"+info.getBotServer().getLanguage("web.blog.page.unknow")+"</div>";
		}
		return buildIndex(s, null, info);
	}
	public boolean needConnection() {
		return false;
	}
	public String neededPermission() {
		return null;
	}
}
