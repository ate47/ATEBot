package fr.atesab.bot.command;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.BotServer;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class HugCommand extends Command {
	private List<String> aliases;
	private BotServer server;
	public HugCommand(BotServer server) {
		this.server = server;
		aliases = new ArrayList<String>();
		aliases.add("calin");
	}
	@Override
	public List<String> getAliases() {
		return aliases;
	}

	@Override
	public String getName() {
		return "hug";
	}
	@Override
	public String getUsage() {
		return getName()+" (send)? (random)? ("+server.getLanguage("cmd.hug.usage")+")";
	}

	@Override
	public String neededPermission() {
		return "msg";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		if(args.length==0) return false;
		InputStream stream = HugCommand.class.getResourceAsStream("/files/hug.gif");
		long bid = botInstance.getClient().getOurUser().getLongID();
		LinkedList<IUser> users = new LinkedList<>();
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].replaceAll("<@[&]([0-9]+){1}>", "r:$1")
					.replaceAll("<@[!]?([0-9]+){1}>", "$1");
			try {
				if(args[i].startsWith("r:")) {
					IRole r = event.getClient().getRoleByID(Long.valueOf(args[i].substring(2)));
					users.addAll(r.getGuild().getUsersByRole(r));
				} else {
					users.add(event.getClient().getUserByID(Long.valueOf(args[i])));
				}
			} catch (Exception e) {}
		}
		String clientName = event.getGuild()!=null?(event.getAuthor().getDisplayName(event.getGuild())):event.getAuthor().getName();
		if(args[0].equalsIgnoreCase("send")) {
			if (botInstance.getServer().userHasPerm(event.getAuthor(), event.getGuild(), "manageclients", botInstance)) {
				if(args.length==2 && args[1].equalsIgnoreCase("random"))  {
					IUser u = (event.getGuild()!=null?RandomCommand.getRandomUser(event.getGuild()):RandomCommand.getRandomGlobalUser(botInstance.getClient()));
					u.getOrCreatePMChannel().sendFile(("* "+botInstance.getServer().getLanguage("cmd.hug.send", clientName)+" *"), 
							HugCommand.class.getResourceAsStream("/files/hug.gif"), "hug.gif");
					event.getChannel().sendMessage(("* "+botInstance.getServer().getLanguage("cmd.hug.sended")+" *"));
				} else {
					int a = 0;
					List<String> usernames = new ArrayList<String>();
					while (!users.isEmpty()) {
						IUser user = users.poll();
						if(!user.isBot()) {
							try {
								user.getOrCreatePMChannel().sendFile(("* "+botInstance.getServer().getLanguage("cmd.hug.send", clientName)+" *"), 
										HugCommand.class.getResourceAsStream("/files/hug.gif"), "hug.gif");
								usernames.add(user.getName());
								if(a<2) a = 2;
							} catch (Exception e) {}
						} else if(a==0 && user.getLongID() == bid) a = 1;
					}
					String s = "";
					for (int i = 0; i < usernames.size(); i++) {
						if(usernames.size()==i+1 && i!=0)s+=" "+botInstance.getServer().getLanguage("cmd.hug.and")+" ";
						else if(i>0)s+=", ";
						s+=usernames.get(i);
					}
					if(a==1) botInstance.sendMessage(event.getChannel(), ("* "+botInstance.getServer().getLanguage("cmd.hug.solo")+" *"), stream);
					else if(a==2) botInstance.sendMessage(event.getChannel(),(("* "+botInstance.getServer().getLanguage("cmd.hug.sended", s)+" *")));
					else event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.hug.unknow"));
				}
			} else
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("noperm"));
		} else if(args.length==1 && args[0].equalsIgnoreCase("random"))  {
			IUser u = (event.getGuild()!=null?RandomCommand.getRandomUser(event.getGuild()):RandomCommand.getRandomGlobalUser(botInstance.getClient()));
			if(u.getLongID()==botInstance.getClient().getOurUser().getLongID())
				event.getChannel().sendFile(("* "+botInstance.getServer().getLanguage("cmd.hug.solo")+" *"), stream, "hug.gif");
			else
				event.getChannel().sendFile(("* "+botInstance.getServer().getLanguage("cmd.hug.other", u.mention())+" *"), stream, "hug.gif");
		} else {
			List<String> mentions = new ArrayList<String>();
			boolean containHimSelf = false;
			while (!users.isEmpty()) {
				IUser user = users.poll();
				if(user.getLongID() == bid)
					containHimSelf = true;
				else if(!mentions.contains(user.mention()))
					mentions.add(user.mention());
			}
			if(mentions.size()>0) {
				if(containHimSelf)
					mentions.add(botInstance.getServer().getLanguage("cmd.hug.himself"));
				String s = "";
				for (int i = 0; i < mentions.size(); i++) {
					if(mentions.size()==i+1 && i!=0)s+=" "+botInstance.getServer().getLanguage("cmd.hug.and")+" ";
					else if(i>0)s+=", ";
					s+=mentions.get(i);
				}
				event.getChannel().sendFile(("* "+botInstance.getServer().getLanguage("cmd.hug.other", s)+" *"), stream, "hug.gif");
			} else if(containHimSelf) {
				event.getChannel().sendFile(("* "+botInstance.getServer().getLanguage("cmd.hug.solo")+" *"), stream, "hug.gif");
			} else {
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.hug.unknow"));
			}
		}
		return true;
	}

}
