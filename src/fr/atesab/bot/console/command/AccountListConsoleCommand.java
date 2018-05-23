package fr.atesab.bot.console.command;

import fr.atesab.bot.Account;
import fr.atesab.bot.BotServer;
import fr.atesab.bot.console.Console;
import fr.atesab.bot.console.ConsoleCommand;

public class AccountListConsoleCommand extends ConsoleCommand {

	@Override
	public boolean execute(Console console, String[] args) {
		String s = "Accounts:";
		for (Account acc: console.getServer().getAccounts())
			s+="\n- "+acc.name+" - "+(acc.isAdmin()?"Admin":(BotServer.listWriter(acc.perms, "%s ")));
		console.getOutputStream().println(s);
		return true;
	}

	@Override
	public String getDescription() {
		return "View account list";
	}

	@Override
	public String getName() {
		return "al";
	}
	@Override
	public String getUsage() {
		return getName();
	}

}
