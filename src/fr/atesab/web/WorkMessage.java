package fr.atesab.web;

public class WorkMessage {
	public String author;
	public String title;
	public String text;
	public String img;
	public String link;
	public WorkMessage(String author, String title, String text, String img, String link) {
		this.author = author;
		this.title = title;
		this.text = text;
		this.img = img;
		this.link = link;
	}

}
