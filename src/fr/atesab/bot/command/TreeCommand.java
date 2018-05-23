package fr.atesab.bot.command;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.BotServer;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class TreeCommand extends ASyncCommand {
	private static final Color DISCORD_DARK_COLOR = new Color(54, 57, 62);

	class Node {
		IUser root;
		List<Node> subs;

		Node(IUser root) {
			this.root = root;
			this.subs = new ArrayList<>();
		}
		
		@Override
		public String toString() {
			return "Node [root=" + root + ", subs=" + subs + ", size()=" + size() + ", deep()=" + deep() + ", length()="
					+ length() + "]";
		}

		int size() {
			return 1 + subs.stream().mapToInt(Node::size).sum();
		}

		int deep() {
			OptionalInt i = subs.stream().mapToInt(Node::deep).max();
			return 1 + (i.isPresent() ? i.getAsInt() : 0);
		}

		int length() {
			return Math.max(1, subs.stream().mapToInt(Node::length).sum());
		}

		Node getNode(IUser root) {
			if (this.root != null && root != null && this.root.equals(root))
				return this;
			else {
				for (Node sub : subs) {
					Node subGet = sub.getNode(root);
					if (subGet != null)
						return subGet;
				}
				return null;
			}
		}

		BufferedImage getIconImage() throws MalformedURLException, IOException {
			if (root == null)
				return null;
			URL url = new URL(root.getAvatarURL().toLowerCase().endsWith("null.png")
					? "https://discordapp.com/assets/0e291f67c9274a1abdddeb3fd919cbaa.png"
					: root.getAvatarURL().replaceAll("[.]webp", ".png"));
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-Agent", BotServer.BOT_NAME + "/" + BotServer.BOT_VERSION);
			connection.connect();
			return ImageIO.read(connection.getInputStream());
		}

		void draw(Graphics2D graphics, int offsetX, int offsetY) {
			int length = length();
			draw(graphics, offsetX, offsetY, length, offsetX + ((length - 1) * 10 + 80 * length) / 2 - 40, offsetY);
		}

		protected void draw(Graphics2D graphics, int offsetX, int offsetY, int length, int rootX, int rootY) {
			BufferedImage rootImage = null;
			try {
				rootImage = getIconImage();
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (int i = 0, currentSize = 0; i < subs.size(); i++) {
				Node node = subs.get(i);
				int subLength = node.length();
				int subOffsetX = offsetX + currentSize;
				int subOffsetY = offsetY + 380;
				int subRootX = subOffsetX + ((subLength - 1) * 10 + 80 * subLength) / 2 - 40;
				int subRootY = subOffsetY;
				graphics.setColor(Color.WHITE);
				graphics.drawLine(rootX + 40, rootY + 80, subRootX + 40, subRootY);
				node.draw(graphics, subOffsetX, subOffsetY, subLength, subRootX, subRootY);
				currentSize += subLength * 90;
			}
			if (rootImage != null) {
				graphics.drawImage(rootImage, rootX, rootY, rootX + 80, rootY + 80, 0, 0, rootImage.getWidth(),
						rootImage.getHeight(), null);
				if (root != null) {
					graphics.setColor(Color.YELLOW);
					drawCenteredString(root.getName(), rootX + 40, rootY + 90, graphics);
				}
			}
		}

		void drawCenteredString(String str, int posX, int posY, Graphics2D graphics) {
			int len = graphics.getFontMetrics().stringWidth(str);
			graphics.drawString(str, posX - len / 2, posY);
		}
	}

	private static List<IRole> rolesFromId(IGuild guild, Enumeration<Long> roles) {
		List<IRole> list = new ArrayList<>();
		while (roles.hasMoreElements()) {
			try {
				list.add(guild.getRoleByID(Long.valueOf(roles.nextElement())));
			} catch (Exception e) {
			}
		}
		return list;
	}

	private static <T> boolean partition(Collection<T> list, Collection<T> list2) {
		for (T l : list)
			if (l != null && list2.contains(l))
				return false;
		return true;
	}

	private Node getNode(ServerConfig config, IUser root, List<IRole> roles, List<IUser> users, IGuild guild,
			IRole main) {

		Node node = new Node(root);
		Iterator<IUser> iterator = users.iterator();

		while (iterator.hasNext()) {
			IUser user = iterator.next();
			if ((main != null && user.hasRole(main)) || partition(user.getRolesForGuild(guild), roles)) {
				node.subs.add(new Node(user));
				iterator.remove();
			}
		}
		if (main != null)
			roles.remove(main);

		node.subs.forEach(subNode -> {
			IUser subRoot = subNode.root;
			if (subRoot == null)
				return;
			IRole originRole = guild.getRoleByID(config.originBind.getOrDefault(subRoot.getLongID(), -1L));
			if (originRole != null) {
				Node newNode = getNode(config, subRoot, roles, users, guild, originRole);
				subNode.subs = newNode.subs;
			}
		});
		return node;
	}

	public Node buildTree(ServerConfig config, IGuild guild) {
		return getNode(config, null, rolesFromId(guild, config.originBind.elements()),
				new ArrayList<>(guild.getUsers()), guild, null);
	}

	@Override
	public boolean runCommandASync(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		Node tree = buildTree(serverConfig, event.getGuild());
		int deep = tree.deep();
		int length = tree.length();
		BufferedImage buffImg = new BufferedImage(40 + (length - 1) * 10 + 80 * length,
				40 + 300 * (deep - 1) + deep * 80, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphic = buffImg.createGraphics();
		graphic.setBackground(DISCORD_DARK_COLOR);
		graphic.drawString(event.getGuild().getName(), 20, 20);
		tree.draw(graphic, 20, 20);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(buffImg, "png", outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		event.getChannel().sendFile(
				botInstance.getServer().getLanguage("cmd.tree"),
				new ByteArrayInputStream(outputStream.toByteArray()), "tree.png");
		return true;
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		if (args.length == 0 || event.getGuild() != null || event.getGuild().getTotalMemberCount() > 100)
			return false;
		args = unformatMention(args);
		switch (args[0].toLowerCase()) {
		case "bind":
			if (args.length != 3)
				return false;
			long r, u;
			try {
				r = Long.valueOf(args[1]);
				u = Long.valueOf(args[2]);
			} catch (Exception e) {
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("math.dc.nan"));
				return true;
			}
			IRole role = event.getGuild().getRoleByID(r);
			IUser user = event.getGuild().getUserByID(u);
			if (role == null)
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.tree.bind.NaR"));
			else if (user == null)
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.tree.bind.NaU"));
			else if (serverConfig.originBind.containsValue(r))
				event.getChannel().sendMessage(
						botInstance.getServer().getLanguage("cmd.tree.bind.alreadyBind", serverConfig.originBind
								.keySet(u).stream().map(l -> "<@&" + l + ">").collect(Collectors.joining(", "))));
			else {
				serverConfig.originBind.put(u, r);
				event.getChannel().sendMessage(
						botInstance.getServer().getLanguage("cmd.tree.bind", role.getName(), user.getName()));
			}
			botInstance.getServer().saveConfig();
			break;
		case "list":
			event.getChannel()
					.sendMessage(serverConfig.getOriginBind().isEmpty()
							? botInstance.getServer().getLanguage("cmd.tree.list.empty")
							: serverConfig
									.getOriginBind().entrySet().stream().map(e -> "<@!" + e.getKey() + "> ("
											+ e.getKey() + ") -> <@&" + e.getValue() + "> (" + e.getValue() + ")")
									.collect(Collectors.joining("\n-", "-", "")));
			break;
		case "unbind":
			List<String> name = new ArrayList<>();
			List<String> unknow = new ArrayList<>();
			arrayForEach(1, args, arg -> {
				long id;
				try {
					id = Long.valueOf(arg);
				} catch (Exception e) {
					unknow.add(arg);
					return;
				}
				IRole nr = event.getGuild().getRoleByID(id);
				IUser nu = event.getGuild().getUserByID(id);
				if (nr != null) {
					asArrayList(serverConfig.originBind.keys()).forEach(l -> {
						if (serverConfig.originBind.getOrDefault(l, -1L) == id) {
							serverConfig.originBind.remove(l);
							name.add(nr.getName());
						}
					});
				} else if (nu != null) {
					serverConfig.originBind.remove(id);
					name.add(nu.getNicknameForGuild(event.getGuild()));
				} else {
					unknow.add(arg);
				}
			});
			if (name.size() != 0)
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.tree.unbind",
						name.stream().collect(Collectors.joining(", "))));
			if (unknow.size() != 0)
				event.getChannel().sendMessage(botInstance.getServer().getLanguage("cmd.tree.unbind.unknow",
						unknow.stream().collect(Collectors.joining(", "))));
			botInstance.getServer().saveConfig();
			break;
		case "build":
			return super.runCommand(event, args, message, serverConfig, botInstance); // buildtree
		default:
			return false;
		}
		return true;
	}

	private static <A> void arrayForEach(int start, A[] array, Consumer<A> consumer) {
		for (int i = start; i < array.length; i++)
			consumer.accept(array[i]);
	}

	private static <A> ArrayList<A> asArrayList(Enumeration<A> enumeration) {
		ArrayList<A> arrayList = new ArrayList<>();
		while (enumeration.hasMoreElements())
			arrayList.add(enumeration.nextElement());
		return arrayList;
	}

	@Override
	public String getName() {
		return "tree";
	}

	@Override
	public String getUsage() {
		return super.getUsage() + " <build>|(<bind> <user> <role>)|(<unbind> <role>)|<list>";
	}

	@Override
	public String neededPermission() {
		return "tree";
	}

}
