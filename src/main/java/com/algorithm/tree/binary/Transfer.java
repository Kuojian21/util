package com.algorithm.tree.binary;

/**
 * 把查找二叉树转化为双向链表，不能新建节点
 * @author ThinkPad
 *
 */
public class Transfer {


	public void transfer(AvlNode node) {
		if (node.left != null) {
			transfer(node.left);
			setLeft(node, node.left);
		}
		if (node.right != null) {
			transfer(node.right);
			setRight(node, node.right);
		}
	}

	public void setLeft(AvlNode node, AvlNode left) {
		if (left.right != null) {
			setLeft(node, left.right);
		} else {
			node.left = left;
			left.right = node;
		}
	}

	public void setRight(AvlNode node, AvlNode right) {
		if (right.left != null) {
			setRight(node, right.left);
		} else {
			node.right = right;
			right.left = node;
		}
	}

}
