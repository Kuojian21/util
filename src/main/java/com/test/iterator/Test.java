package com.test.iterator;

import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Test {

	public static void main(String[] args) {
		Deque<Integer> queue = new ConcurrentLinkedDeque<Integer>();
		
		queue.add(1);
		queue.add(2);
		queue.add(3);
		Iterator<Integer> ite = queue.iterator();
		ite.next();
		ite.next();
		ite.next();
		ite.remove();
		ite.remove();
	}
	
}
