package com.java.kj.snowflake;

/**
 * @author bjzhangkuojian
 */
public class Snowflake {

	private static final int WORKER_BIT = 10;
	private static final int WORKER_MASK = -1 ^ (-1 << WORKER_BIT);
	private static final int SEQ_BIT = 12;
	private static final int SEQ_MASK = -1 ^ (-1 << SEQ_BIT);
	private static final int WORK_SEQ_BIT = WORKER_BIT + SEQ_BIT;
	private static final long IDEPOCH = 1344322705519L;
	
	private int workerId;
	private int sequence = 0;

	public Snowflake(int workerId) {
		this.workerId = workerId & WORKER_MASK;
	}
	
	
	
	public long nextId() {
		int seq = 0;
		synchronized (this) {
			seq = (this.sequence = ++this.sequence & SEQ_MASK);
		}
		long timestamp = System.currentTimeMillis() - IDEPOCH;
		long id = (timestamp << WORK_SEQ_BIT) | (this.workerId << SEQ_BIT) | seq;
		return id;
	}
}
