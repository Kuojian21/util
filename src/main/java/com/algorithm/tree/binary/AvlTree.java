package com.algorithm.tree.binary;

public class AvlTree<T> {

	public AvlNode<T> insert(AvlNode<T> node, T data) {
		if (node == null) {
			node = new AvlNode<T>(data);
		} else if (this.compareTo(node.data, data) < 0) {
			node.left = this.insert(node.left, data);
			if (Math.abs(node.left.height - node.right.height) >= 2) {
				if (node.left.height > node.right.height) {
					node = ll(node);
				} else {
					node = lr(node);
				}
			}
			node.height = Math.max(node.left.height, node.right.height) + 1;
		} else if (this.compareTo(node.data, data) > 0) {
			node.left = this.insert(node.right, data);
			if (Math.abs(node.left.height - node.right.height) >= 2) {
				if (node.left.height > node.right.height) {
					node = rl(node);
				} else {
					node = rr(node);
				}
			}
			node.height = Math.max(node.left.height, node.right.height) + 1;
		}
		return node;
	}

	public void delete(AvlNode<T> node, T data) {

	}

	protected int compareTo(T t1, T t2) {
		if (t1.getClass().isAssignableFrom(Integer.class)) {
			return (int) t1 - (int) t2;
		}
		return -1;
	}

	private AvlNode<T> ll(AvlNode<T> node) {
		return node;

	}

	private AvlNode<T> rr(AvlNode<T> node) {
		return node;

	}

	private AvlNode<T> lr(AvlNode<T> node) {
		return node;

	}

	private AvlNode<T> rl(AvlNode<T> node) {
		return node;

	}

}
