package sort.java;

import java.util.Arrays;

public class HeapSort {

	public static int[] heapSort(int[] array) {
		if(array==null||array.length<=0)
			return array;
		
		for(int i=array.length/2-1;i>=0;i--) {
			adjustHeap(array,i,array.length);
		}
		
		for(int j=array.length-1;j>=0;j--) {
			swap(array,0,j);
			adjustHeap(array,0,j);
		}
		return array;
	}

	private static void swap(int[] array, int i, int j) {
		int temp=array[i];
		array[i]=array[j];
		array[j]=temp;
		
	}

	private static void adjustHeap(int[] array, int first, int last) {
int temp=array[first];
		
		for(int k=2*first+1;k<last;k=k*2+1) {
			if(k+1<last&&array[k+1]>array[k]) {
				k++;
			}
			if(array[k]>temp) {
				array[first]=array[k];
				first=k;
			}
			array[first]=temp;
			
		}
	}
	
	public static void main(String[] args) {
		int[] array={5,3,7,5,6,2,8,1,9};
		int[] s=heapSort(array);
		System.out.println(Arrays.toString(s));
	}
}
