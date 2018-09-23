package fr.atesab.bot.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Language {
	private String langName = "UNKNOW";
	private String fileDirectory;
	private ConcurrentHashMap<String, String> langmap;

	public Language(String fileDirectory) {
		this.fileDirectory = fileDirectory;
		try {
			this.loadLanguage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getFileDirectory() {
		return fileDirectory;
	}

	public Map<String, String> getLangmap() {
		return langmap;
	}

	public String getLangName() {
		return langName;
	}

	public String getLanguage(String lang, Object... params) {
		String s = this.langmap.getOrDefault(lang, lang).replace("::", "\n");
		for (int i = 0; i < params.length; i++)
			s = s.replaceFirst("%s", String.valueOf(params[i]));
		return s;
	}

	public void loadLanguage() throws IOException {
		File file = new File(this.fileDirectory + "/lang.al");
		InputStream i;
		if (!file.exists()) {
			i = Language.class.getResourceAsStream("/files/lang.al");
		} else {
			i = new FileInputStream(file);
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(i, "UTF-8"));
		this.langmap = new ConcurrentHashMap<String, String>();
		langName = "UNKNOW";
		String line;
		while ((line = br.readLine()) != null) {
			while (line.startsWith(" "))
				line = line.substring(1);
			if (line.startsWith("#"))
				continue;
			String[] l = line.split("=", 2);
			if (l.length == 2) {
				if (l[0].equals("name"))
					langName = l[1];
				else
					this.langmap.put(l[0], l[1]);
			}
		}
		br.close();
	}
}
