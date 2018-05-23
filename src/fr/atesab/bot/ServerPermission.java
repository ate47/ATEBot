package fr.atesab.bot;

import java.util.List;

public class ServerPermission {
	private List<String> permission;
	private String identifier;
	
	public ServerPermission(List<String> permission, String identifier) {
		this.permission = permission;
		this.identifier = identifier;
	}
	public String getIdentifier() {
		return identifier;
	}
	public boolean hasPermission(String perm) {
		return permission.contains(perm) || permission.contains("admin"); 
	}
}
