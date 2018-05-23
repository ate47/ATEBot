package fr.atesab.bot.command;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import fr.atesab.bot.BotInstance;
import fr.atesab.bot.ServerConfig;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class PolynomialCommand extends Command {
	private float calc(float x, List<Float> arguments) {
		float r = 0;
		for (int i = 0; i < arguments.size(); i++) {
			r+= arguments.get(i) * power(x, i);
		}
		return r;
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public String getName() {
		return "polyn";
	}

	@Override
	public String getUsage() {
		return getName()+" (width=n)? (height=n)? (scale=d)? <a0> <a1> ... <an>\nf(X) = a0.X^0  + a1.X^1 + ... an.X^n";
	}
	@Override
	public String neededPermission() {
		return "maths";
	}
	private float power(float x, int p) {
		if(p==0) return 1;
		float w = x*power(x,p/2);
		if (p % 2 == 0) return w * w;
		else return w * w * x;
	}
	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] args, String message, ServerConfig serverConfig,
			BotInstance botInstance) {
		if(args.length==0) return false;
		int width = 600;
		int height = 350;
		float sf = width/10F;
		List<Float> arguments = new ArrayList<Float>();
		for (String a: args)
			try {
				if(a.toLowerCase().startsWith("w="))
					width = Integer.valueOf(a.substring(2));
				else if(a.toLowerCase().startsWith("width="))
					width = Integer.valueOf(a.substring(6));
				else if(a.toLowerCase().startsWith("h="))
					height = Integer.valueOf(a.substring(2));
				else if(a.toLowerCase().startsWith("height="))
					height = Integer.valueOf(a.substring(7));
				else if(a.toLowerCase().startsWith("s="))
					sf = Float.valueOf(a.substring(2));
				else if(a.toLowerCase().startsWith("scale="))
					sf = Float.valueOf(a.substring(6));
				else arguments.add(Float.valueOf(a));
			} catch (Exception e) {
				throw new IllegalArgumentException(botInstance.getServer().getLanguage("cmd.polyn.nan")+": "+a);
			}
		BufferedImage buffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				buffImage.setRGB(x, height-(1+y), Color.WHITE.getRGB());
			}
			int y = (int) (calc((x-width/2)/((float) sf), arguments)*((float) sf)) + height/2;
			if(y<height && y >= 0)
				buffImage.setRGB(x, height-(1+y), Color.BLACK.getRGB());
			buffImage.setRGB(x, height/2, Color.RED.getRGB());
			int s = x-width/2;
			if(s%sf==0 && s!=0)
				for(int y_ = height/2-4; y_ < height/2+5; y_++)
					buffImage.setRGB(x, y_, Color.RED.getRGB());
		}
		for (int y = 0; y < height; y++) {
			buffImage.setRGB(width/2, y, Color.BLUE.getRGB());
			int s = y-height/2;
			if(s%sf==0 && s!=0)
				for(int x = width/2-4; x < width/2+5; x++)
					buffImage.setRGB(x, y, Color.BLUE.getRGB());
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(buffImage, "png", outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String f = "";
		int start = arguments.size()-1;
		for (int i = start; i >=0 ; i--) {
			Float n = arguments.get(i);
			if(Math.abs(n) < 1E-12) continue;
			if(i!=start)f+=" + ";
			f+=n.toString();
			if(i!=0)f+=".X^"+i;
		}
		event.getChannel().sendFile("f(X)="+f, new ByteArrayInputStream(outputStream.toByteArray()), "poly.png");
		return true;
	}
}
