package com.java.kj.netty;

public class Request<R> {

	private long id;
	private R data;

	public Request() {
		
	}
	
	public Request(long id, R data) {
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

	public R getData() {
		return data;
	}

	public void setData(R data) {
		this.data = data;
	}

}
