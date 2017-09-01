package fr.atesab.bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fr.atesab.bot.handler.MessageHandler;
import fr.atesab.bot.handler.PanelHandler;
import javaxt.http.servlet.HttpServlet;
import javaxt.http.servlet.HttpServletRequest;
import javaxt.http.servlet.HttpServletResponse;
import javaxt.http.servlet.ServletException;

public class BotServlet extends HttpServlet {
	public Map<String, WebHandler> contexts;
	public WebHandler loginContext;
	public BotServlet(WebHandler loginContext, WebHandler defaultHandler){
		contexts = new HashMap<String, WebHandler>();
		this.loginContext = loginContext;
		contexts.put("index.ap", defaultHandler);
		
	}
	public void registerContext(String context, WebHandler handler){
		contexts.put(context.toLowerCase(), handler);
	}
	public void setDefaultContext(String defaultContext, WebHandler handler){
		contexts.put("index.ap", handler);
	}
	
	public void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// if it change between loading
			Main.lang.loadLanguage();
			WebInformation info = new WebInformation(request, response, this);
			response.setCharacterEncoding("utf-8");
			response.setLocale(new Locale("fr"));
			response.setContentType("text/html;charset=utf-8"); // UTF-8 <3
			if(info.getContext().equals("/favicon.ico"))info.setContext("/files/favicon.ico"); // change favicon file location to the "/files/" deposit
			if(info.getContext().startsWith("/files/")) { // search in deposit or not ?
				boolean text = true;
				String[] sa = info.getContext().split("\\."); // look at content type
				if(sa.length>1){
					ContentType[] c = ContentType.class.getEnumConstants();
					for (int j = 0; j < c.length; j++) {
						if(sa[sa.length-1].equals(c[j].toString().toLowerCase())) {
							response.setContentType(c[j].getType());
							text = c[j].isText();
						}
					}
				}
				// if the type is text change information
				if(text) {
					BufferedReader br = new BufferedReader(new InputStreamReader(BotServlet.class.getResourceAsStream(info.getContext())));
					String line;
					String s = "";
					while((line=br.readLine()) !=null){
						s +=line.replaceAll("%host%", info.getHost())
							.replaceAll("%context%", info.getContext())
							.replaceAll("%sessionid%", info.getSession_Id())
							.replaceAll("%pcolor%", Main.PANEL_COLOR)+"\n";
					}
					info.getResponse().write(s);
				} else info.getResponse().write(BotServlet.class.getResourceAsStream(info.getContext()), false);
				return;
			}
			WebHandler handler = contexts.getOrDefault(info.getContext(), contexts.getOrDefault("index.ap", new MessageHandler("ERROR: NO DEFAULT HANDLER","Erreur")));
			String page = getHandler(handler, info);
			// create head
			String header = "<!DOCTYPE html>\n<html lang='"+Main.lang.getLangage("html_lang")+"'>\n<head>\n<meta charset='UTF-8'>\n<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>\n<title>"+PanelHandler.PANEL_NAME;
			if(info.getTitle()!=null)header+=" - "+Main.lang.getLangage(info.getTitle()); //custom title
			header+="</title>\n<link rel='stylesheet' href='"+info.getHost()+"/files/style.css' />\n"
					+ "<script type='text/javascript' src='"+info.getHost()+"/files/script.js'></script>" + Main.listWriter(info.getHeader(), "%s")
					+ "</head>\n<body>\n";
			if(info.getNotification()!=null)header+="<div id='notification'>"+info.getNotification()+"</div>\n";
			if(page!=null) {
				String html = header+page+"\n</body>\n</html>";
				response.write(html.getBytes("UTF-8"),false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getHandler(WebHandler handler, WebInformation info) throws IOException {
		// load handler with context
		String page = null;
		if(handler.needConnection() && info.isConnect()){ // try if a connection is needed and if the user is connected
			if(info.getAccount().hasPerm(handler.neededPermission())) { // try if a permission is needed and if the user have it
				page = handler.handle(info);
			} else {
				page = (new MessageHandler(Main.lang.getLangage("panel.noperm"),"panel.error")).handle(info);
			}
		} else { // if not connected AND need connection send to login
			if(loginContext!=null) page = loginContext.handle(info);
			else page = (new MessageHandler("ERROR: NO LOGIN HANDLER","panel.error")).handle(info);
		}
		return page;
	}
	public boolean accountExist(String name){
		for (Account a: Main.accounts) if(a.name.equals(name))return true;
		return false;
	}
	public Account getAccountByName(String name){
		for (Account a: Main.accounts) if(a.name.equals(name))return a;
		return null;
	}
}
