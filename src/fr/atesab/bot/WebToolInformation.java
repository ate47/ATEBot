package fr.atesab.bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import fr.atesab.bot.handler.ToolsHandler;
import javaxt.http.servlet.HttpServletRequest;
import javaxt.http.servlet.HttpServletResponse;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;

public class WebToolInformation extends WebInformation {
	private WebInformation webInformation;
	private ToolsHandler toolsHandler;
	public WebToolInformation(WebInformation webInformation, ToolsHandler toolsHandler)
			throws IOException {
		this.webInformation = webInformation;
		this.toolsHandler = toolsHandler;
	}
	@Override
	public BotServer getBotServer() {
		return webInformation.getBotServer();
	}
	public void addHeader(String header) {
		webInformation.addHeader(header);
	}
	public void addMeta(String name, String content) {
		webInformation.addMeta(name, content);
	}
	public Account getAccount() {
		return webInformation.getAccount();
	}
	public int getBotId() {
		return webInformation.getBotId();
	}
	public BotInstance getBotInstance() {
		return webInformation.getBotInstance();
	}
	public IDiscordClient getClient() {
		return webInformation.getClient();
	}
	public String getContext() {
		return webInformation.getContext();
	}
	public String getCurrentLink() {
		return webInformation.getCurrentLink();
	}
	public String getCurrentServerId() {
		return webInformation.getCurrentServerId();
	}
	public Map<String, Object> getGet() {
		return webInformation.getGet();
	}
	public String getGlobalTitle() {
		return webInformation.getGlobalTitle();
	}
	public ArrayList<String> getHeader() {
		return webInformation.getHeader();
	}
	public String getHost() {
		return webInformation.getHost();
	}
	public String getNotification() {
		return webInformation.getNotification();
	}
	public Map<String, Object> getPost() {
		return webInformation.getPost();
	}
	public HttpServletRequest getRequest() {
		return webInformation.getRequest();
	}
	public HttpServletResponse getResponse() {
		return webInformation.getResponse();
	}
	public IGuild getServer() {
		return webInformation.getServer();
	}
	public ServerConfig getServerConfig() {
		return webInformation.getServerConfig();
	}
	public BotServlet getServlet() {
		return webInformation.getServlet();
	}
	public String getSession_Id() {
		return webInformation.getSession_Id();
	}
	public Map<String, String> getSessions() {
		return webInformation.getSessions();
	}
	public String getTitle() {
		return webInformation.getTitle();
	}
	public ToolsHandler getToolsHandler() {
		return toolsHandler;
	}
	public boolean isConnect(){
		return webInformation.isConnect();
	}
	public void setBotInstance(BotInstance botInstance) {
		webInformation.setBotInstance(botInstance);
	}
	public void setContext(String context) {
		webInformation.setContext(context);
	}
	public void setCurrentLink(String currentLink) {
		webInformation.setCurrentLink(currentLink);
	}
	public void setGlobalTitle(String globalTitle) {
		webInformation.setGlobalTitle(globalTitle);
	}
	public void setNotification(String notification) {
		webInformation.setNotification(notification);
	}
	public void setServerConfig(ServerConfig serverConfig) {
		webInformation.setServerConfig(serverConfig);
	}
	public void setServlet(BotServlet servlet) {
		webInformation.setServlet(servlet);
	}
	public void setSession(String key, String value){
		webInformation.setSession(key, value);
	}
	public void setTitle(String title) {
		webInformation.setTitle(title);
	}
}
