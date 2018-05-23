package fr.atesab.bot.command;

import java.util.List;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

public class SondageCommand extends Command {
	public static class Score {
		public Emoji emoji;
		public int score;
		public String arg;
		public Score(Emoji emoji, int score, String arg) {
			this.emoji = emoji;
			this.score = score;
			this.arg = arg;
		}
	}
	private static String[] elem= {"zero","one","two","three","four","five","six","seven","eight","nine"};
	public static String getNumber(long number) {
		return getNumber(number, 0);
	}
	public static String getNumber(long number, int size) {
		String s = "";
		String n = String.valueOf(number);
		for (char i: n.toCharArray()) {
			s+=":"+elem[Integer.valueOf(String.valueOf(i))]+":";
		}
		if(s.length()<size)for (int i = s.length(); i < size; i++)s=":"+elem[0]+":"+s;
		return s;
	}
	@Override
	public List<String> getAliases() {
		return null;
	}
	public String getName() {
		return "sond";
	}
	public String getUsage() {
		return getName()+" <name> <arg[1-"+(elem.length-1)+"]>";
	}
	public String neededPermission() {
		return "sond";
	}
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig, BotInstance botInstance) {
		message = event.getMessage().getContent();
		List<String> elm = getAdvancedArgument(args);
		if(elm.size()>elem.length || elm.size()<2) return false;
		String b = "";
		for (int i = 1; i < elm.size(); i++) {
			b+="\n"+getNumber(i)+" \\: "+elm.get(i);
		}
		IMessage msg = event.getChannel().sendMessage("", botInstance.getServer().getEmbedObject(event.getAuthor(), elm.get(0), b));
		long l = System.currentTimeMillis();
		for (int i = 1; i < elm.size(); i++) {
			String e = elem[i];
			while (System.currentTimeMillis()<l+500);
			msg.addReaction(EmojiManager.getForAlias(e));
			l=System.currentTimeMillis();
		}
		return true;
	}
}
