package fr.atesab.bot.handler.tools;

import java.awt.Color;
import java.io.IOException;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import fr.atesab.bot.Account;
import fr.atesab.bot.BotServer;
import fr.atesab.bot.WebInformation;
import fr.atesab.bot.WebToolInformation;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IExtendedInvite;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MissingPermissionsException;

public class GuildModHandler extends ToolHandler {
	public static final String TOOL_NAME = "guildmod";

	public static <T> boolean arrayContainObject(T object, T[] array) {
		for (T o1 : array) {
			if (o1.equals(object)) {
				return true;
			}
		}
		return false;
	}

	public static String getPerm(WebInformation info, IRole r, String title) {
		String s = "<a href='javascript:switchView(\"perm_" + r.getLongID() + "\")'>"
				+ info.getBotServer().getLanguage("tools.guildmod.perm") + "</a> - " + title
				+ "\n<div style='display:none;' id='perm_" + r.getLongID() + "'>" + "\n<form action='' method='POST'>"
				+ "\n<input type='hidden' name='action' value='perm_mod' />"
				+ "\n<input type='hidden' name='role' value='" + r.getLongID() + "' />"
				+ "\n<input type='hidden' name='guild' value='" + r.getGuild().getLongID() + "' />";
		Permissions[] pl = Permissions.class.getEnumConstants();
		Permissions[] pl2 = (Permissions[]) r.getPermissions().toArray(new Permissions[] {});
		for (Permissions p : pl) {
			String ck = "";
			if (arrayContainObject(p, pl2))
				ck = " checked";
			s += "\n<input type='checkbox'" + ck + " name='" + r.getLongID() + "_" + p.name() + "' /> - "
					+ info.getBotServer().getLanguage("tools.guildmod.perm." + p.name()) + "<br />";
		}
		return s + "\n<input type='submit' value='" + info.getBotServer().getLanguage("tools.guildmod.perm.save")
				+ "' /></form></div>";
	}

