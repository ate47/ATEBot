package fr.atesab.bot;

public enum ContentType {
	art("image/x-jg"),
	bm("image/bmp"),
	bmp("image/bmp"),
	xbmp("image/x-windows-bmp"),
	dwg("image/vnddwg"),
	xdwg("image/x-dwg"),
	dxf("image/vnddwg"),
	xdxf("image/x-dwg"),
	fif("image/fif"),
	flo("image/florian"),
	fpx("image/vndfpx"),
	fpx1("image/vndnet-fpx"),
	g3("image/g3fax"),
	gif("image/gif"),
	ico("image/x-icon"),
	ief("image/ief"),
	iefs("image/ief"),
	jpeg("image/jpeg"),
	pjpeg("image/pjpeg"),
	jps("image/x-jps"),
	jut("image/jutvision"),
	mcf("image/vasa"),
	nap("image/naplps"),
	naplps("image/naplps"),
	nif("image/x-niff"),
	niff("image/x-niff"),
	pbm("image/x-portable-bitmap"),
	pct("image/x-pict"),
	pcx("image/x-pcx"),
	pgm("image/x-portable-graymap"),
	pic("image/pict"),
	pict("image/pict"),
	pm("image/x-xpixmap"),
	png("image/png"),
	pnm("image/x-portable-anymap"),
	ppm("image/x-portable-pixmap"),
	qif("image/x-quicktime"),
	qti("image/x-quicktime"),
	qtif("image/x-quicktime"),
	ras("image/cmu-raster"),
	xras("image/x-cmu-raster"),
	rast("image/cmu-raster"),
	rgb("image/x-rgb"),
	svf("image/vnddwg"),
	xsvf("image/x-dwg"),
	tif("image/tiff"),
	xtif("image/x-tiff"),
	tiff("image/tiff"),
	xtiff("image/x-tiff"),
	turbot("image/florian"),
	xxbm("image/x-xbm"),
	xxbm1("image/x-xbitmap"),
	xbm("image/xbm"),
	xif("image/vndxiff"),
	xpixmap("image/x-xpixmap"),
	xpm("image/xpm"),
	xpng("image/png"),
	xxwd("image/x-xwd"),
	xxwindowdump("image/x-xwindowdump"), 
	css("text/css",true),
	ass("text/css",true),
	js("application/javascript",true);
	private String type;
	private boolean text;
	ContentType(String type){
		this.type = type;
	}
	ContentType(String type, boolean text){
		this.type = type;
		this.text = text;
	}
	public String getType(){
		return type;
	}
	public boolean isText() {
		return text;
	}
}
