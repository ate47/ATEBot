package fr.atesab.bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Langage {
	public String langName = "UNKNOW";
	public String fileDirectory;
	public Map<String, String> langmap = new HashMap<String, String>();
	public Langage(String fileDirectory){
		this.fileDirectory = fileDirectory;
		try {
			this.loadLanguage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String getLangage(String lang, String... params) {
		String s = this.langmap.getOrDefault(lang, lang);
		for (int i = 0; i < params.length; i++) {
			s=s.replaceAll("%s", params[i]);
		}
		return s;
	}
	public void loadLanguage() throws IOException{
		File file=new File(this.fileDirectory+"/lang.al");
		InputStream i;
    	if(!file.exists()){
    		i = Langage.class.getResourceAsStream("/files/lang.al");
    	}else{
    		i = new FileInputStream(file);
    	}
		BufferedReader br = new BufferedReader(new InputStreamReader(i, "UTF-8"));
		this.langmap = new HashMap<String,String>();
		langName = "UNKNOW";
		String line;
		while((line=br.readLine()) !=null){
			while(line.startsWith(" "))line=line.substring(1);
			if(line.startsWith("#"))continue;
			String[] l = line.split("=", 2);
			if(l.length==2) {
				if(l[0].equals("name"))langName=l[1]; else this.langmap.put(l[0], l[1]);
			}
		}
		br.close();
	}

}
