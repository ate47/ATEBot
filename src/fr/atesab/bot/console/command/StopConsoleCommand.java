package fr.atesab.bot.console.command;

import fr.atesab.bot.console.Console;
import fr.atesab.bot.console.ConsoleCommand;

public class StopConsoleCommand extends ConsoleCommand {

	@Override
	public boolean execute(Console console, String[] args) {
		console.getOutputStream().println("Stopping clients");
		console.getServer().stopClients();
		console.getOutputStream().println("Saving configs");
		console.getServer().saveConfig();
		console.getOutputStream().println("Stopping web server");
		console.getServer().getServlet().destroy();
		console.getOutputStream().println("Goodbye <3");
		System.exit(0);
		console.interrupt();
		return true;
	}

	@Override
	public String getDescription() {
		return "Stop the server";
	}

	@Override
	public String getName() {
		return "stop";
	}

	@Override
	public String getUsage() {
		return getName();
	}

}
