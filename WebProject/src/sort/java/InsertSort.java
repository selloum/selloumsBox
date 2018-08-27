package sort.java;

import java.util.Arrays;

public class InsertSort {
	public static int[] insertSort(int[] array) {
		if(array==null||array.length<=0)
			return array;
		int temp;
		for(int i=0;i<array.length-1;i++) {
			int k=i;
			temp=array[k+1];
			while(k>=0&&array[k]>temp) {
				array[k+1]=array[k];
				k--;
			}
			array[k+1]=temp;
		}
		return array;
	}
	
	public static void main(String[] args) {
		int[] array= {5,3,7,5,6,9,1};
		int[] s=insertSort(array);
		System.out.println(Arrays.toString(s));
	}
}
