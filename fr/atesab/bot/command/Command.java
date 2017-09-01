package fr.atesab.bot.command;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public abstract class Command {
	public abstract String getName();
	public abstract boolean runCommand(MessageReceivedEvent event, String[] args, String message);
	public abstract String neededPermission();
	public abstract String getUsage();
}
