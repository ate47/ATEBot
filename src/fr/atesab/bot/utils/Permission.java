package fr.atesab.bot.utils;

public class Permission implements Comparable<Permission> {
	public final String name;
	public final String group;
	public final boolean serverPermission;

	public Permission(String name, String group) {
		this(name, group, false);
	}
	public Permission(String name, String group, boolean serverPermission) {
		this.name = name;
		this.group = group;
		this.serverPermission = serverPermission;
	}
	@Override
	public int compareTo(Permission per) {
		return name.compareTo(per.name);
	}
	
	public String getGroupLangName() {
		return "group."+group;
	}
	public String getLangName() {
		return "perm."+name;
	}
	
}
