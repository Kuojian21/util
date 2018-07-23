package com.java.kj.executor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author kuojian21
 *
 */
public class KjExecutorService {

	/**
	 * add the shutdown-hook of the thread pool service.
	 */
	public KjExecutorService() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				KjExecutorService.this.service.shutdown();
			}
		});
	}

	/**
	 * tasks count.
	 */
	private final AtomicInteger tasks = new AtomicInteger(0);
	/**
	 * shutdown flag.
	 */
	private final AtomicBoolean shutdown = new AtomicBoolean(false);

	/**
	 * thread pool service.
	 */
	private final ExecutorService service = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
			Runtime.getRuntime().availableProcessors(), 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(1024), new ThreadPoolExecutor.CallerRunsPolicy()) {
		/**
		 * increment task count before task execute.
		 */
		@Override
		protected void beforeExecute(Thread t, Runnable r) {
			tasks.incrementAndGet();
		}

		/**
		 * decrement task count after task execute, and if the shutdown-flag is true,
		 * then shutdown the thread pool.
		 */
		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			if (tasks.decrementAndGet() == 0 && shutdown.get()) {
				super.shutdown();
			}
		}

		/**
		 * set the shutdown-flag of the thread pool, and if the tasks-count is 0, then
		 * shutdown the thread pool.
		 */
		@Override
		public void shutdown() {
			if (shutdown.compareAndSet(false, true) && tasks.get() == 0) {
				super.shutdown();
			}
		}
	};

	/**
	 * submit the task to the thread pool service.
	 * 
	 * @param tasks
	 *            the task array.
	 * @param drivers
	 *            the ship of the task，for example：if task 1 depend the task 2 and
	 *            3, then the drivers is {2=[1],3=[1]}.
	 * @param asSelf
	 *            need the current thread executor the task ?
	 */
	public void submit(Task<?>[] tasks, Map<Integer, Integer[]> drivers, boolean asSelf) {
		if (drivers == null) {
			drivers = Maps.newHashMap();
		}

		/**
		 * According to the ship of the drivers, infer the ship of the depends.
		 */
		Map<Integer, List<Integer>> depends = Maps.newHashMap();
		for (Map.Entry<Integer, Integer[]> entry : drivers.entrySet()) {
			Integer key = entry.getKey();
			Integer[] values = entry.getValue();
			for (Integer v : values) {
				List<Integer> l = depends.get(v);
				if (l == null) {
					l = Lists.newArrayList();
					depends.put(v, l);
				}
				l.add(key);
			}
		}

		Task<?> task = null;
		for (int i = 0, len = tasks.length; i < len; i++) {
			/**
			 * Get the ship of the depends and drivers.
			 */
			Integer[] dr = drivers.get(i);
			List<Integer> de = depends.get(i);
			tasks[i].id = i;
			tasks[i].service = this.service;
			/**
			 * If the driver-ship is not empty, then add next-tasks.
			 */
			if (dr != null) {
				for (Integer x : dr) {
					tasks[i].nexts.add(tasks[x]);
				}
			}

			/**
			 * If the depend-ship is not empty, then set the depend-count, else submit the
			 * task to thread pool service.
			 */
			if (de != null) {
				tasks[i].dependCount = de.size();
			} else {
				if (task != null) {
					this.service.submit(task);
				}
				task = tasks[i];
			}
		}
		if (task != null) {
			if (asSelf) {
				task.run();
			} else {
				this.service.submit(task);
			}
		}
	}

	public void shutdown() {
		this.service.shutdown();
	}
	
	/**
	 * 
	 * @author kuojian21
	 *
	 * @param <T>
	 */
	public static abstract class Task<T> implements Runnable {

		Integer id;
		List<Task<?>> nexts = Lists.newArrayList();
		ExecutorService service = null;
		Integer dependCount = 1;

		private AtomicInteger dependFinish = new AtomicInteger(0);
		private Map<Integer, Object> dependResult = Maps.newHashMap();

		@Override
		public final void run() {
			T t = this.runTask(dependResult.entrySet().stream().sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
					.map(e -> e.getValue()).collect(Collectors.toList()).toArray());
			if (!this.nexts.isEmpty()) {
				int len = this.nexts.size();
				for (int i = 0; i < len - 1; i++) {
					this.nexts.get(i).runTask(this.id, t, false);
				}
				this.nexts.get(len - 1).runTask(this.id, t, true);
			}
		}

		private void runTask(Integer index, Object obj, boolean asSelf) {
			dependResult.put(index, obj);
			if (this.dependFinish.incrementAndGet() == dependCount) {
				if (asSelf) {
					this.run();
				} else {
					this.service.submit(this);
				}
			}
		}

		public abstract T runTask(Object[] objs);

	}

}
