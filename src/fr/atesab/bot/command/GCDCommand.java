package fr.atesab.bot.command;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import fr.atesab.bot.math.MathHelp;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class GCDCommand extends ASyncCommand {
	private List<String> aliases;
	public GCDCommand() {
		aliases = new ArrayList<String>();
		aliases.add("gcd");
	}
	@Override
	public List<String> getAliases() {
		return aliases;
	}
	public String getName() {
		return "pgcd";
	}
	public String getUsage() {
		return getName()+" <n> <p>";
	}
	public String neededPermission() {
		return "advmaths";
	}
	public boolean runCommandASync(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig, BotInstance botInstance) {
		if(args.length==2) {
			String s;
			try {
				BigInteger n = new BigInteger(args[0]);
				BigInteger p = new BigInteger(args[1]);
				BigInteger hcf = MathHelp.HCF(n,p);
				s = botInstance.getServer().getLanguage("maht.hcv")+"("+n+","+p+")="+hcf.toString();
			} catch (Exception e) {
				e.printStackTrace();
				s = botInstance.getServer().getLanguage("math.dc.nan");
			}
			event.getChannel().sendMessage(s);
			return true;
		} else return false;
	}

}
