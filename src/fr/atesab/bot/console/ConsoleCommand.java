package fr.atesab.bot.console;

public abstract class ConsoleCommand {
	public abstract boolean execute(Console console, String[] args);
	public abstract String getDescription();
	public abstract String getName();
	public abstract String getUsage();
}
