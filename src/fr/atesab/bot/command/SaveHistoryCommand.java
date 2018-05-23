package fr.atesab.bot.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

public class SaveHistoryCommand extends Command {
	public static class MessageSave {
		public String message;
		public long date;
		public long author;
		public String id;
		public MessageSave(IMessage m) throws Exception{
			message = m.getFormattedContent();
			id = m.getStringID();
			author = m.getAuthor().getLongID();
			date = m.getCreationDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); //LocalDateTime
		}
		
	}
	@Override
	public List<String> getAliases() {
		return null;
	}
	public String getName() {
		return "scm";
	}
	public String getUsage() {
		return getName();
	}
	public String neededPermission() {
		return "savemessage";
	}
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig, BotInstance botInstance) {
		event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.scm.pre"));
		String n = "rawMessageHistory.json";
		try {
			File file = new File(n);
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			Gson gson = new GsonBuilder().create();
			for (IMessage m: event.getChannel().getFullMessageHistory().asArray()) {
				if(m!=null)
					try{writer.write("\n"+gson.toJson(new MessageSave(m)));} catch (Exception e) {}
			}
			event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.scm.post"));
			event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.scm.save"));
			writer.close();
			event.getChannel().sendFile(botInstance.getServer().getLanguage("cmd.scm.save2", n),file);
		} catch (Exception e) {
			event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.scm.save.error"));
			e.printStackTrace();
		}
		return true;
	}

}
