package com.test.slide;

import java.util.concurrent.TimeUnit;

import com.java.kj.slide.KjSlide;

public class SlideTest {

	public static void main(String[] args) {
		
		KjSlide slide = new KjSlide(1,TimeUnit.MILLISECONDS,100);
		System.out.println(System.nanoTime());
		
		for(int i = 0;i < 10;i++) {
			int x = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					while(true) {
						if(slide.tryAcquire()) {
							System.out.println("线程" + x + "通过");
						}else {
							System.out.println("限流" + x);
						}
					}
				}
			})/*.start()*/;
		}
		
		while (true) {
			if (slide.tryAcquire()) {
				System.out.println("Hi,World!");
			}else {
				System.out.println("限流");
			}
		}
	}
	
}
