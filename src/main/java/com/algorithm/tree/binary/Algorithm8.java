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
	
	
	/**
	 * 颠倒一个字符串。优化速度。优化空间。
	 * @param str
	 */
	public void reverse(char[] str,int s,int e) {
		while(s < e) {
			char c = str[s];
			str[s] = str[e];
			str[e] = c;
		}
	}
	
	/**
	 * 颠倒一个句子中的词的顺序，比如将“我叫克丽丝”转换为“克丽丝叫我”，
实现速度最快，移动最少。
	 * @param str
	 */
	public void reverseWord(char[] str) {
		reverse(str,0,str.length - 1);
		int s = 0;
		for(int i = 0; i < str.length;i++) {
			if(str[i] == ' ') {
				if(s < i - 1) {
					reverse(str,s,i-1);
				}
				s = i + 1;
			}
		}
	}
	

}
