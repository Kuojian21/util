package com.test.jdb;

/**
 * 
 * @author kuojian21
 *
 *  -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n
 *  -Xdebug -Xrunjdwp,transport=dt_socket,server=y,address=5432,suspend=n,onthrow=java.io.IOException,launch=/sbin/echo
 */
public class JdbTest {

	/**
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		while (true) {
			int i = 5;
			int j = 6;
			int sum = add(i, j);
			System.out.println(sum);
			sum = 0;
			for (i = 0; i < 100; i++)
				sum += i;
			System.out.println(sum);
			Thread.sleep(2000);
		}
	}
	public static int add(int augend, int addend) {
		int sum = augend + addend;
		return sum;
	}
	
}
