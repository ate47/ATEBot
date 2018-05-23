package fr.atesab.bot.console.command;

import fr.atesab.bot.console.Console;
import fr.atesab.bot.console.ConsoleCommand;

public class HelpConsoleCommand extends ConsoleCommand {

	@Override
	public boolean execute(Console console, String[] args) {
		String s = "Commands:";
		for (ConsoleCommand cmd : console.getCommands()) {
			s += "\n" + cmd.getUsage() + ": " + cmd.getDescription();
		}
		console.getOutputStream().println(s);
		return true;
	}

	@Override
	public String getDescription() {
		return "Get server help";
	}

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getUsage() {
		return getName();
	}

}
