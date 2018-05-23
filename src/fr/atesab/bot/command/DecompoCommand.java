package fr.atesab.bot.command;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import fr.atesab.bot.math.MathHelp;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class DecompoCommand extends ASyncCommand {
	@Override
	public List<String> getAliases() {
		return null;
	}
	public String getName() {
		return "decompo1er";
	}
	public String getUsage() {
		return getName()+" <n>";
	}
	public String neededPermission() {
		return "advmaths";
	}
	public boolean runCommandASync(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig, BotInstance botInstance) {
		if(args.length==1){
			String s;
			try {
				BigInteger n = new BigInteger(args[0]);
				Map<BigInteger, BigInteger> map = MathHelp.decomposition(n);
				s = n + "=";
				if(map.isEmpty()) {
					s = botInstance.getServer().getLanguage("math.dc.pr");
				}else {
					boolean a = false; 
					map = new TreeMap<BigInteger, BigInteger>(map);
					for(BigInteger i: map.keySet()){
						if(a) s+="x"; else a = true;
						s+=i.toString();
						BigInteger e = map.get(i);
						if(e.compareTo(BigInteger.ONE)!=0) s+="^"+e.toString();
					}
				}
			} catch (Exception e) {
				s = botInstance.getServer().getLanguage("math.dc.nan");
			}
			event.getChannel().sendMessage(s);
			return true;
		} else return false;
	}
}
