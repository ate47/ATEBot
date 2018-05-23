package fr.atesab.bot;

import java.util.ArrayList;

public class Account {
	public String name;
	public String groupId;
	public String userId;
	public String hash;
	public ArrayList<String> perms = new ArrayList<String>();
	public ArrayList<String> botAccess = new ArrayList<String>();
	public ArrayList<String> serverAccess = new ArrayList<String>();
	public Account(String name, String groupId, String userId, String[] perms, String hash) {
		this.name = name;
		this.groupId = groupId;
		this.userId = userId;
		this.perms = new ArrayList<String>();
		for (int i = 0; i < perms.length; i++) {
			this.perms.add(perms[i]);
		}
		this.botAccess = new ArrayList<String>();
		this.serverAccess = new ArrayList<String>();
		this.hash = hash;
	}
	public boolean hasBotAccess(String bot) {
		if(botAccess==null) botAccess  = new ArrayList<String>();
		if(bot==null) return true;
		if(this.hasPerm("ignorebotaccess")) return true;
		for (String b: botAccess) {
			if(b.equals(bot))return true;
		}
		return false;
	}
	public boolean hasPerm(String perm){
		if(perms==null) perms  = new ArrayList<String>();
		if(perm==null) return true;
		for (String p: perms) {
			if(p.equals(perm))return true;
			if(p.equals("admin"))return true;
		}
		return false;
	}
	public boolean hasServerAccess(String bot, long server) {
		return hasServerAccess(bot, String.valueOf(server));
	}
	public boolean hasServerAccess(String bot, String server) {
		if(serverAccess==null) serverAccess  = new ArrayList<String>();
		if(server==null) return true;
		if(this.hasPerm("ignoreserveraccess")) return true;
		for (String s: serverAccess) {
			if(s.equals(bot+"_"+server))return true;
		}
		return false;
	}
	public boolean isAdmin() {
		return hasPerm("admin");
	}
	public void removeBotAccess(String bot){
		if(botAccess.contains(bot))botAccess.remove(bot);
	}
	public void removePerm(String perm){
		if(perms.contains(perm))perms.remove(perm);
	}
	public void removeServerAccess(String server){
		if(serverAccess.contains(server))serverAccess.remove(server);
	}
	public void setBotAccess(String bot){
		if(!botAccess.contains(bot))botAccess.add(bot);
	}
	public void setPerm(String perm){
		if(!perms.contains(perm))perms.add(perm);
	}
	public void setServerAccess(String server){
		if(!serverAccess.contains(server))serverAccess.add(server);
	}
}
