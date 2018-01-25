package com.algorithm.sort;

public abstract class SortBase {

	public boolean compare(int x, int y) {
		if (x > y) {
			return true;
		}
		return false;
	}

	public void swap(int[] a, int p, int q) {
		if (p != q) {
			int temp = a[p];
			a[p] = a[q];
			a[q] = temp;
		}
	}
	
	public int max(int[] a) {
		int length = a.length;
		int max = a[0];
		for (int i = 1; i < length; i++) {
			if (max < a[i]) {
				max = a[i];
			}
		}
		return max;
	}

	public abstract void sort(int[] a);




	


	public void heap(int[] a) {

	}

	public static void main(String[] args) {
//		SortBase sort = new SortBase();
//		int[] a = new int[] { 9, 8, 7, 4, 5, 6, 7, 7, 7, 5, 55, 2, 1, 0 };
//		sort.quick2(a);
//		for (int i = 0; i < a.length; i++) {
//			System.out.println(a[i]);
//		}
	}

}
