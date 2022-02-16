package fr.atesab.bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.atesab.bot.ServerConfig;
import fr.atesab.bot.handler.PanelHandler;
import javaxt.http.servlet.HttpServletRequest;
import javaxt.http.servlet.HttpServletResponse;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;

public class WebInformation {
	public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {
		if (query != null) {
			String pairs[] = query.split("[&]");
			for (String pair : pairs) {
				String param[] = pair.split("[=]");
				String key = null;
				String value = null;
				if (param.length > 0)
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				if (param.length > 1)
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						@SuppressWarnings("unchecked")
						List<String> values = (List<String>) obj;
						values.add(value);

					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}
	private Account account;
	private int botId;
	private boolean connect;
	private boolean canceled;
	private String context;
	private String currentServerId;
	private Map<String, Object> get;
	private Map<String, Object> post;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private BotServlet servlet;
	private String session_Id;
	private String title;
	private String globalTitle;
	private String notification;
	private ArrayList<String> header;
	private BotInstance botInstance;
	private ServerConfig serverConfig;
	private IGuild server;
	private BotServer botServer;
	private String currentLink;
	WebInformation() {}
	public WebInformation(HttpServletRequest request, HttpServletResponse response, BotServlet servlet) throws IOException {
		this.request = request;
		this.response = response;
		this.context = request.getURL().getPath().toLowerCase();
		this.globalTitle = PanelHandler.PANEL_NAME;
		this.servlet = servlet;
		this.botServer = servlet.getServer();
		this.header = new ArrayList<String>();
		this.post = new HashMap<String, Object>();
		if(request.getMethod().equals("POST"))
			if(request.getContentType()!=null && !request.getContentType().contains("multipart/form-data")) {
				BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
				String query = br.readLine();
				parseQuery(query, this.post);
			}
		this.get = new HashMap<String, Object>();
		String query = request.getURL().getQuery();
		if(query!=null && !query.isEmpty()){
			String[] qs=query.split("\\&");
			for (int i = 0; i < qs.length; i++) {
				String[] d = qs[i].split("=", 2);
				if(d.length==2)this.get.put(URLDecoder.decode(d[0],"UTF-8"), URLDecoder.decode(d[1],"UTF-8"));
			}
		}
		this.session_Id = request.getSession().getID();
		if(post.containsKey("action") && !((String) post.get("action")).isEmpty()){
			String action = (String) post.get("action");
			if(action.equals("disconnect")){
				if(botServer.getSessions().containsKey(this.session_Id)) botServer.getSessions().remove(this.session_Id);
			}
		}
		Map<String, String> sessions = botServer.getSession(session_Id);
		if(BotServer.mapContainKeysS(sessions, new String[]{"log_us","log_ps"})){
			if(this.servlet.accountExist(sessions.get("log_us").toString()) && this.servlet.getAccountByName(sessions.get("log_us").toString()).hash.equals(sessions.get("log_ps"))){
				this.account = this.servlet.getAccountByName(sessions.get("log_us").toString());
				connect=true;
			} else connect=false;
		}else if(BotServer.mapContainKeys(this.post, new String[]{"log_us","log_ps"})){
			if(this.post.get("log_us")!=null && this.post.get("log_ps")!=null && this.servlet.accountExist(this.post.get("log_us").toString())
					&& this.servlet.getAccountByName(this.post.get("log_us").toString())
					.hash.equals(BotServer.md5(this.post.get("log_ps").toString()))){
				this.account = this.servlet.getAccountByName(this.post.get("log_us").toString());
				setSession("log_us", this.post.get("log_us").toString());
				setSession("log_ps", BotServer.md5((String) this.post.get("log_ps")));
				connect=true;
			} else connect=false;
		} else connect=false;
		currentLink = "?";
		botId = -1;
		currentServerId = "";
		if(getGet().containsKey("botid") && !((String)getGet().get("botid")).isEmpty()) {
			botId = Integer.valueOf((String)getGet().get("botid"));
			currentLink+="botid="+botId;
		} else botId = -1;
		if(account!=null && account.hasPerm("deletebot") && BotServer.mapContainNoEmptyKeys(getGet(), new String[] {"deletebot"})) {
			BotInstance i;
			if((i = botServer.getBotInstanceByName((String) getGet().get("deletebot")))!=null) {
				botServer.getInstances().remove(i);
				botServer.saveConfig();
			}
		}
		if(getGet().containsKey("serverid") && !((String)getGet().get("serverid")).isEmpty()) {
			currentServerId = (String)getGet().get("serverid");
		}
		if(botId>=0 && botId<botServer.getInstances().size()) {
			botInstance = botServer.getInstances().get(botId);
			if(!currentServerId.isEmpty())
				try {
					long id = Long.valueOf(currentServerId);
					serverConfig = botInstance.getServerConfigById(id);
					server = botInstance.getClient().getGuildByID(id);
					currentLink+="&serverid="+currentServerId;
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
	public WebInformation(WebInformation oldInfo) {
		this.account = oldInfo.account;
		this.botId = oldInfo.botId;
		this.connect = oldInfo.connect;
		this.context = oldInfo.context;
		this.currentServerId = oldInfo.currentServerId;
		this.get = oldInfo.get;
		this.post = oldInfo.post;
		this.request = oldInfo.request;
		this.response = oldInfo.response;
		this.servlet = oldInfo.servlet;
		this.session_Id = oldInfo.session_Id;
		this.title = oldInfo.title;
		this.globalTitle = oldInfo.globalTitle;
		this.notification = oldInfo.notification;
		this.header = oldInfo.header;
		this.botInstance = oldInfo.botInstance;
		this.serverConfig = oldInfo.serverConfig;
		this.server = oldInfo.server;
		this.currentLink = oldInfo.currentLink;
	}
	
	public void addHeader(String header) {
		this.header.add(header);
	}
	public void addMeta(String name, String content) {
		this.header.add("<meta name='"+name.replaceAll("\'", "\\\\\'")+"' content='"+content.replaceAll("\'", "\\\\\'")+"'");
	}
	public Account getAccount() {
		return account;
	}
	public int getBotId() {
		return botId;
	}
	public BotInstance getBotInstance() {
		return botInstance;
	}
	public BotServer getBotServer() {
		return botServer;
	}
	public IDiscordClient getClient() {
		return botInstance.getClient();
	}
	public String getContext() {
		return context;
	}
	public String getCurrentLink() {
		return currentLink;
	}
	public String getCurrentServerId() {
		return currentServerId;
	}
	public Map<String, Object> getGet() {
		return get;
	}
	public String getGlobalTitle() {
		return globalTitle;
	}
	public ArrayList<String> getHeader() {
		return header;
	}
	public String getHost() {
		String s;
		if(request.isSecure()) s = "https://"; else s = "http://";
		s+=request.getHost();
		if(request.getPort()!=80)s+=":"+request.getPort();
		return s;
	}
	public String getNotification() {
		return notification;
	}
	public Map<String, Object> getPost() {
		return post;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public IGuild getServer() {
		return server;
	}
	public ServerConfig getServerConfig() {
		return serverConfig;
	}
	public BotServlet getServlet() {
		return servlet;
	}
	public String getSession_Id() {
		return session_Id;
	}
	public Map<String, String> getSessions() {
		return botServer.getSession(session_Id);
	}
	public String getTitle() {
		return title;
	}
	public boolean isCanceled() {
		return canceled;
	}
	public boolean isConnect(){
		return connect;
	}
	public void setBotInstance(BotInstance botInstance) {
		this.botInstance = botInstance;
	}
	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
	public void setContext(String context) {
		this.context = context.toLowerCase();
	}
	public void setCurrentLink(String currentLink) {
		this.currentLink = currentLink;
	}
	public void setGlobalTitle(String globalTitle) {
		this.globalTitle = globalTitle;
	}
	public void setNotification(String notification) {
		this.notification = notification;
	}
	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}
	public void setServlet(BotServlet servlet) {
		this.servlet = servlet;
	}
	public void setSession(String key, String value){
		botServer.setSessionValue(this.session_Id, key, value);
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String toString() {
		return "WebInformation [account=" + account + ", connect=" + connect + ", context=" + context + ", get=" + get
				+ ", post=" + post + ", session_Id=" + session_Id + ", title=" + title + ", notification="
				+ notification + ", header=" + header + "]";
	}
}
