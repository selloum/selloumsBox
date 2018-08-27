package sort.java;

import java.util.Arrays;

public class SelectSort {

	public static int[] selectSort(int[] array) {
		if(array==null||array.length<=0)
			return array;
		for(int i=0;i<array.length;i++) {
			int min=i;
			for(int j=i+1;j<array.length;j++) {
				if(array[j]<array[min])
					min=j;
			}
			int temp=array[i];
			array[i]=array[min];
			array[min]=temp;
		}
		return array;
	}
	
	public static void main(String[] args) {
		int[] array={5,3,7,5,6,2,8,1,9};
		int[] s=selectSort(array);
		System.out.println(Arrays.toString(s));
	}
}
