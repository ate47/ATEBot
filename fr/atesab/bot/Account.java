package fr.atesab.bot;

import java.util.ArrayList;

public class Account {
	public String name;
	public String groupId;
	public String userId;
	public String hash;
	public ArrayList<String> perms;
	public Account(String name, String groupId, String userId, String[] perms, String hash) {
		this.name = name;
		this.groupId = groupId;
		this.userId = userId;
		this.perms = new ArrayList<String>();
		for (int i = 0; i < perms.length; i++) {
			this.perms.add(perms[i]);
		}
		this.hash = hash;
	}
	public boolean hasPerm(String perm){
		if(perm==null) return true;
		for (int i = 0; i < perms.size(); i++) {
			if(perms.get(i).equals(perm))return true;
			if(perms.get(i).equals("admin"))return true;
		}
		return false;
	}
	public void setPerm(String perm){
		ArrayList<String> perms=new ArrayList<String>();
		for (int i = 0; i < this.perms.size(); i++) {
			if(!perms.contains(this.perms.get(i)))perms.add(this.perms.get(i));
		}
		for (int i = 0; i < perms.size(); i++) {
			if(perms.get(i).equals(perm))return;
		}
		perms.add(perm);
		this.perms=perms;
	}
	public void removePerm(String perm){
		ArrayList<String> perms=new ArrayList<String>();
		for (int i = 0; i < this.perms.size(); i++) {
			if(!perms.contains(this.perms.get(i)))perms.add(this.perms.get(i));
		}
		for (int i = 0; i < perms.size(); i++) {
			if(perms.get(i).equals(perm))perms.remove(i);
		}
		this.perms = perms;
	}
}
