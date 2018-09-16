package fr.atesab.bot.console.command;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import fr.atesab.bot.console.Console;
import fr.atesab.bot.console.ConsoleCommand;
import sx.blah.discord.api.IDiscordClient;

public class BotListConsoleCommand extends ConsoleCommand {

	@Override
	public boolean execute(Console console, String[] args) {
		AtomicInteger i = new AtomicInteger(0);
		String s = console.getServer().getInstances().stream().map(bi -> {
			if (bi != null)
				if (bi.getClient() != null) {
					IDiscordClient c = bi.getClient();
					return c.getApplicationClientID() + " (" + c.getApplicationName() + ") - servers : "
							+ c.getGuilds().size();
				} else
					return bi.getConfig().getName();
			return "";
		}).collect(Collectors.joining("\n"));
		console.getOutputStream().println(i.get() + " server" + (i.get() > 1 ? "s" : "") + ":\n" + s);
		return true;
	}

	@Override
	public String getDescription() {
		return "Get running bot instance";
	}

	@Override
	public String getName() {
		return "bl";
	}

	@Override
	public String getUsage() {
		return getName();
	}

}
