package com.java.kj.netty;

class Request {

	private long id;
	private int workId;
	private Object data;

	public Request() {
		
	}
	
	public Request(int workId,long id, Object data) {
		super();
		this.workId = workId;
		this.id = id;
		this.data = data;
	}

	public Request(long id, Object data) {
		super();
		this.id = id;
		this.data = data;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getWorkId() {
		return workId;
	}

	public void setWorkId(int workId) {
		this.workId = workId;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
