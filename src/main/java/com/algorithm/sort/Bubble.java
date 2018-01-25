package com.algorithm.sort;

/**
 * 冒泡排序
 * 
 * @author bjzhangkuojian
 *
 */
public class Bubble extends SortBase{

	@Override
	public void sort(int[] a) {
		for (int i = 1, length = a.length; i <= length - 1; i++) {
			boolean swap = false;
			for (int j = 0; j < length - i; j++) {
				if (super.compare(a[j], a[j + 1])) {
					super.swap(a, j, j + 1);
					swap = true;
				}
			}
			if (!swap) {
				break;
			}
		}
	}
}
