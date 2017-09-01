package fr.atesab.bot.handler.tools;

import java.awt.Color;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import fr.atesab.bot.Account;
import fr.atesab.bot.Main;
import fr.atesab.bot.WebInformation;
import fr.atesab.bot.command.KickCommand;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class GuildModHandler extends ToolHandler {
	public String handle(WebInformation info) throws IOException {
		String s = "";
		String action;
		if(info.getPost().containsKey("action") && (action=(String) info.getPost().get("action"))!=null){
			if(action.toLowerCase().equals("kick") && Main.mapContainKeys(info.getPost(), new String[] {"guild","user"})) {
				try {
					long g = Long.valueOf((String)info.getPost().get("guild"));
					long u = Long.valueOf((String)info.getPost().get("user"));
				Random rnd = new Random();
					IVoiceChannel vc = Main.client.getGuildByID(g).createVoiceChannel(KickCommand.randomWord[rnd.nextInt(KickCommand.randomWord.length)]);
					IUser u1 = Main.client.getUserByID(u);
					u1.moveToVoiceChannel(vc);
					vc.delete();
					info.setNotification(Main.lang.getLangage("tools.guildmod.channel.users.kick.notif", u1.getName()));
				}catch (Exception e) {}
			} else if(action.toLowerCase().equals("join") && Main.mapContainKeys(info.getPost(), new String[] {"channel","guild"})) {
				try {
					long g = Long.valueOf((String)info.getPost().get("guild"));
					long ch = Long.valueOf((String)info.getPost().get("channel"));
					IVoiceChannel vc = Main.client.getGuildByID(g).getVoiceChannelByID(ch);
					vc.join();
				}catch (Exception e) {e.printStackTrace();}
			}
		}
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
			g=Main.client.getGuildByID(Long.valueOf((String)info.getGet().getOrDefault("guild", "")));
		} catch (Exception e) {}
		if(g!=null) {
			s+="\n<hr />\n"+UserModHandler.serverElement(g, info.getAccount())+"\n<h3 id='role_edit'>"+Main.lang.getLangage("tools.guildmod.role")+"</h3>\n<table class='table_list'>\n"
					+ "<tr class='table_list_top'>"
					+ "<td>"+Main.lang.getLangage("tools.guildmod.role.name")+"</td>"
					+ "<td class='usermod_account'>"+Main.lang.getLangage("tools.guildmod.role.accounts")+"</td>"
					+ "<td class='usermod_id'>"+Main.lang.getLangage("tools.guildmod.role.id")+"</td></tr>";
			List<IRole> rl = g.getRoles();
			rl.sort(new Comparator<IRole>() {
				public int compare(IRole o1, IRole o2) {
					return o2.getPosition()-o1.getPosition();
				}
			});
			for (IRole r: rl) {
				Color c = r.getColor();
				s+="<tr style='border:1px solid rgb("+c.getRed()+", "+c.getGreen()+", "+c.getBlue()+"); background-color:rgba("+c.getRed()+", "+c.getGreen()+", "+c.getBlue()+", 0.25)'><td>"+r.getName()+"</td><td><ul>";
				long l = r.getLongID();
				for (Account a: Main.accounts) {
					if(a.groupId.equals(String.valueOf(l))) {
						if(info.getAccount().hasPerm("users"))s+="<a href='users.ap?u="+a.name+"'>";
						s+="<li class='acc_tree_user'>"+a.name+"</li>";
						if(info.getAccount().hasPerm("users"))s+="</a>";
					}
				}
				s+="</ul></td><td>"+r.getLongID()+"</td></tr>";
			}
			s+="</table>\n<h3 id='text_edit'>"+Main.lang.getLangage("tools.guildmod.channel.text")+"</h3>\n<table class='table_list'>\n"
					+ "<tr class='table_list_top'>"
					+ "<td>"+Main.lang.getLangage("tools.guildmod.channel.name")+"</td>"
					+ "<td class='usermod_id'>"+Main.lang.getLangage("tools.guildmod.channel.id")+"</td></tr>";
			for (IChannel ch: g.getChannels()) {
				s+="<tr><td>"+ch.getName();
				if(ch.isNSFW()) s+="<div class='ch_nsfw'>"+Main.lang.getLangage("tools.guildmod.channel.nsfw")+"</div>";
				s+="</td><td>"+ch.getLongID()+"</td></tr>";
			}
			s+="</table><h3 id='voice_edit'>"+Main.lang.getLangage("tools.guildmod.channel.voice")+"</h3>\n<table class='table_list'>\n"
					+ "<tr class='table_list_top'>"
					+ "<td>"+Main.lang.getLangage("tools.guildmod.channel.name")+"</td>"
					+ "<td class='guildmod_ch_client'>"+Main.lang.getLangage("tools.guildmod.channel.users")+"</td>"
					+ "<td class='usermod_id'>"+Main.lang.getLangage("tools.guildmod.channel.id")+"</td></tr>";
			for (IVoiceChannel ch: g.getVoiceChannels()) {
				s+="<tr><td><form action='' method='POST' class='inline'>"
							+ "<input type='hidden' name='guild' value='"+g.getLongID()+"' />"
							+ "<input type='hidden' name='channel' value='"+ch.getLongID()+"' />"
							+ "<input type='hidden' name='action' value='join' />"
							+ "<input type='submit' value='"+Main.lang.getLangage("tools.guildmod.channel.join")+"' />"
							+ "</form>"+ch.getName()+"</td><td>";
				List<IUser> ul = ch.getConnectedUsers();
				ul.sort(new Comparator<IUser>() {
					public int compare(IUser o1, IUser o2) {
						return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
					}
				});
				for(IUser u: ul) {
					s+="\n<div>\n<form action='' method='POST' class='inline'>"
							+ "<input type='hidden' name='guild' value='"+g.getLongID()+"' />"
							+ "<input type='hidden' name='user' value='"+u.getLongID()+"' />"
							+ "<input type='hidden' name='action' value='kick' />"
							+ "<input type='submit' value='"+Main.lang.getLangage("tools.guildmod.channel.users.kick")+"' />"
							+ "</form>\n<img src='"+u.getAvatarURL()+"' style='width:16px;height:16px;' /> ";
					if(info.getAccount().hasPerm("manageclients"))s+="<a href='tools.ap?app=usermod&guild="+g.getLongID()+"#"+u.getLongID()+"'>";
					s+=UserModHandler.name(u, g, false);
					if(info.getAccount().hasPerm("manageclients"))s+="</a>";
					s+="</div>";
				}
				s+="\n</td><td>"+ch.getLongID()+"</td></tr>";
			}
		}
		return s;
	}
	public String neededPermission() {
		return "guildmod";
	}
	public boolean needConnection() {
		return true;
	}
	public String toolName() {
		return "guildmod";
	}

}
