package com.algorithm.tree.binary;

public class Algorithm8 {
	/**
	 * 3. ★用一种算法来颠倒一个链接表的顺序。现在在不用递归式的情况下做一遍。
	 * 
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
		while (root != null) {
			node = root.next;
			root.next = head;
			head = root;
			root = node;
		}
		return head;
	}
	
	/**
	 * 用一种算法使通用字符串相匹配。
	 * @param str
	 * @param si
	 * @param com
	 * @param ci
	 * @return
	 */
	public boolean match(char[] str, int si, char[] com, int ci) {
		if (ci >= com.length && si >= str.length) {
			return true;
		}
		if (ci >= com.length) {
			return false;
		}
		if (com[ci] == '*') {
			do {
				if (match(str, si++, com, ci + 1)) {
					return true;
				}
			} while (si < str.length);
			return false;
		} else if (com[ci] == '?' || com[ci] == str[si]) {
			return match(str, si + 1, com, ci + 1);
		} else {
			return false;
		}
	}

}
