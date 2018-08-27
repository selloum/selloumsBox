package sort.java;

import java.util.Arrays;

public class QuickSort {

	public static int[] quickSort(int[] array,int start,int end) {
		if(array==null&&array.length<=0) {
			return array;
		}
		int index=partition(array,start,end);
		if(index>start)
			quickSort(array, start, index-1);
		if(index<end)
			quickSort(array, index+1, end);
		return array;
	}

	private static int partition(int[] array, int start, int end) {
		int pivot=array[start];
		while(start<end) {
			while(array[end]>=pivot&&start<end) {
				end--;
				array[start]=array[end];
			}
			while(array[start]<=pivot&&start<end) {
				start++;
				array[end]=array[start];
			}
		}
		array[start]=pivot;
		return start;
		
	}
	
	public static void main(String[] args) {
		int[] array= {5,3,7,5,6,2,8,1,9};
		int[] s=quickSort(array, 0, array.length-1);
		System.out.println(Arrays.toString(s));
	}
}
