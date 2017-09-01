package fr.atesab.bot.handler.tools;

import java.io.File;
import java.io.IOException;

import fr.atesab.bot.Main;
import fr.atesab.bot.WebInformation;

public class MusicUploadHandler extends ToolHandler {
	public String handle(WebInformation info) throws IOException {
		File d = new File("musics");
		if(!(d.exists() && d.isDirectory()))d.mkdir();
		File[] files = d.listFiles();
		String s = "<table id='table_list'>\n<tr id='table_list_top'><td style='width:20px;'></td><td>"+Main.lang.getLangage("tools.audio.name")+"</td><td></td></tr>";
		for (File f:files) {
			if(!f.isDirectory())
				s+="\n<tr><td><input type='checkbox' name='"+f.getName()+"' /></td><td>"+f.getName()+"</td><td></td></tr>";
		}
		return s+"</table>\n"+Main.lang.getLangage("tools.audio.option")+"";
	}
	public String neededPermission() {
		return "audiogest";
	}
	public boolean needConnection() {
		return true;
	}
	public String toolName() {
		return "audio";
	}

}
