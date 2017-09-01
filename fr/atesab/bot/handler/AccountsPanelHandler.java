package fr.atesab.bot.handler;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.atesab.bot.Account;
import fr.atesab.bot.Main;
import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class AccountsPanelHandler extends WebHandler {

	@Override
	public String handle(WebInformation info) throws IOException {
		String content = "";
		String notification = null;
		if(info.getGet().containsKey("a") && info.getGet().get("a").toString().toLowerCase().equals("dsc") && info.getPost().containsKey("delete") && info.getPost().get("delete")!=null && Main.getAccountByName(info.getPost().get("delete").toString())!=null) {
			String un = info.getPost().get("delete").toString();
			if(Main.getAccountByName(un).hasPerm("admin")) {
				notification = Main.lang.getLangage("panel.users.delete.msg.admin");
			} else {
				Main.deleteAccount(un);
				Main.saveConfig();
				notification = Main.lang.getLangage("panel.users.delete.msg.delete");
			}
		}
		Account u;
		if(info.getGet().containsKey("u") && (u=Main.getAccountByName(info.getGet().get("u").toString()))!=null) {
			content="<h3><a href='?u="+u.name+"'>"+u.name.toUpperCase()+"</a>";
			if(info.getGet().containsKey("a") && info.getGet().get("a").toString().toLowerCase().equals("per")) {
				content+=" >> "+Main.lang.getLangage("panel.users.perm").toUpperCase()+"</h3>\n";
				if(info.getAccount().hasPerm("perm")) {
					if(u.hasPerm("admin")) {
						content+="<p>"+Main.lang.getLangage("perm.admin")+"</p>";
					} else {
						if(info.getPost().containsKey("pm_mod")) {
							u.perms.clear();
							for (String[] p: Main.perms)
								if(p.length==2 && info.getPost().getOrDefault(p[0], "off").toString().equals("on"))
									u.perms.add(p[0]);
							Main.saveConfig();
							notification = Main.lang.getLangage("panel.users.perm.mod");
						}
						content+="<form action='' method='post'>\n<input type='hidden' name='pm_mod' value='true' />\n"
								+ "<p>"+Main.lang.getLangage("panel.users.perm.list")+" : </p>";
						Map<String,ArrayList<String>> map = new TreeMap<String,ArrayList<String>>();
						for (String[] p: Main.perms) {
							if(p.length==2) {
								ArrayList<String> array = map.getOrDefault(p[1], new ArrayList<String>());
								array.add(p[0]);
								map.put(p[1], array);
							}
						}
						for (String group : map.keySet()) {
							content+="\n<p>> "+Main.lang.getLangage("group."+group)+"</p>";
							for (String p: map.get(group))
								content+="\n<input type='checkbox' name='"+p+"' "+check(u.hasPerm(p))+" /> - "+Main.lang.getLangage("perm."+p)+"<br />";
						}
						content+="\n<p><input type='submit' value='"+Main.lang.getLangage("panel.save")+"' /></p>";
					}
				} else {
					content = Main.lang.getLangage("panel.noperm");
				}
			} else if(info.getGet().containsKey("a") && info.getGet().get("a").toString().toLowerCase().equals("mdp")) {
				if(Main.mapContainKeys(info.getPost(), new String[] {"nw_nps","nw_nps2"})) {
					if(info.getPost().get("nw_nps")!=null && info.getPost().get("nw_nps").equals(info.getPost().get("nw_nps2"))) {
						u.hash = Main.MD5(info.getPost().get("nw_nps").toString());
						if(info.getAccount().equals(u)) info.setSession("log_ps", u.hash);
					Main.saveConfig();
						notification = Main.lang.getLangage("panel.users.msg.ps");
					} else {
						notification = Main.lang.getLangage("panel.users.error.create.pw");
					}
				}
				content+=" >> "+Main.lang.getLangage("panel.password").toUpperCase()+"</h3>\n"
						+ "<form action='' method='post'>\n<table>\n"
						+ "\n<tr><td>"+Main.lang.getLangage("panel.nPassword")+" : </td><td><input type='password' name='nw_nps' /></td></tr>"
						+ "\n<tr><td>"+Main.lang.getLangage("panel.nPassword2")+" : </td><td><input type='password' name='nw_nps2' /></td></tr>"
						+ "</table>\n<input type='submit' value='"+Main.lang.getLangage("panel.save")+"' />\n</form>";
			} else if(info.getGet().containsKey("a") && info.getGet().get("a").toString().toLowerCase().equals("dsc")) {
				if(Main.mapContainKeys(info.getPost(), new String[]{"roleid","userid"})) {
					if(info.getPost().get("roleid")!=null)u.groupId = info.getPost().get("roleid").toString(); else u.groupId="";
					if(info.getPost().get("userid")!=null)u.userId = info.getPost().get("userid").toString(); else u.userId="";
					notification = Main.lang.getLangage("panel.users.options.save");
					Main.saveConfig();
				}
				content+=" >> "+Main.lang.getLangage("panel.users.options").toUpperCase()+"</h3>\n"
						+ "<form method='post' action=''>\n"
						+ "<table>\n"
						+ "<tr><td>"+Main.lang.getLangage("panel.users.options.userid")+" : </td><td>"+getUserSelect(u.userId, "userid")+"</td></tr>\n"
						+ "<tr><td>"+Main.lang.getLangage("panel.users.options.roleid")+" : </td><td>"+getRoleSelect(u.groupId, "roleid")+"</td></tr>\n"
						+ "</table>\n<input type='submit' value='"+Main.lang.getLangage("panel.save")+"' />\n</form>";
				if(!u.hasPerm("admin"))
					content+="\n<form method='post' action=''><input type='hidden' name='delete' value='"+u.name+"' /><input type='submit' value='"+Main.lang.getLangage("panel.users.delete")+"' /></form>";
			} else {
				content+="</h3>\n"
						+ "<p>"+Main.lang.getLangage("panel.users.optList")+" : </p>\n"
						+ "<ul class='acc_tree'>\n";
				if(info.getAccount().hasPerm("perm")) content+="<a href='?u="+u.name+"&a=per'>"
						+ "<li class='acc_tree_app'>"+Main.lang.getLangage("panel.users.perm")+"</li>\n";
				content+="<a href='?u="+u.name+"&a=mdp'>"
						+ "<li class='acc_tree_app'>"+Main.lang.getLangage("panel.password")+"</li>\n"
						+ "<a href='?u="+u.name+"&a=dsc'>"
						+ "<li class='acc_tree_app'>"+Main.lang.getLangage("panel.users.options")+"</li>\n</ul>";
			}
		} else {
			if(Main.mapContainKeys(info.getPost(), new String[] {"nw_us","nw_ps","nw_ps2"})) {
				if(Main.getAccountByName(info.getPost().get("nw_us").toString())==null){
					if(info.getPost().get("nw_ps")!=null && info.getPost().get("nw_ps2") !=null && info.getPost().get("nw_ps").toString().equals(info.getPost().get("nw_ps2").toString())) {
						Main.accounts.add(new Account(info.getPost().get("nw_us").toString(), "", "", new String[] {}, Main.MD5(info.getPost().get("nw_ps").toString())));
						Main.saveConfig();
						notification = Main.lang.getLangage("panel.users.msg.create");
					} else {
						notification = Main.lang.getLangage("panel.users.error.create.pw");
					}
				} else {
					notification = Main.lang.getLangage("panel.users.error.create");
				}
			}
			content="<h3>"+Main.lang.getLangage("panel.users.newUser").toUpperCase()+"</h3>\n"
					+ "<form method='POST' action=''>\n<table>\n"
					+ "<tr><td>"+Main.lang.getLangage("panel.username")+" : </td><td><input type='text' name='nw_us' /></td></tr>"
					+ "<tr><td>"+Main.lang.getLangage("panel.password")+" : </td><td><input type='password' name='nw_ps' /></td></tr>"
					+ "<tr><td>"+Main.lang.getLangage("panel.password2")+" : </td><td><input type='password' name='nw_ps2' /></td></tr>"
					+ "\n</table>\n<input type='submit' value='"+Main.lang.getLangage("panel.users.newUser.create")+"' />\n</form>";
		}
		String s = "<table class='acc'>\n"
				+ "<tr>\n<td class='acc_tree acc_left'>\n"
				+ "<ul><a href='"+info.getHost()+"/users.ap'><li class='acc_tree_add'>"+Main.lang.getLangage("panel.users.newUser")+"</li></a>\n";
		for (Account a: Main.accounts) {
			s+="<a href='?u="+a.name+"'><li class='acc_tree_user'>"+a.name+"\n<br /><ul>\n";
				if(info.getAccount().hasPerm("perm")) s+= "<a href='?u="+a.name+"&a=per'>"
						+ "<li class='acc_tree_app'>"+Main.lang.getLangage("panel.users.perm")+"</li></a>\n";
				s+="<a href='?u="+a.name+"&a=mdp'>"
						+ "<li class='acc_tree_app'>"+Main.lang.getLangage("panel.password")+"</li></a>\n"
						+ "<a href='?u="+a.name+"&a=dsc'>"
						+ "<li class='acc_tree_app'>"+Main.lang.getLangage("panel.users.options")+"</li></a>\n"
						+ "</ul></li></a>";
		}
		s+="</ul>"
				+ "</td>\n<td class='acc_body'>\n"+content+"\n</td>\n</tr>\n</table>";
		info.setTitle("panel.users");
		info.setNotification(notification);
		return PanelHandler.buildPanel(s, info);
	}
	public static String getRoleSelect(String id, String name) {
		String s = "<select value='"+id+"' name='"+name+"'>\n<option value=''> </option>";
		List<IGuild> gl = Main.client.getGuilds();
		gl.sort(new Comparator<IGuild>() {
			public int compare(IGuild o1, IGuild o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		for (IGuild g: gl) {
			List<IRole> rl = g.getRoles();
			rl.sort(new Comparator<IRole>() {
				public int compare(IRole o1, IRole o2) {
					return o2.getPosition()-o1.getPosition();
				}
			});
			s+="\n<option disabled>"+g.getName()+"</option>";
			for (IRole r: rl) {
				String rid = String.valueOf(r.getLongID());
				Color c = r.getColor();
				s+="<option style='background-color:rgba("+c.getRed()+", "+c.getGreen()+", "+c.getBlue()+", 0.25)' value='"+rid+"'";
				if(rid.equals(id))s+=" selected";
				s+=">- "+r.getName()+"</option>";
			}
		}
		return s+"\n</select>";
	}
	public static String getUserSelect(String id, String name) {
		String s = "<select value='"+id+"' name='"+name+"'>\n<option value=''> </option>";
		List<IGuild> gl = Main.client.getGuilds();
		gl.sort(new Comparator<IGuild>() {
			public int compare(IGuild o1, IGuild o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		for (IGuild g: gl) {
			List<IUser> ul = g.getUsers();
			ul.sort(new Comparator<IUser>() {
				public int compare(IUser o1, IUser o2) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
			});
			s+="\n<option disabled>"+g.getName()+"</option>";
			for (IUser u: ul) {
				String uid = String.valueOf(u.getLongID());
				s+="<option value='"+uid+"'";
				if(uid.equals(id))s+=" selected";
				s+=">- "+u.getName()+"</option>";
			}
		}
		return s+"\n</select>";
	}
	@Override
	public String neededPermission() {
		return "users";
	}

	@Override
	public boolean needConnection() {
		return true;
	}
	
	private static String check(boolean a){
		if(a)return "checked";
		return "";
	}

}
