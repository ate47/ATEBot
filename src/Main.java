import fr.atesab.bot.BotServer;

public class Main {

	public static void main(String[] args) {
		System.setProperty("file.encoding", "UTF-8");
		new BotServer(2201, 2202, 50, "atebot");
	}

}
