package fr.atesab.bot.console;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import fr.atesab.bot.BotServer;
import fr.atesab.bot.console.command.AccountListConsoleCommand;
import fr.atesab.bot.console.command.AddPermConsoleCommand;
import fr.atesab.bot.console.command.BotListConsoleCommand;
import fr.atesab.bot.console.command.ChangePasswordConsoleCommand;
import fr.atesab.bot.console.command.HelpConsoleCommand;
import fr.atesab.bot.console.command.RemovePermConsoleCommand;
import fr.atesab.bot.console.command.SaveConfigConsoleCommand;
import fr.atesab.bot.console.command.StopConsoleCommand;

public class Console extends Thread {
	private InputStream in;
	private PrintStream out;
	private BotServer server;
	private List<ConsoleCommand> commands = new ArrayList<>();

	public Console(BotServer server, String name) {
		this(server, name, System.in, System.out);
	}

	public Console(BotServer server, String name, InputStream in, PrintStream out) {
		super(name);
		this.server = server;
		this.in = in;
		this.out = out;
		commands.add(new HelpConsoleCommand());
		commands.add(new SaveConfigConsoleCommand());
		commands.add(new StopConsoleCommand());
		commands.add(new ChangePasswordConsoleCommand());
		commands.add(new AddPermConsoleCommand());
		commands.add(new RemovePermConsoleCommand());
		commands.add(new AccountListConsoleCommand());
		commands.add(new BotListConsoleCommand());
	}

	public ConsoleCommand getCommandByName(String name) {
		for (ConsoleCommand cmd : commands)
			if (cmd.getName().equalsIgnoreCase(name))
				return cmd;
		return null;
	}

	public List<ConsoleCommand> getCommands() {
		return commands;
	}

	public InputStream getInputStream() {
		return in;
	}

	public PrintStream getOutputStream() {
		return out;
	}

	public BotServer getServer() {
		return server;
	}

	@Override
	public void run() {
		Scanner scanner = new Scanner(in);
		while (!isInterrupted()) {
			String line = scanner.nextLine();
			String[] cmd = line.split(" ", 2);
			ConsoleCommand cc = getCommandByName(cmd[0]);
			if (cc != null) {
				if (!cc.execute(this, cmd.length == 2 ? cmd[1].split(" ") : new String[] {}))
					out.print(cc.getUsage());
			} else
				out.println("Unknow command");
		}
		scanner.close();
	}

	public void setCommands(List<ConsoleCommand> commands) {
		this.commands = commands;
	}
}
