package fr.atesab.bot.console.command;

import fr.atesab.bot.console.Console;
import fr.atesab.bot.console.ConsoleCommand;

public class SaveConfigConsoleCommand extends ConsoleCommand {

	@Override
	public boolean execute(Console console, String[] args) {
		console.getOutputStream().println("Saving configs ...");
		console.getServer().saveConfig();
		console.getOutputStream().println("Configs saved.");
		return true;
	}
	
	@Override
	public String getDescription() {
		return "Save config";
	}
	@Override
	public String getName() {
		return "save";
	}
	@Override
	public String getUsage() {
		return getName();
	}

}
