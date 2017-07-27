package com.tools.tree;

public interface TreeNode<T> {
	
	public boolean isLeaf();
	
	public boolean filter();
	
	public T compute() throws Exception;
	
	public TreeNode<T>[] getChilds() throws Exception;
	
	public String getParentId();
	
	public String getId();
		
}
