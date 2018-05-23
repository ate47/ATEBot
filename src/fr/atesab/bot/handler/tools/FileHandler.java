package fr.atesab.bot.handler.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.atesab.bot.WebToolInformation;

public class FileHandler extends ToolHandler {
	public static List<File> getDirectoryFiles(File... files){
		List<File> a = new ArrayList<File>();
		List<File> pre = new ArrayList<File>();
		for (File f: files) {
			pre.add(f);
		}
		while (!pre.isEmpty()) {
			File f = pre.get(0);
			if(f!=null) {
				if(f.isDirectory() && f.listFiles()!=null) {
					for (File f2: f.listFiles()) {
						pre.add(f2);
					}
				} else {
					a.add(f);
				}
			}
			pre.remove(0);
			a.add(f);
		}
		return a;
	}
	public String handle(WebToolInformation info) throws IOException {
		String directory = "atebot/";
		File pred = new File(directory);
		if(info.getGet().containsKey("directory") && info.getGet().get("directory")!=null && !((String)info.getGet().get("directory")).isEmpty()) {
			directory+=((String)info.getGet().get("directory"))+"/";
		}
		File d = new File(directory);
		if(info.getRequest().getContentType()!=null && info.getRequest().getContentType().contains("multipart/form-data")) {
			// File f = Main.createDir("atebot/tmp");
			String[] ct = info.getRequest().getContentType().split("[;] ");
			String boundary = "";
			for (String c: ct) if(c.split("=",2)[0].equals("boundary"))boundary=c.split("=",2)[1];
			if(!boundary.isEmpty()) {
				// BufferedReader br = new BufferedReader(new InputStreamReader(info.getRequest().getInputStream(), "UTF-8"));
				
				try {
					FileOutputStream fos = new FileOutputStream(new File(d, "testfileupload.txt"));
					int i;
					while ((i=info.getRequest().getInputStream().read())!=-1) {
						fos.write(i);
					}
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		String s = "<P>>> <a href='?app="+this.toolName()+"'>atebot</a>";
		String link = "";
		for (String folder: directory.substring("atebot/".length()).replace('\\', '/').split("/")) {
			s+="/<a href='?app="+this.toolName()+"&directory="+(link+="/"+folder).substring(1)+"'>"+folder+"</a>";
		}
		s+="</P>";
		if(!(d.exists() && d.isDirectory()))return s+"<p>"+info.getBotServer().getLanguage("tools.file.notAFolder")+"</p>";
		s+="\n<table class='table_list'>\n<tr class='table_list_top'><td style='width:20px;'></td><td>"+info.getBotServer().getLanguage("tools.file.name")+"</td><td></td></tr>";
		
		for (File f: d.listFiles()) {
			if(f.isDirectory()) {
				s+="\n<tr><td><img src='"+info.getHost()+"/files/icon/folder.png' alt='"+info.getBotServer().getLanguage("tools.file.folder")+"' title='"+info.getBotServer().getLanguage("tools.file.folder")+"' /></td>"
							+ "<td><a href='?app="+this.toolName()+"&directory="+removeDirectory(f.getAbsolutePath(),pred)+"'>"+removeDirectory(f.getAbsolutePath(),d)+"</a></td><td></td></tr>";
			}
		}
		for (File f: d.listFiles()) {
			if(!f.isDirectory()) {
				s+="\n<tr><td>";
				String[][] fileType = new String[][]{
						{"image","png","jpg","jpeg","jpe","gif","bmp","dib","jfif","tif","tiff","ico"},
						{"hypertext","html","htm","xhtml"},
						{"stylesheet","css"},
						{"shell","sh","bat","cmd","nt","bsh"},
						{"script","java","js","lua","c","cpp","class","tex","r","py","pyw","php","rb","rbw","sql","vb","vbs","pl","pm","plx"},
						{"datastorage","db","json","config"}
					};
				boolean a = true;
				String icon = "file";
				for (String[] file: fileType) {
					if(a)
						for (int i = 0; a && i <  file.length; i++) {
							if(f.getName().matches(".*\\."+file[i])) {
								icon = file[0];
								a = false;
							}
						}
				}
				s+="<img src='"+info.getHost()+"/files/icon/"+icon+".png' alt='"+info.getBotServer().getLanguage("tools.file."+icon)+"' target='_blank' title='"+info.getBotServer().getLanguage("tools.file."+icon)+"' />";
				s+="</td>"
						+ "<td><a href='"+info.getHost()+"/files/"+removeDirectory(f.getAbsolutePath(),pred)+"'>"+removeDirectory(f.getAbsolutePath(),pred)+"</a></td><td></td></tr>";
			}
		}
		return s+"</table>\n";/*<form method='post' action='' enctype='multipart/form-data'>"
				+ "\n<input type='file' name='datafile' size='"+1024L*1024L*10L+"'>"
				+ "\n<input type='submit' value='"+Main.lang.getLanguage("tools.file.uploadimg")+"' />"
				+ "\n</form>";*/
	}
	public boolean needConnection() {
		return true;
	}
	public String neededPermission() {
		return "files";
	}
	private String removeDirectory(String s, File d) {
		String dp = d.getAbsolutePath();
		if(s.length()>dp.length())s=s.substring(1);
		return s.substring(dp.length()).replace('\\', '/');
	}
	public String toolName() {
		return "file";
	}
}
