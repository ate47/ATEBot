package fr.atesab.bot.command;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import fr.atesab.bot.math.MathHelp;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class FactCommand extends ASyncCommand {
	@Override
	public List<String> getAliases() {
		return null;
	}
	public String getName() {
		return "fact";
	}
	public String getUsage() {
		return getName()+" <n>";
	}
	public String neededPermission() {
		return "advmaths";
	}
	public boolean runCommandASync(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig, BotInstance botInstance) {
		if(args.length==1){
			try {
				BigInteger n = new BigInteger(args[0]);
				BigInteger f = MathHelp.fact(n);
				File file = new File("result.txt");
				PrintWriter writer = new PrintWriter(file, "UTF-8");
				writer.println(f.toString());
				writer.close();
				event.getChannel().sendFile("!"+n.toString(), file);
				file.delete();
			} catch (Exception e) {
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("math.dc.napn"));
			}
			return true;
		} else return false;
	}
}
