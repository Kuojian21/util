package com.tools.tree;

public interface Node {
	
	public boolean isLeaf();
	
	public Node[] getChilds();

	public void before();
	
	public void compute();
	
	public void after();
	
	public NODE_TYPE type();
	
	public enum NODE_TYPE{
		A,S,C,N
	}
	
}
