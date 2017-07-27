package com.tools.tree;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class TraverseTree<T> {

	private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(3000);
	
	private final ExecutorService service = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MILLISECONDS,
			queue, new ThreadPoolExecutor.CallerRunsPolicy());

	public TraverseTree() {

	}

	@SuppressWarnings("unchecked")
	private TraverseResult<T> compute(TreeNode<T> node) throws Exception {

		T result = null;

		if (node.filter()) {
			result = node.compute();
		}
		if (!node.isLeaf()) {
			TreeNode<T>[] nodes = node.getChilds();
			int length = nodes.length;
			if (length > 0) {
				Future<TraverseResult<T>>[] futures = new Future[length];
				for (int i = 0; i < length - 1; i++) {
					futures[i] = submitTask(nodes[i]);
				}
				TraverseResult<T> next = this.compute(nodes[length - 1]);
				return new TraverseResult<T>(result, futures, next);
			}
		}
		return new TraverseResult<T>(result);
	}

	public Future<TraverseResult<T>> submitTask(final TreeNode<T> node) {
		return this.service.submit(new Callable<TraverseResult<T>>() {
			public TraverseResult<T> call() throws Exception {
				return TraverseTree.this.compute(node);
			}
		});
	}

	public TraverseResult<T> traverse(TreeNode<T> root, boolean clear) throws Exception {

		long start = System.nanoTime();

		Future<TraverseResult<T>> future = this.submitTask(root);

		future.get().await(clear);

		this.service.shutdown();

		System.out.println("耗时\t" + Math.round((System.nanoTime() - start) / Math.pow(10, 6)) / Math.pow(10, 3) + "秒");
		
		System.out.println("共处理文件" + future.get().getTotal() + "个文件");
		
		return future.get();

	}
}