	public String handle(WebToolInformation info) throws IOException {
		String s = "";
		String action;
		if (info.getPost().containsKey("action") && (action = (String) info.getPost().get("action")) != null) {
			if (action.toLowerCase().equals("kick")
					&& BotServer.mapContainKeys(info.getPost(), new String[] { "user" })) {
				try {
					IUser u1 = info.getBotInstance().getClient()
							.getUserByID(Long.valueOf((String) info.getPost().get("user")));
					IVoiceChannel vc = info.getServer().createVoiceChannel(UUID.randomUUID().toString());
					u1.moveToVoiceChannel(vc);
					vc.delete();
					info.setNotification(
							info.getBotServer().getLanguage("tools.guildmod.channel.users.kick.notif", u1.getName()));
				} catch (Exception e) {
				}
			} else if (action.toLowerCase().equals("join") && info.getPost().containsKey("channel")) {
				try {
					IVoiceChannel vc = info.getServer()
							.getVoiceChannelByID(Long.valueOf((String) info.getPost().get("channel")));
					vc.join();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (action.toLowerCase().equals("duplicate") && info.getPost().containsKey("role")) {
				try {
					IGuild guild = info.getServer();
					IRole role = guild.getRoleByID(Long.valueOf((String) info.getPost().get("role")));
					IRole newRole = guild.createRole();
					newRole.changePermissions(role.getPermissions());
					info.setNotification(info.getBotServer().getLanguage("tools.guildmod.role.duplicate.notif",
							role.getName(), newRole.getName()));
				} catch (Exception e) {
				}
			} else if (action.toLowerCase().equals("role_delete") && info.getPost().containsKey("role")) {
				try {
					IRole role = info.getServer().getRoleByID(Long.valueOf((String) info.getPost().get("role")));
					String name = role.getName();
					role.delete();
					info.setNotification(info.getBotServer().getLanguage("tools.guildmod.role.delete.notif", name));
				} catch (Exception e) {
				}
			} else if (action.toLowerCase().equals("perm_mod") && info.getPost().containsKey("role")) {
				try {
					IRole role = info.getServer().getRoleByID(Long.valueOf((String) info.getPost().get("role")));
					Permissions[] pl = Permissions.class.getEnumConstants();
					EnumSet<Permissions> perms = EnumSet.noneOf(Permissions.class);
					for (Permissions p : pl) {
						if (info.getPost().containsKey(role.getLongID() + "_" + p.name()))
							perms.add(p);
					}
					role.changePermissions(perms);
					info.setNotification(info.getBotServer().getLanguage("tools.guildmod.perm.saved"));
				} catch (Exception e) {
				}
			}
		}
		if (info.getClient() != null) {
			List<IGuild> gl = info.getBotInstance().getClient().getGuilds();
			gl.sort(new Comparator<IGuild>() {
				public int compare(IGuild o1, IGuild o2) {
					return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
				}
			});
			s += "\n<h3>" + info.getBotServer().getLanguage("tools.usermod.guilds") + " : </h3>";
			IGuild g = info.getServer();
			if (g != null) {
				s += "\n<hr />\n" + UserModHandler.serverElement(g, info.getAccount(), info) + "\n"
						+ "\n<h3 id='role_edit'>" + info.getBotServer().getLanguage("tools.guildmod.role")
						+ "</h3>\n<table class='table_list'>\n" + "<tr class='table_list_top'>" + "<td>"
						+ info.getBotServer().getLanguage("tools.guildmod.role.name") + "</td>"
						+ "<td class='usermod_account'>"
						+ info.getBotServer().getLanguage("tools.guildmod.role.accounts") + "</td>"
						+ "<td class='usermod_id'>" + info.getBotServer().getLanguage("tools.guildmod.role.id")
						+ "</td></tr>";
				List<IRole> rl = g.getRoles();
				rl.sort(new Comparator<IRole>() {
					public int compare(IRole o1, IRole o2) {
						return o2.getPosition() - o1.getPosition();
					}
				});
				for (IRole r : rl) {
					Color c = r.getColor();
					s += "\n<tr id='role_" + r.getLongID() + "' style='border:1px solid rgb(" + c.getRed() + ", "
							+ c.getGreen() + ", " + c.getBlue() + "); background-color:rgba(" + c.getRed() + ", "
							+ c.getGreen() + ", " + c.getBlue() + ", 0.25)'>" + "<td>"
							+ "\n<form action='' method='POST' class='inline'>"
							+ "\n<input type='hidden' name='guild' value='" + g.getLongID() + "' />"
							+ "\n<input type='hidden' name='role' value='" + r.getLongID() + "' />"
							+ "\n<input type='hidden' name='action' value='duplicate' />"
							+ "\n<input type='submit' value='"
							+ info.getBotServer().getLanguage("tools.guildmod.role.duplicate") + "' />" + "\n</form>";
					if (!r.isEveryoneRole())
						s += "<form action='' method='POST' class='inline'>"
								+ "\n<input type='hidden' name='guild' value='" + g.getLongID() + "' />"
								+ "\n<input type='hidden' name='role' value='" + r.getLongID() + "' />"
								+ "\n<input type='hidden' name='action' value='role_delete' />"
								+ "\n<input type='submit' value='"
								+ info.getBotServer().getLanguage("tools.guildmod.role.delete") + "' />" + "\n</form> ";

					s += getPerm(info, r, r.getName()) + "\n</td><td><ul>";
					long l = r.getLongID();
					for (Account a : info.getBotServer().getAccounts()) {
						if (a.groupId.equals(String.valueOf(l))) {
							if (info.getAccount().hasPerm("users"))
								s += "<a href='users.ap?u=" + a.name + "'>";
							s += "<li class='acc_tree_user'>" + a.name + "</li>";
							if (info.getAccount().hasPerm("users"))
								s += "</a>";
						}
					}
					s += "</ul></td><td>" + r.getLongID() + "</td></tr>";
				}
				s += "</table>\n<h3 id='invites'>" + info.getBotServer().getLanguage("tools.guildmod.invites")
						+ "</h3>\n";
				try {
					List<IExtendedInvite> ei = g.getExtendedInvites();
					s += "<table class='table_list'>\n" + "<tr class='table_list_top'>" + "<td>"
							+ info.getBotServer().getLanguage("tools.guildmod.invites.link") + "</td>"
							+ "<td class='usermod_account'>"
							+ info.getBotServer().getLanguage("tools.guildmod.invites.uses") + "</td>"
							+ "<td class='usermod_id'>"
							+ info.getBotServer().getLanguage("tools.guildmod.invites.author") + "</td></tr>";
					for (IExtendedInvite i : ei) {
						s += "<tr><td><a href='https://discord.gg/" + i.getCode() + "'>https://discord.gg/"
								+ i.getCode() + "</a></td><td>" + i.getUses();
						if (i.getMaxUses() != 0)
							s += "/" + i.getMaxUses();
						s += "</td><td>";
						if (i.getInviter() != null)
							s += i.getInviter().getName() + "#" + i.getInviter().getDiscriminator();
						s += "</td></tr>";
					}
					s += "</table>";
				} catch (MissingPermissionsException e) {
					s += info.getBotServer().getLanguage("needperm",
							e.getMissingPermissions().stream()
									.map(p -> info.getBotServer().getLanguage("tools.guildmod.perm." + p.name()))
									.collect(Collectors.joining(", ")));
				}
				s += "\n<h3 id='bans'>" + info.getBotServer().getLanguage("tools.guildmod.bans")
						+ "</h3>\n<table class='table_list'>\n";
				try {
					List<IUser> bu = g.getBannedUsers();
					s += "<tr class='table_list_top'>" + "<td>"
							+ info.getBotServer().getLanguage("tools.guildmod.bans.username") + "</td>"
							+ "<td class='usermod_id'>" + info.getBotServer().getLanguage("tools.guildmod.role.id")
							+ "</td></tr>";
					for (IUser u : bu) {
						s += "<tr><td>" + u.getName() + "#" + u.getDiscriminator() + "</td><td>" + u.getLongID()
								+ "</td></tr>";
					}
					s += "</table>";
				} catch (MissingPermissionsException e) {
					s += info.getBotServer().getLanguage("needperm",
							e.getMissingPermissions().stream()
									.map(p -> info.getBotServer().getLanguage("tools.guildmod.perm." + p.name()))
									.collect(Collectors.joining(", ")));
				}

				s += "\n<h3 id='text_edit'>" + info.getBotServer().getLanguage("tools.guildmod.channel.text")
						+ "</h3>\n<table class='table_list'>\n" + "<tr class='table_list_top'>" + "<td>"
						+ info.getBotServer().getLanguage("tools.guildmod.channel.name") + "</td>"
						+ "<td class='usermod_id'>" + info.getBotServer().getLanguage("tools.guildmod.channel.id")
						+ "</td></tr>";
				for (IChannel ch : g.getChannels()) {
					s += "<tr><td># " + ch.getName();
					if (ch.isNSFW())
						s += "<span class='ch_nsfw'>" + info.getBotServer().getLanguage("tools.guildmod.channel.nsfw")
								+ "</span>";
					s += "</td><td>" + ch.getLongID() + "</td></tr>";
				}
				s += "</table><h3 id='voice_edit'>" + info.getBotServer().getLanguage("tools.guildmod.channel.voice")
						+ "</h3>\n<table class='table_list'>\n" + "<tr class='table_list_top'>" + "<td>"
						+ info.getBotServer().getLanguage("tools.guildmod.channel.name") + "</td>"
						+ "<td class='guildmod_ch_client'>"
						+ info.getBotServer().getLanguage("tools.guildmod.channel.users") + "</td>"
						+ "<td class='usermod_id'>" + info.getBotServer().getLanguage("tools.guildmod.channel.id")
						+ "</td></tr>";
				for (IVoiceChannel ch : g.getVoiceChannels()) {
					s += "<tr><td><form action='' method='POST' class='inline'>"
							+ "<input type='hidden' name='guild' value='" + g.getLongID() + "' />"
							+ "<input type='hidden' name='channel' value='" + ch.getLongID() + "' />"
							+ "<input type='hidden' name='action' value='join' />" + "<input type='submit' value='"
							+ info.getBotServer().getLanguage("tools.guildmod.channel.join") + "' />" + "</form>"
							+ ch.getName() + "</td><td>";
					List<IUser> ul = ch.getConnectedUsers();
					ul.sort(new Comparator<IUser>() {
						public int compare(IUser o1, IUser o2) {
							return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
						}
					});
					for (IUser u : ul) {
						s += "\n<div>\n<form action='' method='POST' class='inline'>"
								+ "<input type='hidden' name='guild' value='" + g.getLongID() + "' />"
								+ "<input type='hidden' name='user' value='" + u.getLongID() + "' />"
								+ "<input type='hidden' name='action' value='kick' />" + "<input type='submit' value='"
								+ info.getBotServer().getLanguage("tools.guildmod.channel.users.kick") + "' />"
								+ "</form>\n<img src='" + u.getAvatarURL().replaceAll("webp", "png")
								+ "' style='width:16px;height:16px;' /> ";
						if (info.getAccount().hasPerm("manageclients"))
							s += "<a href='tools.ap" + info.getCurrentLink() + "&app=" + toolName() + "#"
									+ u.getLongID() + "'>";
						s += UserModHandler.name(info, u, g, false);
						if (info.getAccount().hasPerm("manageclients"))
							s += "</a>";
						s += "</div>";
					}
					s += "\n</td><td>" + ch.getLongID() + "</td></tr>";
				}
			}
		}
		return s;
	}

	public boolean needConnection() {
		return true;
	}

	public String neededPermission() {
		return "guildmod";
	}

	public String toolName() {
		return TOOL_NAME;
	}
}
