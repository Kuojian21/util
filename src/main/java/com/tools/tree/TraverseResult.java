package com.tools.tree;

import java.util.concurrent.Future;

public class TraverseResult<T> {

	private T result;
	private Future<TraverseResult<T>>[] futures;
	private TraverseResult<T> next;

	public TraverseResult(T result) {
		super();
		this.result = result;
	}

	public TraverseResult(T result, Future<TraverseResult<T>>[] futures,
			TraverseResult<T> next) {
		super();
		this.result = result;
		this.futures = futures;
		this.next = next;
	}

	public T getResult() {
		return result;
	}

	public Future<TraverseResult<T>>[] getFutures() {
		return futures;
	}

	public TraverseResult<T> getNext() {
		return next;
	}

	private long total = 1;

	public void await(boolean clear) {
		if (this.futures != null && this.futures.length > 0) {
			for (int i = 0; i < this.futures.length; i++) {
				try {
					Future<TraverseResult<T>> future = this.futures[i];
					if (future != null) {
						future.get().await(clear);
						total = total + future.get().getTotal();
						if (clear) {
							this.futures[i] = null;
						}
					}
				} catch (Exception e) {

				}
			}
		}
		if (this.next != null) {
			try {
				this.next.await(clear);
				total = total + this.next.getTotal();
				if (clear) {
					this.next = null;
				}
			} catch (Exception e) {

			}
		}
	}

	public void await() {
		this.await(false);
	}

	public long getTotal() {
		this.await();
		return this.total;
	}

}
