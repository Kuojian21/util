package com.tools.algorithm;

public class Sort {

	private boolean compare(int x, int y) {
		if (x > y) {
			return true;
		}
		return false;
	}

	private void swap(int[] a, int p, int q) {
		if (p != q) {
			int temp = a[p];
			a[p] = a[q];
			a[q] = temp;
		}
	}

	public void bubble(int[] a) {
		int length = a.length;
		for (int i = 1; i <= length - 1; i++) {
			boolean swap = false;
			for (int j = 0; j < length - i; j++) {
				if (this.compare(a[j], a[j + 1])) {
					this.swap(a, j, j + 1);
					swap = true;
				}
			}
			if (!swap) {
				break;
			}
		}
	}

	public void select(int[] a) {
		int length = a.length;
		for (int i = 0; i < length; i++) {
			int tIndex = i;
			int tValue = a[i];
			for (int j = i + 1; j < length; j++) {
				if (this.compare(a[j], tValue)) {
					tValue = a[j];
					tIndex = j;
				}
			}
			this.swap(a, i, tIndex);
		}
	}

	public void insert(int[] a) {
		int length = a.length;
		for (int i = 1; i < length; i++) {
			for (int j = i - 1; j >= 0; j--) {
				if (this.compare(a[j], a[j + 1])) {
					swap(a, j, j + 1);
				} else {
					break;
				}
			}
		}
	}

	private int maxGap(int length) {
		int gap = 1;
		while (length / (3 * gap + 1) > 2) {
			gap = 3 * gap + 1;
		}
		return gap;
	}

	private int nextGap(int gap) {
		if (gap == 1) {
			return 0;
		} else {
			return (gap - 1) / 3;
		}
	}

	public void shell(int[] a) {
		int length = a.length;
		int gap = maxGap(length);
		while (gap > 0) {
			for (int i = gap; i < length; i++) {
				for (int j = i - gap; j >= 0; j = j - gap) {
					if (this.compare(a[j], a[j + gap])) {
						this.swap(a, j, j + gap);
					} else {
						break;
					}
				}
			}
			gap = nextGap(gap);
		}
	}

	private int max(int[] a) {
		int length = a.length;
		int max = a[0];
		for (int i = 1; i < length; i++) {
			if (max < a[i]) {
				max = a[i];
			}
		}
		return max;
	}

	public void count(int[] a) {
		int length = a.length;
		int max = this.max(a);
		int[] count = new int[max];
		for (int i = 0; i < length; i++) {
			count[a[i] - 1]++;
		}
		for (int i = 1; i < max; i++) {
			count[i] = count[i] + count[i - 1];
		}
		for (int i = length - 1; i >= 0; i--) {
			count[a[i]]--;
			this.swap(a, i, count[a[i]]);
		}

	}

	private int digital(int x, int k) {
		for (int i = 2; i <= k; i++) {
			x = x / 10;
		}
		return x % 10;
	}

	private int length(int x) {
		int length = 1;
		while (x / 10 > 0) {
			x = x / 10;
			length++;
		}
		return length;
	}

	public void radix(int[] a) {
		int length = a.length;
		int max = this.max(a);
		for (int i = this.length(max); i > 0; i--) {
			int[] count = new int[10];
			for (int j = 0; j < length; j++) {
				count[this.digital(a[j], i)]++;
			}
			for (int j = 1; j < count.length; j++) {
				count[j] = count[j] + count[j - 1];
			}
			for (int j = length - 1; j >= 0; j--) {
				count[this.digital(a[j], i)]--;
				this.swap(a, j, count[this.digital(a[j], i)]);
			}
		}
	}

	private void quick(int[] a, int low, int high) {
		if (low >= high) {
			return;
		}
		int tLow = low + 1;
		int tHigh = high;

		boolean toBack = true;
		while (tLow <= tHigh) {
			if (toBack) {
				if (this.compare(a[low], a[tLow])) {
					tLow++;
				} else {
					toBack = false;
				}
			} else {
				if (!this.compare(a[low], a[tHigh])) {
					tHigh--;
				} else {
					toBack = true;
					this.swap(a, tLow, tHigh);
					tLow++;
				}
			}
		}
		tLow--;
		this.swap(a, low, tLow);
		this.quick(a, low, tLow - 1);
		this.quick(a, tLow + 1, high);
	}

	public void quick(int[] a) {
		int length = a.length;
		this.quick(a, 0, length - 1);
	}

	private void quick2(int[] a, int low, int high) {
		if (low >= high) {
			return;
		}
		int tIndex = low;

		for (int i = low + 1; i <= high; i++) {
			if(this.compare(a[low], a[i])){
				tIndex++;
				this.swap(a, tIndex, i);
			}
		}

		this.swap(a, low, tIndex);
		this.quick2(a, low, tIndex - 1);
		this.quick2(a, tIndex + 1, high);
	}
	
	public void quick2(int[] a) {
		int length = a.length;
		this.quick2(a, 0, length - 1);
	}
	
	private void merge(int[] a,int low,int high){
		if (low >= high) {
			return;
		}
		int mid = (high + low)/2;
		merge(a,low,mid);
		merge(a,mid + 1,high);
		
	}
	
	public void merge(int[] a) {
		int length = a.length;
	}

	public void heap(int[] a) {

	}

	public static void main(String[] args) {
		Sort sort = new Sort();
		int[] a = new int[] { 9, 8, 7, 4, 5, 6, 7, 7, 7, 5, 55, 2, 1, 0 };
		sort.quick2(a);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}

}
