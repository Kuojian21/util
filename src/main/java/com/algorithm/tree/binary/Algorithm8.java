package com.algorithm.tree.binary;

public class Algorithm8 {
	/**
	 * 3. ★用一种算法来颠倒一个链接表的顺序。现在在不用递归式的情况下做一遍。
	 * @param root
	 * @return
	 */
	public LinkNode reverse(LinkNode root) {
		if (root == null || root.next == null) {
			return root;
		}
		LinkNode head = reverse(root.next);
		root.next.next = root;
		root.next = null;
		return head;
	}
	
	public LinkNode reverse2(LinkNode root) {
		LinkNode head = null;
		LinkNode node = null;
		while(root != null) {
			node = root.next;
			root.next = head;
			head = root;
			root = node;
		}
		return head;
	}
}
