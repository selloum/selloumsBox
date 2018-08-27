package sort.java;

import java.util.Arrays;

public class MergeSort {
	
	public static void mergeSort(int[] array) {
		if(array==null||array.length<=0)
			return;
		int[] temp=new int[array.length];
		sort(array,0,array.length-1,temp);
	}

	private static void sort(int[] array, int i, int j, int[] temp) {
		if(i<j) {
			int mid=(i+j)/2;
			sort(array, i, mid, temp);
			sort(array, mid+1, j, temp);
			merge(array,i,mid,j,temp);
		}
	}

	private static void merge(int[] array, int start, int mid, int end, int[] temp) {
		int i=start;
		int j=mid+1;
		int t=0;
		
		while(i<=mid&&j<=end) {
			if(array[i]<=array[j]) {
				temp[t++]=array[i++];
			}else {
				temp[t++]=array[j++];
			}
		}
		
		while(i<=mid)
			temp[t++]=array[i++];
		while(j<=end)
			temp[t++]=array[j++];
		
		t=0;
		while(start<end)
			array[start++]=temp[t++];
	}
	
	public static void main(String[] args) {
		int[] array={5,3,7,5,6,2,8,1,9};
		mergeSort(array);
		System.out.println(Arrays.toString(array));
	}
	
}
