package fr.atesab.bot.command;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

public class InformationCommand extends Command {
	private List<String> aliases;

	public InformationCommand() {
		aliases = new ArrayList<String>();
		aliases.add("ci");
	}

	@Override
	public List<String> getAliases() {
		return aliases;
	}

	public String getName() {
		return "clientinformation";
	}

	public String getUsage() {
		return getName();
	}

	public String neededPermission() {
		return "info";
	}

	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		IUser user = args.length == 1 ? event.getClient().fetchUser(Long.parseLong(args[0])) : event.getAuthor();
		event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.clientinformation") + ": " + "\n"
				+ botInstance.getServer().getLanguage("cmd.bi.author") + ": " + user.getName() + "#"
				+ user.getDiscriminator()
				+ (event.getGuild() != null ? "\n" + botInstance.getServer().getLanguage("cmd.ci.roles") + ": {"
						+ user.getRolesForGuild(event.getGuild()).stream().filter(r -> !r.isEveryoneRole())
								.map(r -> botInstance.getServer().getLanguage("cmd.ci.id") + ":" + r.getStringID()
										+ ", " + botInstance.getServer().getLanguage("cmd.ci.name") + ":" + r.getName())
								.collect(Collectors.joining(", ", "[", "]"))
						+ "}" : "")
				+ "" + "\nId: " + user.getStringID());
		return true;
	}

}
