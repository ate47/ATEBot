package fr.atesab.bot.command;

import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IMessage;

public class EditMessageCommand extends Command {

	@Override
	public List<String> getAliases() {
		return null;
	}
	@Override
	public String getName() {
		return "editmsg";
	}
	@Override
	public String getUsage() {
		return getName() + " (<id> <option>)|opt";
	}
	@Override
	public String neededPermission() {
		return "msgmod";
	}
	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		if(args.length==0)return false;
		else if(args.length > 1 && args[0].matches("[0-9]*")) {
			long l = 0;
			try {
				l = Long.valueOf(args[0]);
			} catch (Exception e) {}
			IMessage m = event.getClient().getMessageByID(l);
			if(m!=null) {
				try {
					String ccmd = serverConfig.commandPrefix+getName()+" "+args[0]+" ";
					if(args[1].equalsIgnoreCase("addreact")) {
						if(args.length==3) {
							args[2] = event.getMessage().getFormattedContent().split(" ")[3];
							if(EmojiManager.isEmoji(args[2]))
								m.addReaction(EmojiManager.getByUnicode(args[2]));
							else {
								IEmoji e = event.getGuild()!=null?event.getGuild().getEmojiByName(args[2].replaceAll(":", "")):null;
								if(e!=null)
									m.addReaction(e);
								else m.addReaction(EmojiManager.getForAlias(args[2].replaceAll(":", "")));
							}
						} else event.getChannel().sendMessage(ccmd+" addreact <id>");
					} else if(args[1].equalsIgnoreCase("edit")) {
						if(args.length>2) {
							m.edit(buildString(args, 2));
						} else event.getChannel().sendMessage(ccmd+" edit <text>");
					} else if(args[1].equalsIgnoreCase("delete")) {
						m.delete();
					} else event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.editmsg.uopt"));
				} catch (Exception e) {
					event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.editmsg.fail"));
				}
			} else event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.editmsg.umsg"));
		} else if(args[0].equalsIgnoreCase("opt")) {
			event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.editmsg.opt")+" : "
					+ "\n- addreact: "+botInstance.getServer().getLanguage("cmd.editmsg.opt.addreact")
					+ "\n- edit: "    +botInstance.getServer().getLanguage("cmd.editmsg.opt.edit")
					+ "\n- delete: "  +botInstance.getServer().getLanguage("cmd.editmsg.opt.delete"));
		} else return false;
		return true;
	}

}
