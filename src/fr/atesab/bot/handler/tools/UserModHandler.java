package fr.atesab.bot.handler.tools;

import java.awt.Color;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import fr.atesab.bot.Account;
import fr.atesab.bot.WebToolInformation;
import fr.atesab.bot.WebInformation;
import fr.atesab.bot.handler.AccountsPanelHandler;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

@SuppressWarnings("deprecation")
public class UserModHandler extends ToolHandler {
	public static final String TOOL_NAME = "usermod";

	public static String name(WebInformation info, IUser u, IGuild g) {
		return name(info, u, g, true);
	}

	public static String name(WebInformation info, IUser u, IGuild g, boolean showRealName) {
		String s = StringEscapeUtils.escapeHtml4(u.getName() + "#" + u.getDiscriminator());
		if (!u.getName().equals(u.getDisplayName(g)) && showRealName)
			s += " (" + u.getDisplayName(g) + ") ";
		if (u.isBot())
			s += " <span class='usermod_bot'>" + info.getBotServer().getLanguage("tools.usermod.bot").toUpperCase()
					+ "</span>";
		try {
			if (g.getClient().getApplicationClientID().equals(String.valueOf(u.getLongID())))
				s += " <span class='usermod_me'>" + info.getBotServer().getLanguage("tools.usermod.me").toUpperCase()
						+ "</span>";
		} catch (Exception e) {
		}
		return s;
	}

	public static String serverElement(IGuild g, Account acc, WebInformation info) {
		boolean guildmod = acc.hasPerm("guildmod") && info.getServerConfig() != null
				&& info.getServerConfig().tools.contains(GuildModHandler.TOOL_NAME);
		boolean usermod = acc.hasPerm("manageclients") && info.getServerConfig() != null
				&& info.getServerConfig().tools.contains(UserModHandler.TOOL_NAME);
		String s = "<table class='table_list usermod'><tr class='table_list_top'>" + "<td>"
				+ info.getBotServer().getLanguage("tools.usermod.guild.icon") + "</td>" + "<td>"
				+ info.getBotServer().getLanguage("tools.usermod.guild.name") + "</td>";
		if (guildmod)
			s += "<td class='usermod_gtool'>" + info.getBotServer().getLanguage("tools.usermod.guild.tool") + "</td>";
		s += "<td class='usermod_id'>" + info.getBotServer().getLanguage("tools.usermod.guild.id") + "</td></tr>"
				+ "\n<tr>" + "\n<td class='usermod_gimg'><img src='" + g.getIconURL() + "' alt='"
				+ info.getBotServer().getLanguage("tools.usermod.guild.icon") + "' /></td>" + "\n<td>" + g.getName()
				+ "</td>";
		s += "\n<td>\n <ul>";
		if (guildmod)
			s += "\n  <a href='tools.ap" + info.getCurrentLink() + "&app=" + GuildModHandler.TOOL_NAME
					+ "#text_edit'><li>" + info.getBotServer().getLanguage("tools.guildmod.channel.text") + "</li></a>"
					+ "\n  <a href='tools.ap" + info.getCurrentLink() + "&app=" + GuildModHandler.TOOL_NAME
					+ "#voice_edit'><li>" + info.getBotServer().getLanguage("tools.guildmod.channel.voice")
					+ "</li></a>" + "\n  <a href='tools.ap" + info.getCurrentLink() + "&app="
					+ GuildModHandler.TOOL_NAME + "#role_edit'><li>"
					+ info.getBotServer().getLanguage("tools.guildmod.role") + "</li></a>" + "\n  <a href='tools.ap"
					+ info.getCurrentLink() + "&app=" + GuildModHandler.TOOL_NAME + "#invites'><li>"
					+ info.getBotServer().getLanguage("tools.guildmod.invites") + "</li></a>" + "\n  <a href='tools.ap"
					+ info.getCurrentLink() + "&app=" + GuildModHandler.TOOL_NAME + "#bans'><li>"
					+ info.getBotServer().getLanguage("tools.guildmod.bans") + "</li></a>";
		if (usermod)
			s += "\n  <a href='tools.ap" + info.getCurrentLink() + "&app=" + UserModHandler.TOOL_NAME
					+ "#user_edit'><li>" + info.getBotServer().getLanguage("tools.usermod") + "</li></a>";
		s += "\n </ul>\n</td>";
		s += "\n<td>" + g.getLongID() + "</td>" + "\n</tr></table>";
		return s;
	}

