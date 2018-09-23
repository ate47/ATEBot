package fr.atesab.bot.console.command;

import fr.atesab.bot.Account;
import fr.atesab.bot.BotServer;
import fr.atesab.bot.console.Console;
import fr.atesab.bot.console.ConsoleCommand;

public class ChangePasswordConsoleCommand extends ConsoleCommand {

	@Override
	public boolean execute(Console console, String[] args) {
		if(args.length==2) {
			Account acc = console.getServer().getAccountByName(args[0]);
			if(acc!=null) {
				acc.hash = BotServer.md5(args[1]);
				console.getOutputStream().println("Password changed for "+acc.name);
			} else console.getOutputStream().println("Unknow account");
		} else return false;
		return true;
	}

	@Override
	public String getDescription() {
		return "Change account password";
	}

	@Override
	public String getName() {
		return "cap";
	}

	@Override
	public String getUsage() {
		return getName() + " <acc> <pw>";
	}

}
