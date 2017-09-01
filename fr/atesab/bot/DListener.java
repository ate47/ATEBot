package fr.atesab.bot;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import fr.atesab.bot.command.Command;
import fr.atesab.bot.handler.tools.AutoMessageHandler.MessageElement;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.audio.AudioPlayer;

public class DListener {
	public static IVoiceChannel voicechannel;
	public static AudioPlayer audioPlayer;
	public static char heart = '\u2764';
	public static final String COMMAND_PREFIX = "!";
	public static String[] SebastienPatoche={
			"https://www.youtube.com/watch?v=9K-T6h84F7k", //La cartouche
			"https://www.youtube.com/watch?v=jw5d_W-JPP4", //Quand il péte
			"https://www.youtube.com/watch?v=lENw-MXwT6A", //Le panard
			"https://www.youtube.com/watch?v=g4wNSIcbxCM", //Beau black
			"https://www.youtube.com/watch?v=CDrgFOAECTc", //On va la foutre au fond
			"https://www.youtube.com/watch?v=UMQyRNafd_o", //Zizicoptère
			"https://www.youtube.com/watch?v=z19ZEblxPBA", //La goutte
			"https://www.youtube.com/watch?v=ih2wyp8GbVQ"  //La tournée du patron
	};

	public static String[] PatrickSebastien={
			"https://www.youtube.com/watch?v=azDpxzkdGgs", //On est des fous
			"https://www.youtube.com/watch?v=vUes9-tFWm4", //Sardine 1
			"https://www.youtube.com/watch?v=PA3P1-aSvKQ", //Sardine 2
			"https://www.youtube.com/watch?v=-5K-wmNKavA", //Il fait chaud
			"https://www.youtube.com/watch?v=HjXxvooa-0g", //Joyeux Anniversaire
			"https://www.youtube.com/watch?v=fAmvQ8C2SUo", //Le petit bonhomme en mousse
			"https://www.youtube.com/watch?v=dboTHl6ELnY", //Ta gueule
			"https://www.youtube.com/watch?v=9nOYviJqBAI", //tourner les serviettes
			"https://www.youtube.com/watch?v=cAoK7dRgulI", //chanteur 
			"https://www.youtube.com/watch?v=HLHn3mGyqjk", //Pourvu que ça dure
			"https://www.youtube.com/watch?v=nQAB3Zicm_M"  //La fiesta
	};
	public static String getReplaceText(String s){
		return s.replace("é", "e").replace("ê", "e").replace("è", "e")
				.replace("à", "a").replace("â", "a").replace("ë", "e");
	}
    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
		IMessage m = event.getMessage();
		String message = m.getFormattedContent();
		ArrayList<String> ans=new ArrayList<String>();
		for (MessageElement msg: Main.messages) {
			boolean a = false;
			switch (msg.type) {
			case equals:
				a = message.toLowerCase().equals(msg.question.toLowerCase());
				break;
			case contain:
				a = message.toLowerCase().contains(msg.question.toLowerCase());
				break;
			case startWith:
				a = message.toLowerCase().startsWith(msg.question.toLowerCase());
				break;
			}
			if(a){
				ans.add(msg.answer);
			}
		}
		if(message.startsWith(COMMAND_PREFIX)) {
        	message=message.substring(COMMAND_PREFIX.length());
	        String[] els = message.split(" ");
	        String[] args = new String[els.length-1];
	        Command cmd = Main.getCommandByName(els[0]);
	        if(cmd!=null) {
	        	System.arraycopy(els, 1, args, 0, args.length);
	        	if(Main.userHasPerm(event.getAuthor(), event.getGuild(), cmd.neededPermission())) {
	        		if(!cmd.runCommand(event, args, message))
	        			event.getChannel().sendMessage(COMMAND_PREFIX+cmd.getUsage());
	        	} else ans.add("Mdr t'as trop cru t'avais la perm toi.");
	        }
        } else if(message.toLowerCase().contains("nazi")){
        	InputStream i = Main.class.getResourceAsStream("/files/yolo/hitler.png");
        	if(i!=null) {
        		event.getClient().checkReady("send message");
        		event.getChannel().sendFile("Uiii \u534d", i, "Mon lapin.png");
        	}
        } else if(message.toLowerCase().contains("communisme") || message.toLowerCase().contains("communiste")){
        	InputStream i = Main.class.getResourceAsStream("/files/yolo/staline.png");
        	if(i!=null) {
        		event.getClient().checkReady("send message");
        		event.getChannel().sendFile("Uiii \u262d", i, "Mon bébé.png");
        	}
        } else if(message.toLowerCase().contains("pirate")){
        	ans.add("https://www.youtube.com/watch?v=pMhfbLRoGEw");
        } else if(getReplaceText(message.toLowerCase().replace(" ", "")).contains("patricksebastien")){
			ans.add("Owiii "+heart+"\n"+PatrickSebastien[new Random().nextInt(PatrickSebastien.length)]);
		} else if(getReplaceText(message.toLowerCase().replace(" ", "")).contains("sebastienpatoche")){
			ans.add("Hell yeah "+heart+"\n"+SebastienPatoche[new Random().nextInt(SebastienPatoche.length)]);
		}
        if(message.toLowerCase().contains("t'aime") && message.toLowerCase().contains("\u2764") ){
        	event.getMessage().delete();
        }
        if(ans.size()>0) {
	        event.getClient().checkReady("send message");
	        event.getChannel().sendMessage(Main.listWriter(ans, "\n%s"));
        }
    }
}
