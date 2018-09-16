package fr.atesab.bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import fr.atesab.bot.handler.MessageHandler;
import javaxt.http.servlet.HttpServlet;
import javaxt.http.servlet.HttpServletRequest;
import javaxt.http.servlet.HttpServletResponse;
import javaxt.http.servlet.ServletException;

public class BotServlet extends HttpServlet {
	public static String getIntWithSize(int n, int size) {
		String s = String.valueOf(n);
		if (s.length() < size)
			for (int i = s.length(); i < size; i++)
				s = "0" + s;
		return s;
	}

	public static void log(String instance, String message) {
		long l = System.currentTimeMillis();
		System.out.println(getIntWithSize((int) (l / (1000 * 60 * 60) % 24), 2) + ":"
				+ getIntWithSize((int) (l / (1000 * 60) % 60), 2) + ":" + getIntWithSize((int) (l / 1000 % 60), 2) + ":"
				+ getIntWithSize((int) (l % 1000), 3) + ": [INFO][" + instance + "] " + message);
	}

	private Map<String, WebHandler> contexts;
	private WebHandler loginContext;
	private BotServer server;
	private String path;

	public BotServlet(BotServer server, WebHandler loginContext, WebHandler defaultHandler) {
		this(server, loginContext, defaultHandler, "");
	}

	public BotServlet(BotServer server, WebHandler loginContext, WebHandler defaultHandler, String path) {
		this.server = server;
		contexts = new HashMap<String, WebHandler>();
		this.loginContext = loginContext;
		this.path = path;
		contexts.put("index.ap", defaultHandler);
	}

	public boolean accountExist(String name) {
		for (Account a : server.getAccounts())
			if (a.name.equals(name))
				return true;
		return false;
	}

	public Account getAccountByName(String name) {
		return server.getAccountByName(name);
	}

	public Map<String, WebHandler> getContexts() {
		return contexts;
	}

	public WebHandler getDefaultContext() {
		return contexts.getOrDefault("index.ap", new MessageHandler("ERROR: NO DEFAULT HANDLER", "Erreur"));
	}

	public String getHandler(WebHandler handler, WebInformation info) throws IOException {
		// load handler with context
		String page = null;
		if (handler.needConnection() && info.isConnect()) { // try if a connection is needed and if the user is
															// connected
			if (info.getAccount().hasPerm(handler.neededPermission())) { // try if a permission is needed and if the
																			// user have it
				page = handler.handle(info);
			} else {
				page = (new MessageHandler(server.getLanguage("panel.noperm"), "panel.error")).handle(info);
			}
		} else if (!handler.needConnection()) {
			page = handler.handle(info);
		} else { // if not connected AND need connection send to login
			if (loginContext != null)
				page = loginContext.handle(info);
			else
				page = (new MessageHandler("ERROR: NO LOGIN HANDLER", "panel.error")).handle(info);
		}
		return page;
	}

	public WebHandler getLoginContext() {
		return loginContext;
	}

	public String getPath() {
		return path;
	}

	public BotServer getServer() {
		return server;
	}

	public void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			log("Servlet", request.getProtocol() + " " + request.getMethod() + " " + request.getPath() + " "
					+ request.getRemoteHost());
			// if it change between loading
			server.getLang().loadLanguage();
			WebInformation info = new WebInformation(request, response, this);
			String page = servletProcessor(info);
			// create head
			String header = "<!DOCTYPE html>\n<html lang='" + server.getLanguage("html_lang")
					+ "'>\n<head>\n<meta charset='UTF-8'>\n<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>\n<title>"
					+ info.getGlobalTitle();
			if (info.getTitle() != null)
				header += " - " + server.getLanguage(info.getTitle()); // custom title
			header += "</title>\n<link rel='stylesheet' href='" + info.getHost() + "/files/style.css' />\n"
					+ "<script type='text/javascript' src='" + info.getHost() + "/files/script.js'></script>\n"
					+ "<script type='text/javascript' src='" + info.getHost() + "/files/jquery.js'></script>\n"
					+ info.getHeader().stream().collect(Collectors.joining("\n")) + "\n</head>\n<body>\n";
			if (info.getNotification() != null)
				header += "<div id='notification'>" + info.getNotification() + "</div>\n";
			if (!(info.isCanceled() || page == null)) {
				String html = header + page + "\n</body>\n</html>";
				response.write(html.getBytes("UTF-8"), false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void registerContext(String context, WebHandler handler) {
		contexts.put(context.toLowerCase(), handler);
	}

	public String servletProcessor(WebInformation info) throws IOException {
		HttpServletResponse response = info.getResponse();
		response.setCharacterEncoding("utf-8");
		response.setLocale(new Locale("fr"));
		response.setContentType("text/html;charset=utf-8"); // UTF-8 <3
		if (info.getContext().equals("/favicon.ico"))
			info.setContext("/files/favicon.ico"); // change favicon file location to the "/files/" deposit
		if (info.getContext().startsWith("/files/")) { // search in deposit or not ?
			info.getResponse().setContentType(ContentType.defaultCT.getType());
			boolean text = false;
			String[] sa = info.getContext().split("\\."); // look at content type
			if (sa.length > 1) {
				ContentType[] c = ContentType.class.getEnumConstants();

				for (int j = 0; j < c.length; j++) {
					if (sa[sa.length - 1].equalsIgnoreCase(c[j].toString().toLowerCase())) {
						response.setContentType(c[j].getType());
						text = c[j].isText();
						break;
					}
				}
			}
			File f = new File("atebot/" + info.getRequest().getURL().getPath().substring("/files".length()));
			// if the type is text change information
			if (text) {
				BufferedReader br = null;
				if (f.exists())
					br = new BufferedReader(new FileReader(f));
				else
					br = new BufferedReader(
							new InputStreamReader(BotServlet.class.getResourceAsStream(info.getContext()), "UTF-8"));
				if (br != null) {
					String line;
					String s = "";
					while ((line = br.readLine()) != null) {
						s += line.replaceAll("%host%", info.getHost()).replaceAll("%context%", info.getContext())
								.replaceAll("%sessionid%", info.getSession_Id())
								.replaceAll("%pcolor%", server.getPanelColor()) + "\n";
					}
					info.getResponse().write(s);
				}
			} else {
				if (f.exists())
					info.getResponse().write(f, info.getResponse().getContentType(), false);
				else
					info.getResponse().write(BotServlet.class.getResourceAsStream(info.getContext()), false);
			}
			info.setCanceled(true);
			return null;
		}
		String ctx = info.getContext().toLowerCase();
		if (ctx.length() > 0)
			ctx = ctx.substring(1);
		WebHandler handler = contexts.getOrDefault(ctx.split("/")[0], getDefaultContext());
		String page = getHandler(handler, info);
		return page;
	}

	public void setDefaultContext(String defaultContext, WebHandler handler) {
		contexts.put("index.ap", handler);
	}

	public void setPath(String path) {
		this.path = path;
	}
}
