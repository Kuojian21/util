package com.algorithm.sort;

/**
 * 选择排序
 * 
 * @author bjzhangkuojian
 *
 */
public class Select extends SortBase{

	@Override
	public void sort(int[] a) {
		for (int i = 0, length = a.length; i < length; i++) {
			int tIndex = i;
			int tValue = a[i];
			for (int j = i + 1; j < length; j++) {
				if (super.compare(a[j], tValue)) {
					tValue = a[j];
					tIndex = j;
				}
			}
			super.swap(a, i, tIndex);
		}
	}
}