	private String getIconUrl(String url) {
		if (url.toLowerCase().endsWith("null.png"))
			url = "https://discordapp.com/assets/0e291f67c9274a1abdddeb3fd919cbaa.png";
		return url;
	}

	public String handle(WebToolInformation info) throws IOException {
		String s = "";
		if (info.getClient() == null)
			return s;
		List<IGuild> gl = info.getBotInstance().getClient().getGuilds();
		gl.sort(new Comparator<IGuild>() {
			public int compare(IGuild o1, IGuild o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});
		s += "\n<h3>" + info.getBotServer().getLanguage("tools.usermod.guilds") + " : </h3>";
		IGuild g = info.getServer();
		if (g != null) {
			s += "\n<hr />\n" + serverElement(g, info.getAccount(), info);
			List<IUser> ul = g.getTotalMemberCount() < 60 ? g.getUsers() : null;
			if (info.getPost().containsKey("action") && info.getPost().get("action").equals("addrole")) {
				try {
					info.setNotification(info.getBotServer().getLanguage("tools.usermod.role.add.notif"));
					long rid = Long.valueOf((String) info.getPost().getOrDefault("role_id", ""));
					long uid = Long.valueOf((String) info.getPost().getOrDefault("user_id", ""));
					g.getUserByID(uid).addRole(g.getRoleByID(rid));
				} catch (Exception e) {
					e.printStackTrace();
					info.setNotification(info.getBotServer().getLanguage("tools.usermod.role.add.notif.error"));
				}
			}
			if (ul != null) {
				s += "\n<table id='user_edit' class='table_list usermod'>\n<tr class='table_list_top'>" + "<td>"
						+ info.getBotServer().getLanguage("tools.usermod.avatar") + "</td>" + "<td>"
						+ info.getBotServer().getLanguage("tools.usermod.username") + "</td>"
						+ "<td class='usermod_account'>" + info.getBotServer().getLanguage("tools.usermod.account")
						+ "</td>" + "<td class='usermod_role'>" + info.getBotServer().getLanguage("tools.usermod.role")
						+ "</td>" + "<td class='usermod_id'>" + info.getBotServer().getLanguage("tools.usermod.id")
						+ "</td></tr>";
				ul.sort(new Comparator<IUser>() {
					public int compare(IUser o1, IUser o2) {
						return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
					}
				});
				for (IUser u : ul) {
					s += "\n<tr id='" + u.getLongID() + "'><td class='usermod_img'><img src='"
							+ getIconUrl(u.getAvatarURL().replaceAll("webp", "png")) + "' alt='"
							+ info.getBotServer().getLanguage("tools.usermod.avatar") + "' /></td>" + "<td>"
							+ name(info, u, g) + "</td>";
					List<IRole> rl = u.getRolesForGuild(g);
					rl.sort(new Comparator<IRole>() {
						public int compare(IRole o1, IRole o2) {
							return o2.getPosition() - o1.getPosition();
						}
					});
					s += "\n<td>";
					for (Account a : info.getBotServer().getAccountsByUser(u, g)) {
						if (info.getAccount().hasPerm("users"))
							s += "<a href='users.ap?u=" + a.name + "'>";
						s += "<li class='acc_tree_user'>" + a.name + "</li>";
						if (info.getAccount().hasPerm("users"))
							s += "</a>";
					}
					s += "\n</td><td>";
					for (IRole r : rl) {
						Color c = r.getColor();
						s += "\n<a href='tools.ap" + info.getCurrentLink() + "&app=guildmod#role_" + r.getLongID()
								+ "' " + "style='display:block;padding:2px;margin:1px;border:1px solid rgb("
								+ c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + "); "
								+ "background-color:rgba(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue()
								+ ", 0.25)'>" + r.getName() + "</a>";

					}
					s += "<form method='post' action=''>\n<input type='hidden' value='addrole' name='action' />"
							+ "\n<input type='hidden' value='" + u.getLongID() + "' name='user_id' />" + "\n"
							+ AccountsPanelHandler.getRoleSelect("", "role_id", g) + "\n"
							+ "<br /><input type='submit' value='"
							+ info.getBotServer().getLanguage("tools.usermod.role.add") + "' />\n</form>";

					s += "\n</td>\n<td>" + u.getLongID() + "</td></tr>";
				}
			} else {
				s += "\n<h3>" + info.getBotServer().getLanguage("tools.usermod.toomuchusers") + "</h3>";
			}
		}
		return s + "</table>";
	}

	public boolean needConnection() {
		return true;
	}

	public String neededPermission() {
		return "manageclients";
	}

	public String toolName() {
		return TOOL_NAME;
	}
}
