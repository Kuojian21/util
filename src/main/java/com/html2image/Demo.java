package com.html2image;

import gui.ava.html.image.generator.HtmlImageGenerator;

public class Demo {

	public static void main(String[] args) {
		HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
        imageGenerator.loadUrl("https://www.lmlc.com"); 
        imageGenerator.saveAsImage("d:/hello-world.png");  
        imageGenerator.saveAsHtmlWithMap("hello-world.html", "hello-world.png");
	}
	
}
