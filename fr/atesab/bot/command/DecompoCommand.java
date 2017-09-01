package fr.atesab.bot.command;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;

import fr.atesab.bot.Main;
import fr.atesab.bot.MathHelp;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class DecompoCommand extends Command{
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message) {
		if(args.length==1){
			String s;
			try {
				BigInteger n = new BigInteger(args[0]);
				Map<BigInteger, BigInteger> map = MathHelp.decomposition(n);
				s = n + "=";
				if(map.isEmpty()) {
					s = Main.lang.getLangage("math.dc.pr");
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
				s = Main.lang.getLangage("math.dc.nan");
			}
			event.getChannel().sendMessage(s);
			return true;
		} else return false;
	}
	public String neededPermission() {
		return "maths";
	}
	public String getName() {
		return "decompo1er";
	}
	public String getUsage() {
		return getName()+" <n>";
	}
}
