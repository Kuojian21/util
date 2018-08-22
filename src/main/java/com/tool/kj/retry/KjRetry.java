package com.tool.kj.retry;

import com.github.phantomthief.util.ThrowableConsumer;
import com.github.phantomthief.util.ThrowableFunction;

public class KjRetry {

	public static <I, O, X extends Throwable> O call(ThrowableFunction<I, O, X> function,
			ThrowableConsumer<Throwable, X> consumer, I input, int times) throws Throwable {
		Throwable x = null;
		for (int i = 0; i < times; i++) {
			try {
				return function.apply(input);
			} catch (Throwable ex) {
				x = ex;
				consumer.accept(x);
			}
		}
		throw x;
	}

}
