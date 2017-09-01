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

import javaxt.http.servlet.HttpServletRequest;
import javaxt.http.servlet.HttpServletResponse;

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
	private boolean connect;
	private String context;
	private Map<String, Object> get;
	private Map<String, Object> post;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private BotServlet servlet;
	private String session_Id;
	private String title;
	private String notification;
	private ArrayList<String> header;
	public WebInformation(HttpServletRequest request, HttpServletResponse response, BotServlet servlet) throws IOException {
		
		this.request = request;
		this.response = response;
		this.context = request.getURL().getPath().toLowerCase();
		this.servlet = servlet;
		this.header = new ArrayList<String>();
		this.post = new HashMap<String, Object>();
		if(request.getMethod().equals("POST")){
			InputStreamReader isr = new InputStreamReader(request.getInputStream(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
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
				if(Main.sessions.containsKey(this.session_Id)) Main.sessions.remove(this.session_Id);
			}
		}
		Map<String, String> sessions = Main.getSession(session_Id);
		if(Main.mapContainKeysS(sessions, new String[]{"log_us","log_ps"})){
			if(this.servlet.accountExist(sessions.get("log_us").toString()) && this.servlet.getAccountByName(sessions.get("log_us").toString()).hash.equals(sessions.get("log_ps"))){
				this.account = this.servlet.getAccountByName(sessions.get("log_us").toString());
				connect=true;
			} else connect=false;
		}else if(Main.mapContainKeys(this.post, new String[]{"log_us","log_ps"})){
			if(this.post.get("log_us")!=null && this.post.get("log_ps")!=null && this.servlet.accountExist(this.post.get("log_us").toString())
					&& this.servlet.getAccountByName(this.post.get("log_us").toString())
					.hash.equals(Main.MD5(this.post.get("log_ps").toString()))){
				this.account = this.servlet.getAccountByName(this.post.get("log_us").toString());
				setSession("log_us", this.post.get("log_us").toString());
				setSession("log_ps", Main.MD5((String) this.post.get("log_ps")));
				connect=true;
			} else connect=false;
		} else connect=false;
	}
	public ArrayList<String> getHeader() {
		return header;
	}
	public void addHeader(String header) {
		this.header.add(header);
	}
	public Account getAccount() {
		return account;
	}
	public String getContext() {
		return context;
	}
	public Map<String, Object> getGet() {
		return get;
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
	public BotServlet getServlet() {
		return servlet;
	}
	public String getSession_Id() {
		return session_Id;
	}
	public Map<String, String> getSessions() {
		return Main.getSession(session_Id);
	}
	public String getTitle() {
		return title;
	}
	public boolean isConnect(){
		return connect;
	}
	public void setContext(String context) {
		this.context = context.toLowerCase();
	}
	public void setNotification(String notification) {
		this.notification = notification;
	}
	public void setSession(String key, String value){
		Main.setSessionValue(this.session_Id, key, value);
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
