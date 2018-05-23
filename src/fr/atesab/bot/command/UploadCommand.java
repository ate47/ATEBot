package fr.atesab.bot.command;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.BotServer;
import fr.atesab.bot.BotServlet;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage.Attachment;

public class UploadCommand extends Command {
	private BotServer server;
	public UploadCommand(BotServer server) {
		this.server = server;
	}
	@Override
	public List<String> getAliases() {
		return null;
	}
	public String getName() {
		return "upload";
	}
	public String getUsage() {
		return getName()+" ("+server.getLanguage("cmd.upload.usage")+")";
	}
	public String neededPermission() {
		return "files";
	}
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig, BotInstance botInstance) {
		boolean a = false;
		for (Attachment atch: event.getMessage().getAttachments()) {
			event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.upload.downloading"));
			try {
				String s = atch.getUrl();
				String[] as = s.split("/");
				String[] f = as[as.length-1].split("\\.");
				String filename = "atebot/upload/"+botInstance.getConfig().getName()+"/"+
				(args.length>0?(buildString(args, 0)+(f.length>1?"."+f[f.length-1]:"")):as[as.length-1]);
				URLConnection connection = new URL(s).openConnection();
				connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
				ReadableByteChannel channel = Channels.newChannel(connection.getInputStream());
				BotServer.createDir("atebot/upload/"+botInstance.getConfig().getName());
				FileOutputStream stream = new FileOutputStream(filename);
				stream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
				stream.close();
				channel.close();
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.upload.complete")+" : *"+filename+"*");
				BotServlet.log("UploadCommand", "New file \""+filename+"\"");
			} catch (IOException e) {
				e.printStackTrace();
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.upload.error"));
			}
			a = true;
		}
		return a;
	}

}
