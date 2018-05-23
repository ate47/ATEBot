package fr.atesab.bot;

import java.io.IOException;

public abstract class WebHandler {
	public abstract String handle(WebInformation info) throws IOException;
	public abstract boolean needConnection();
	public abstract String neededPermission();
}
