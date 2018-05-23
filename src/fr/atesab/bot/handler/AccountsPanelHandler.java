package fr.atesab.bot.handler;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.atesab.bot.Account;
import fr.atesab.bot.BotInstance;
import fr.atesab.bot.BotServer;
import fr.atesab.bot.WebHandler;
import fr.atesab.bot.WebInformation;
import fr.atesab.bot.utils.Permission;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class AccountsPanelHandler extends WebHandler {

	public static String check(boolean a){
		if(a)return "checked";
		return "";
	}
	public static String getRoleSelect(String id, String name, IGuild guild) {
		String s = "<select value='"+id+"' name='"+name+"'>\n<option value=''> </option>";
		List<IRole> rl = guild.getRoles();
		rl.sort(new Comparator<IRole>() {
			public int compare(IRole o1, IRole o2) {
				return o2.getPosition()-o1.getPosition();
			}
		});
		for (IRole r: rl) {
			String rid = String.valueOf(r.getLongID());
			Color c = r.getColor();
			s+="<option style='background-color:rgba("+c.getRed()+", "+c.getGreen()+", "+c.getBlue()+", 0.25)' value='"+rid+"'";
			if(rid.equals(id))s+=" selected";
			s+=">- "+r.getName()+"</option>";
		}
		return s+"\n</select>";
	}
	public static String getRoleSelect(WebInformation info, String id, String name) {
		String s = "<select value='"+id+"' name='"+name+"'>\n<option value=''> </option>";
		List<IGuild> gl = new ArrayList<IGuild>();
		for (BotInstance instance: info.getBotServer().getInstances()) {
			if(instance.getClient()!=null) gl.addAll(instance.getClient().getGuilds());
		}
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
	public static String getUserSelect(WebInformation info, String id, String name) {
		String s = "<select value='"+id+"' name='"+name+"'>\n<option value=''> </option>";
		List<IGuild> gl = new ArrayList<IGuild>();
		for (BotInstance instance: info.getBotServer().getInstances()) {
			if(instance.getClient()!=null) gl.addAll(instance.getClient().getGuilds());
		}
		gl.sort(new Comparator<IGuild>() {
			public int compare(IGuild o1, IGuild o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		for (IGuild g: gl) {
			List<IUser> ul = g.getTotalMemberCount()<60?g.getUsers():null;
			if(ul!=null) {
				ul.sort(new Comparator<IUser>() {
					public int compare(IUser o1, IUser o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
				s+="\n<option disabled>"+g.getName()+"</option>";
				for (IUser u: ul) {
					String uid = String.valueOf(u.getLongID());
					Color c = u.getColorForGuild(g);
					s+="<option style='background-color:rgba("+c.getRed()+", "+c.getGreen()+", "+c.getBlue()+", 0.25)'value='"+uid+"'";
					if(uid.equals(id))s+=" selected";
					s+=">- "+u.getName()+"</option>";
				}
			} else {
				s+="\n<option disabled>"+info.getBotServer().getLanguage("tools.usermod.toomuchusers")+"</option>";
			}
		}
		return s+"\n</select>";
	}
	@Override
	public String handle(WebInformation info) throws IOException {
		String content = "";
		String notification = null;
		if(info.getGet().containsKey("a") && info.getGet().get("a").toString().toLowerCase().equals("dsc") && info.getPost().containsKey("delete") && info.getPost().get("delete")!=null && info.getBotServer().getAccountByName(info.getPost().get("delete").toString())!=null) {
			String un = info.getPost().get("delete").toString();
			if(info.getBotServer().getAccountByName(un).hasPerm("admin")) {
				notification = info.getBotServer().getLanguage("panel.users.delete.msg.admin");
			} else {
				info.getBotServer().deleteAccount(un);
				info.getBotServer().saveConfig();
				notification = info.getBotServer().getLanguage("panel.users.delete.msg.delete");
			}
		}
		Account u;
		if(info.getGet().containsKey("u") && (u=info.getBotServer().getAccountByName(info.getGet().get("u").toString()))!=null) {
			content="<h3><a href='?u="+u.name+"'>"+u.name.toUpperCase()+"</a>";
			if(info.getGet().containsKey("a") && info.getGet().get("a").toString().toLowerCase().equals("per")) {
				content+=" >> "+info.getBotServer().getLanguage("panel.users.perm").toUpperCase()+"</h3>\n";
				if(info.getAccount().hasPerm("perm")) {
					if(u.hasPerm("admin")) {
						content+="<p>"+info.getBotServer().getLanguage("perm.admin")+"</p>";
					} else {
						if(info.getPost().containsKey("pm_mod")) {
							u.perms.clear();
							for (Permission p: BotServer.perms)
								if(info.getPost().getOrDefault(p.name, "off").toString().equals("on"))
									u.perms.add(p.name);
							info.getBotServer().saveConfig();
							notification = info.getBotServer().getLanguage("panel.users.perm.mod");
						}
						content+="<form action='' method='post'>\n<input type='hidden' name='pm_mod' value='true' />\n"
								+ "<p>"+info.getBotServer().getLanguage("panel.users.perm.list")+" : </p>";
						Map<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
						for (Permission p: BotServer.perms) {
								String group = info.getBotServer().getLanguage(p.getGroupLangName());
								ArrayList<String> array = map.getOrDefault(group, new ArrayList<String>());
								array.add(p.name);
								map.put(group, array);
						}
						map = new TreeMap<String,ArrayList<String>>(map);
						for (String group : map.keySet()) {
							content+="\n<p>> "+group+"</p>";
							List<String> groups = map.get(group);
							groups.sort(new Comparator<String>() {
								public int compare(String o1, String o2) {
									return info.getBotServer().getLanguage("perm."+o2).compareToIgnoreCase(info.getBotServer().getLanguage("perm."+o1));
								}
							});
							for (String p: groups)
								content+="\n<input type='checkbox' name='"+p+"' "+check(u.hasPerm(p))+" /> - "+info.getBotServer().getLanguage("perm."+p)+"<br />";
						}
						content+="\n<p><input type='submit' value='"+info.getBotServer().getLanguage("panel.save")+"' /></p>";
					}
				} else {
					content = info.getBotServer().getLanguage("panel.noperm");
				}
			} else if(info.getGet().containsKey("a") && info.getGet().get("a").toString().toLowerCase().equals("bot")) {
				if(info.getPost().containsKey("action") && ((String)info.getPost().get("action")).equals("save_access")) {
					for (BotInstance instance: info.getBotServer().getInstances()) {
						if(instance.getClient()==null)continue;
						if(info.getPost().getOrDefault(instance.getConfig().getName(), "off").equals("on"))u.setBotAccess(instance.getConfig().getName());
						else u.removeBotAccess(instance.getConfig().getName());
						for (IGuild g: instance.getClient().getGuilds()) {
							String server = instance.getConfig().getName()+"_"+g.getLongID();
							if(info.getPost().getOrDefault(server, "off").equals("on"))u.setServerAccess(server);
							else u.removeServerAccess(server);
						}
					}
					info.getBotServer().saveConfig();
					info.setNotification(info.getBotServer().getLanguage("panel.users.bot.access.save"));
				}
				content+=" >> "+info.getBotServer().getLanguage("panel.users.bot").toUpperCase()+"</h3>\n";
				if(u.hasPerm("admin")) {
					content+="<p>"+info.getBotServer().getLanguage("perm.admin")+"</p>";
				} else {
					content+="<form method='POST' action=''>\n";
					for (BotInstance instance: info.getBotServer().getInstances()) {
						if(instance.getClient()==null) continue;
						content+="\n<hr />\n<h3>"+instance.getConfig().getName()+"</h3>"
								+ "\n<p><input type='checkbox' "+check(u.hasBotAccess(instance.getConfig().getName()))+" ";
								if(u.hasPerm("ignorebotaccess"))content+="disabled ";
								else content+="name='"+instance.getConfig().getName()+"' ";
						content+="/> - "
								+ info.getBotServer().getLanguage("panel.users.bot.access.bot", instance.getConfig().getName())+"</p>";
						for (IGuild g: instance.getClient().getGuilds()) {
							content+="\n<input type='checkbox' "+
									check(u.hasServerAccess(instance.getConfig().getName(), String.valueOf(g.getLongID())))+" ";
								if(u.hasPerm("ignoreserveraccess"))content+="disabled ";
								else content+="name='"+instance.getConfig().getName()+"_"+g.getLongID()+"' ";
							content+="/> - "
								+ info.getBotServer().getLanguage("panel.users.bot.access.server", g.getName())+"<br />";
						}
					}
					content+="<hr />\n<input type='hidden' value='save_access' name='action' />"
							+ "\n<input type='submit' value='"+info.getBotServer().getLanguage("panel.save")+"' />\n</form>\n";
				}
				
			} else if(info.getGet().containsKey("a") && info.getGet().get("a").toString().toLowerCase().equals("mdp")) {
				if(BotServer.mapContainKeys(info.getPost(), new String[] {"nw_nps","nw_nps2"})) {
					if(info.getPost().get("nw_nps")!=null && info.getPost().get("nw_nps").equals(info.getPost().get("nw_nps2"))) {
						u.hash = BotServer.MD5(info.getPost().get("nw_nps").toString());
						if(info.getAccount().equals(u)) info.setSession("log_ps", u.hash);
					info.getBotServer().saveConfig();
						notification = info.getBotServer().getLanguage("panel.users.msg.ps");
					} else {
						notification = info.getBotServer().getLanguage("panel.users.error.create.pw");
					}
				}
				content+=" >> "+info.getBotServer().getLanguage("panel.password").toUpperCase()+"</h3>\n"
						+ "<form action='' method='post'>\n<table>\n"
						+ "\n<tr><td>"+info.getBotServer().getLanguage("panel.nPassword")+" : </td><td><input type='password' name='nw_nps' /></td></tr>"
						+ "\n<tr><td>"+info.getBotServer().getLanguage("panel.nPassword2")+" : </td><td><input type='password' name='nw_nps2' /></td></tr>"
						+ "</table>\n<input type='submit' value='"+info.getBotServer().getLanguage("panel.save")+"' />\n</form>";
			} else if(info.getGet().containsKey("a") && info.getGet().get("a").toString().toLowerCase().equals("dsc")) {
				//TODO: Rename
				if(BotServer.mapContainKeys(info.getPost(), new String[]{"roleid","userid"})) {
					if(info.getPost().get("roleid")!=null)u.groupId = info.getPost().get("roleid").toString(); else u.groupId="";
					if(info.getPost().get("userid")!=null)u.userId = info.getPost().get("userid").toString(); else u.userId="";
					notification = info.getBotServer().getLanguage("panel.users.options.save");
					info.getBotServer().saveConfig();
				}
				content+=" >> "+info.getBotServer().getLanguage("panel.users.options").toUpperCase()+"</h3>\n"
						+ "<form method='post' action=''>\n"
						+ "<table>\n"
						+ "<tr><td>"+info.getBotServer().getLanguage("panel.users.options.userid")+" : </td><td>"+getUserSelect(info, u.userId, "userid")+"</td></tr>\n"
						+ "<tr><td>"+info.getBotServer().getLanguage("panel.users.options.roleid")+" : </td><td>"+getRoleSelect(info, u.groupId, "roleid")+"</td></tr>\n"
						+ "</table>\n<input type='submit' value='"+info.getBotServer().getLanguage("panel.save")+"' />\n</form>";
				if(!u.hasPerm("admin"))
					content+="\n<form method='post' action=''><input type='hidden' name='delete' value='"+u.name+"' /><input type='submit' value='"+info.getBotServer().getLanguage("panel.users.delete")+"' /></form>";
			} else {
				content+="</h3>\n"
						+ "<p>"+info.getBotServer().getLanguage("panel.users.optList")+" : </p>\n"
						+ "<ul class='acc_tree'>\n";
				if(info.getAccount().hasPerm("perm")) content+="<a href='?u="+u.name+"&a=per'>"
						+ "<li class='acc_tree_app'>"+info.getBotServer().getLanguage("panel.users.perm")+"</li></a>\n"
						+ "<a href='?u="+u.name+"&a=bot'>"
						+ "<li class='acc_tree_app'>"+info.getBotServer().getLanguage("panel.users.bot")+"</li></a>\n";
				content+="<a href='?u="+u.name+"&a=mdp'>"
						+ "<li class='acc_tree_app'>"+info.getBotServer().getLanguage("panel.password")+"</li></a>\n"
						+ "<a href='?u="+u.name+"&a=dsc'>"
						+ "<li class='acc_tree_app'>"+info.getBotServer().getLanguage("panel.users.options")+"</li></a>\n</ul>";
			}
		} else {
			if(BotServer.mapContainKeys(info.getPost(), new String[] {"nw_us","nw_ps","nw_ps2"})) {
				if(info.getBotServer().getAccountByName(info.getPost().get("nw_us").toString())==null){
					if(info.getPost().get("nw_ps")!=null && info.getPost().get("nw_ps2") !=null && info.getPost().get("nw_ps").toString().equals(info.getPost().get("nw_ps2").toString())) {
						info.getBotServer().getAccounts().add(new Account(info.getPost().get("nw_us").toString(), "", "", new String[] {}, BotServer.MD5(info.getPost().get("nw_ps").toString())));
						info.getBotServer().saveConfig();
						notification = info.getBotServer().getLanguage("panel.users.msg.create");
					} else {
						notification = info.getBotServer().getLanguage("panel.users.error.create.pw");
					}
				} else {
					notification = info.getBotServer().getLanguage("panel.users.error.create");
				}
			}
			content="<h3>"+info.getBotServer().getLanguage("panel.users.newUser").toUpperCase()+"</h3>\n"
					+ "<form method='POST' action=''>\n<table>\n"
					+ "<tr><td>"+info.getBotServer().getLanguage("panel.username")+" : </td><td><input type='text' name='nw_us' /></td></tr>"
					+ "<tr><td>"+info.getBotServer().getLanguage("panel.password")+" : </td><td><input type='password' name='nw_ps' /></td></tr>"
					+ "<tr><td>"+info.getBotServer().getLanguage("panel.password2")+" : </td><td><input type='password' name='nw_ps2' /></td></tr>"
					+ "\n</table>\n<input type='submit' value='"+info.getBotServer().getLanguage("panel.users.newUser.create")+"' />\n</form>";
		}
		String s = "<table class='acc'>\n"
				+ "<tr>\n<td class='acc_tree acc_left'>\n"
				+ "<ul><a href='"+info.getHost()+"/bot/users.ap'><li class='acc_tree_add'>"+info.getBotServer().getLanguage("panel.users.newUser")+"</li></a>\n";
		for (Account a: info.getBotServer().getAccounts()) {
			s+="<a href='?u="+a.name+"'><li class='acc_tree_user'>"+a.name+"\n<br /><ul>\n";
				if(info.getAccount().hasPerm("perm")) s+= "<a href='?u="+a.name+"&a=per'>"
						+ "<li class='acc_tree_app'>"+info.getBotServer().getLanguage("panel.users.perm")+"</li></a>\n"
						+ "<a href='?u="+a.name+"&a=bot'>"
						+ "<li class='acc_tree_app'>"+info.getBotServer().getLanguage("panel.users.bot")+"</li></a>\n";
				s+="<a href='?u="+a.name+"&a=mdp'>"
						+ "<li class='acc_tree_app'>"+info.getBotServer().getLanguage("panel.password")+"</li></a>\n"
						+ "<a href='?u="+a.name+"&a=dsc'>"
						+ "<li class='acc_tree_app'>"+info.getBotServer().getLanguage("panel.users.options")+"</li></a>\n"
						+ "</ul></li></a>";
		}
		s+="</ul>"
				+ "</td>\n<td class='acc_body'>\n"+content+"\n</td>\n</tr>\n</table>";
		info.setTitle("panel.users");
		info.setNotification(notification);
		return PanelHandler.buildPanel(s, info);
	}

	@Override
	public boolean needConnection() {
		return true;
	}
	
	@Override
	public String neededPermission() {
		return "users";
	}

}
