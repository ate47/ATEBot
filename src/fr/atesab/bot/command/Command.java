package fr.atesab.bot.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public abstract class Command {
	public static final Random random = new Random();

	public static String buildString(String[] array, int start) {
		String s = "";
		for (int i = start; i < array.length; i++) {
			if (i > start)
				s += " ";
			s += array[i];
		}
		return s;
	}

	public static String[] unformatMention(String[] args) {
		return unformatMention(args, false);
	}

	public static String[] unformatMention(String[] args, boolean clone) {
		if (clone)
			args = args.clone();
		for (int i = 0; i < args.length; i++)
			args[i] = args[i].replaceAll("<[@!#&]+[0-9]+>", "$2");
		return args;
	}

	public static List<String> getAdvancedArgument(String[] args) throws IllegalArgumentException {
		List<String> elm = new ArrayList<String>();
		String arg = null;
		String larg = null;
		for (String s : args) {
			if (larg != null) {
				if (s.endsWith(")")) {
					elm.add(larg + " " + s);
					larg = null;
				} else
					larg += " " + s;
			} else {
				if (arg != null) {
					if (s.endsWith("\"")) {
						elm.add(arg + " " + s.substring(0, s.length() - 1));
						arg = null;
					} else
						arg += " " + s;
				} else if (s.startsWith("\"")) {
					if (s.endsWith("\""))
						elm.add(s.substring(1, s.length() - 1));
					else
						arg = s.substring(1);
				} else if (s.startsWith("(")) {
					if (s.endsWith(")"))
						elm.add(s);
					else
						larg = s;
				} else
					elm.add(s);
			}
		}
		if (arg != null)
			throw new IllegalArgumentException("Syntax error: '\"' expected");
		else if (larg != null)
			throw new IllegalArgumentException("Syntax error: ')' expected");
		return elm;
	}

	public List<String> getAliases() {
		return null;
	}

	public abstract String getName();

	public String getUsage() {
		return getName();
	}

	public abstract String neededPermission();

	public abstract boolean runCommand(MessageReceivedEvent event, String[] args, String message,
			ServerConfig serverConfig, BotInstance botInstance);
}
