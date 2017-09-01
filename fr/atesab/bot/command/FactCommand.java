package fr.atesab.bot.command;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigInteger;

import fr.atesab.bot.Main;
import fr.atesab.bot.MathHelp;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class FactCommand extends Command {
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message) {
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
				event.getChannel().sendMessage(Main.lang.getLangage("math.dc.napn"));
			}
			return true;
		} else return false;
	}
	public String neededPermission() {
		return "maths";
	}
	public String getName() {
		return "fact";
	}
	public String getUsage() {
		return getName()+" <n>";
	}
}
