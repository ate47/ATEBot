package fr.atesab.web;

public class BlogMessage {
	public String author;
	public String title;
	public String text;
	public long date;
	public BlogMessage(String author, String title, String text, long date) {
		this.author = author;
		this.title = title;
		this.text = text;
		this.date = date;
	}
	public int compareTo(BlogMessage msg2) {
		return (int) (this.date - msg2.date);
	}
}