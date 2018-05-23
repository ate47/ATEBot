package fr.atesab.bot.console.command;

import fr.atesab.bot.Account;
import fr.atesab.bot.BotServer;
import fr.atesab.bot.console.Console;
import fr.atesab.bot.console.ConsoleCommand;

public class RemovePermConsoleCommand extends ConsoleCommand {

	@Override
	public boolean execute(Console console, String[] args) {
		if(args.length>1) {
			Account acc = console.getServer().getAccountByName(args[0]);
			if(acc!=null) {
				boolean a = false;
				for (int i = 2; i < args.length; i++) {
					String p = args[i];
					if(BotServer.permExist(p)) {
						if(acc.perms.contains(p)) {
							acc.perms.remove(p);
							a = true;
						}
						console.getOutputStream().println("Perm \""+p+"\" removed.");
					} else console.getOutputStream().println("Unknow permission \""+p+"\"");
				}
				if(a) console.getServer().saveConfig();
			} else console.getOutputStream().println("Unknow account");
		} else return false;
		return true;
	}

	@Override
	public String getDescription() {
		return "Remove perm of an account";
	}

	@Override
	public String getName() {
		return "rp";
	}

	@Override
	public String getUsage() {
		return getName() + " <acc> <perm+>";
	}

}
