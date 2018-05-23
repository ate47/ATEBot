package fr.atesab.bot.command;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import fr.atesab.bot.math.Evaluator;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class EvaluateCommand extends Command {
	private List<String> aliases;
	private Evaluator evaluator;
	public EvaluateCommand() {
		this.aliases = new ArrayList<String>();
		this.aliases.add("evaluate");
		this.evaluator = new Evaluator();
	}
	@Override
	public List<String> getAliases() {
		return aliases;
	}

	@Override
	public String getName() {
		return "eval";
	}
	@Override
	public String getUsage() {
		return getName()+" (-show)? <exp>";
	}
	@Override
	public String neededPermission() {
		return "maths";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		if(args.length==0) return false;
		boolean flag = args[0].equalsIgnoreCase("-show") && args.length!=1;
		event.getChannel().sendMessage((String.valueOf(evaluator.evaluate(buildString(args, flag?1:0)))+" ").replaceAll("[.]0 ", ""));
		if(flag) {
			String s = "Max Deep: "+evaluator.getDeep();
			List<String> datas = evaluator.getOperations();
			for (String data: datas)
				s+="\n"+data.replaceAll("([*_]){1}", "\\\\$1");
			event.getChannel().sendMessage(s);
		}
		return true;
	}

}
