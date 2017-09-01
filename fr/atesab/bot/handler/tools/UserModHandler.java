package fr.atesab.bot.handler.tools;

import java.awt.Color;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import fr.atesab.bot.Account;
import fr.atesab.bot.Main;
import fr.atesab.bot.WebInformation;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class UserModHandler extends ToolHandler {
	public String handle(WebInformation info) throws IOException {
		String s = "";
		List<IGuild> gl = Main.client.getGuilds();
		gl.sort(new Comparator<IGuild>() {
			public int compare(IGuild o1, IGuild o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});
		s+="\n<h3>"+Main.lang.getLangage("tools.usermod.guilds")+" : </h3>\n<table>";
		for (IGuild g: gl) {
			s+="\n<tr><td><img src='"+g.getIconURL()+"' style='width:16px;height:16px;'> - <a href='?app="+toolName()+"&guild="+g.getLongID()+"'>"+g.getName()+"</a></td></tr>";
		}
		s+="\n</table>";
		IGuild g = null;
		try {
			g=Main.client.getGuildByID(Long.valueOf((String)info.getGet().getOrDefault("guild", "")));;
		} catch (Exception e) {}
		if(g!=null) {
			s+="\n<hr />\n"+serverElement(g, info.getAccount())
				+ "\n<table id='user_edit' class='table_list usermod'>\n<tr class='table_list_top'>"
				+ "<td>"+Main.lang.getLangage("tools.usermod.avatar")+"</td>"
				+ "<td>"+Main.lang.getLangage("tools.usermod.username")+"</td>"
				+ "<td class='usermod_account'>"+Main.lang.getLangage("tools.usermod.account")+"</td>"
				+ "<td class='usermod_role'>"+Main.lang.getLangage("tools.usermod.role")+"</td>"
				+ "<td class='usermod_id'>"+Main.lang.getLangage("tools.usermod.id")+"</td></tr>";
			List<IUser> ul = g.getUsers();
			ul.sort(new Comparator<IUser>() {
				public int compare(IUser o1, IUser o2) {
					return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
				}
			});
			for (IUser u: ul) {
				s+="\n<tr id='"+u.getLongID()+"'><td class='usermod_img'><img src='"+getIconUrl(u.getAvatarURL())+"' alt='"+Main.lang.getLangage("tools.usermod.avatar")+"' /></td>"
					+ "<td>"+name(u, g)+"</td>";
				List<IRole> rl = u.getRolesForGuild(g);
				rl.sort(new Comparator<IRole>() {
					public int compare(IRole o1, IRole o2) {
						return o2.getPosition()-o1.getPosition();
					}
				});
				s+="\n<td>";
				for (Account a: Main.getAccountsByUser(u, g)) {
					if(info.getAccount().hasPerm("users"))s+="<a href='users.ap?u="+a.name+"'>";
					s+="<li class='acc_tree_user'>"+a.name+"</li>";
					if(info.getAccount().hasPerm("users"))s+="</a>";
				}
				s+="\n</td><td>";
				for (IRole r: rl) {
					Color c = r.getColor();
					s+="\n<div style='padding:2px;margin:1px;border:1px solid rgb("+c.getRed()+", "+c.getGreen()+", "+c.getBlue()+"); background-color:rgba("+c.getRed()+", "+c.getGreen()+", "+c.getBlue()+", 0.25)'>"+r.getName()+"</div>";
				}
				s+="\n</td>\n<td>"+u.getLongID()+"</td></tr>";
			}
		}
		return s+"</table>";
	}
	public static String name(IUser u, IGuild g) {
		return name(u,g,true);
	}
	public static String serverElement(IGuild g, Account acc) {
		String s = "<table class='table_list usermod'><tr class='table_list_top'>"
				+ "<td>"+Main.lang.getLangage("tools.usermod.guild.icon")+"</td>"
				+ "<td>"+Main.lang.getLangage("tools.usermod.guild.name")+"</td>";
			if(acc.hasPerm("guildmod"))s+="<td class='usermod_gtool'>"+Main.lang.getLangage("tools.usermod.guild.tool")+"</td>";
			s+="<td class='usermod_id'>"+Main.lang.getLangage("tools.usermod.guild.id")+"</td></tr>"
				+ "\n<tr>"
				+ "\n<td class='usermod_gimg'><img src='"+g.getIconURL()+"' alt='"+Main.lang.getLangage("tools.usermod.guild.icon")+"' /></td>"
				+ "\n<td>"+g.getName()+"</td>";
			if(acc.hasPerm("guildmod")) s+="\n<td>\n <ul>"
				+ "\n  <a href='tools.ap?app=guildmod&guild="+g.getLongID()+"#text_edit'><li>"+Main.lang.getLangage("tools.guildmod.channel.text")+"</li></a>"
				+ "\n  <a href='tools.ap?app=guildmod&guild="+g.getLongID()+"#voice_edit'><li>"+Main.lang.getLangage("tools.guildmod.channel.voice")+"</li></a>"
				+ "\n  <a href='tools.ap?app=guildmod&guild="+g.getLongID()+"#role_edit'><li>"+Main.lang.getLangage("tools.guildmod.role")+"</li></a>"
				+ "\n  <a href='tools.ap?app=usermod&guild="+g.getLongID()+"#user_edit'><li>"+Main.lang.getLangage("tools.usermod")+"</li></a>"
				+ "\n </ul>\n</td>";
			s+="\n<td>"+g.getLongID()+"</td>"
				+ "\n</tr></table>";
		return s;
	}
	public static String name(IUser u, IGuild g, boolean showRealName) {
		String s = StringEscapeUtils.escapeHtml4(u.getName()+"#"+u.getDiscriminator());
		if(!u.getName().equals(u.getDisplayName(g)) && showRealName)s+=" ("+u.getDisplayName(g)+") ";
		if(u.isBot())s+=" <span class='usermod_bot'>"+Main.lang.getLangage("tools.usermod.bot").toUpperCase()+"</span>";
		try{if(Main.appid==u.getLongID())s+=" <span class='usermod_me'>"+Main.lang.getLangage("tools.usermod.me").toUpperCase()+"</span>";}catch(Exception e) {}
		return s;
	}
	private String getIconUrl(String url) {
		if(url.toLowerCase().endsWith("null.webp")) url = "https://discordapp.com/assets/0e291f67c9274a1abdddeb3fd919cbaa.png";
		return url;
	}
	public String neededPermission() {
		return "manageclients";
	}
	public boolean needConnection() {
		return true;
	}
	public String toolName() {
		return "usermod";
	}

}
