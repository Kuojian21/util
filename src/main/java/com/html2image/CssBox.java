package com.html2image;
 
import java.io.File;
import java.io.FileOutputStream;
 
import org.fit.cssbox.demo.ImageRenderer;
 
public class CssBox {
    public static void main(String[] args) throws Exception {
        ImageRenderer render = new ImageRenderer();
        System.out.println("kaishi");
        String url = "https://www.baidu.com";
        FileOutputStream out = new FileOutputStream(new File("D:"+File.separator+"html.png"));
        render.renderURL(url, out, ImageRenderer.TYPE_PNG);
        System.out.println("OK");
    }
}