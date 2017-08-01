package com.tools.tree;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Traverse {

	private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(3000);

	private final ExecutorService service = new ThreadPoolExecutor(20, 20, 0L,
			TimeUnit.MILLISECONDS, queue, new ThreadPoolExecutor.CallerRunsPolicy()) {
		@Override
		protected void beforeExecute(Thread t, Runnable r) {

		}

		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			if (super.getQueue().size() <= 0) {
				synchronized (this) {
					this.notify();
				}
			}
		}

		@Override
		protected void terminated() {

		}

		@Override
		public void shutdown() {
			while (super.getQueue().size() > 0) {
				try {
					synchronized (this) {
						this.wait(50000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			super.shutdown();
		}
	};

	public Traverse() {

	}

	private void compute(Node node) {
		switch (node.type()) {
		case N:
			break;
		case S:
			node.before();
			node.compute();
			node.after();
			break;
		case C:
			if (!node.isLeaf()) {
				Node[] nodes = node.getChilds();
				int length = nodes.length;
				if (length > 0) {
					for (int i = 0; i < length - 1; i++) {
						this.submitTask(nodes[i]);
					}
					this.compute(nodes[length - 1]);
				}
			}
			break;
		case A:
			node.before();
			node.compute();
			node.after();
			if (!node.isLeaf()) {
				Node[] nodes = node.getChilds();
				int length = nodes.length;
				if (length > 0) {
					for (int i = 0; i < length - 1; i++) {
						this.submitTask(nodes[i]);
					}
					this.compute(nodes[length - 1]);
				}
			}
		}
	}

	public void submitTask(final Node node) {
		this.service.submit(new Runnable() {
			@Override
			public void run() {
				Traverse.this.compute(node);
			}
		});
	}

	public void traverse(Node root) {
		try {
			long start = System.nanoTime();
			this.submitTask(root);
			this.service.shutdown();
			while (!this.service.awaitTermination(10, TimeUnit.SECONDS)) {

			}
			System.out.println("耗时\t" + Math.round((System.nanoTime() - start) / Math.pow(10, 6))
					/ Math.pow(10, 3) + "秒");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static Traverse newInstantce() {
		return new Traverse();
	}
}
