package com.algorithm.sort;

/**
 * 插入排序
 * 
 * @author bjzhangkuojian
 *
 */
public class Insert extends SortBase {

	@Override
	public void sort(int[] a) {
		for (int i = 1, length = a.length; i < length; i++) {
			for (int j = i - 1; j >= 0; j--) {
				if (super.compare(a[j], a[j + 1])) {
					super.swap(a, j, j + 1);
				} else {
					break;
				}
			}
		}
	}
}
