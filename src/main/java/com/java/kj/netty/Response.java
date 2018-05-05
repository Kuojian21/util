package com.java.kj.netty;

public class Response<Q> {

	private long id;
	private Q data;

	public Response() {
		super();
	}

	public Response(long id, Q data) {
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

	public Q getData() {
		return data;
	}

	public void setData(Q data) {
		this.data = data;
	}

}
