package fr.atesab.bot.utils;

public class Square {
	public final int x;
	public final int y;
	public final int x2;
	public final int y2;

	public Square(int x1, int y1, int x2, int y2) {
		if (x1 > x2) {
			x1 -= x2;
			x2 += x1;
			x1 = x2 - x1;
		}
		if (y1 > y2) {
			y1 -= y2;
			y2 += y1;
			y1 = y2 - y1;
		}
		this.x = x1;
		this.y = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

}
