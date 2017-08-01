package com.tools.tree;

public class EmptyNode implements Node {

	private static final Node instance = new EmptyNode();
	private static final Node[] childs = new Node[0]; 
	
	private EmptyNode(){
		
	}
	
	public static Node getEmpty(){
		return instance;
	}
	
	public static Node[] getEmptyChilds(){
		return childs;
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public void compute() {

	}

	@Override
	public Node[] getChilds() {
		return childs;
	}

	@Override
	public NODE_TYPE type() {
		return NODE_TYPE.N;
	}

	@Override
	public void before() {
		
	}

	@Override
	public void after() {
		
	}

}
