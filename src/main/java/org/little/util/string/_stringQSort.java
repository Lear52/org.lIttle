package org.little.util.string;

public class _stringQSort {

       private String buffer[];
       private int    length;


       public void sort(String array[]) {
           if (array == null || array.length == 0) {
               return;
           }
           this.buffer = array;
           this.length = array.length;
           quickSort(0, length - 1);
       }
      
       private void quickSort(int lowerIndex, int higherIndex) {
           int i = lowerIndex;
           int j = higherIndex;
           String pivot = this.buffer[lowerIndex + (higherIndex - lowerIndex) / 2];
      
           while (i <= j) {
               while (this.buffer[i].compareToIgnoreCase(pivot) < 0) {
                   i++;
               }
      
               while (this.buffer[j].compareToIgnoreCase(pivot) > 0) {
                   j--;
               }
      
               if (i <= j) {
                   exchangebuffer(i, j);
                   i++;
                   j--;
               }
           }
           //call quickSort recursively
           if (lowerIndex < j) {
               quickSort(lowerIndex, j);
           }
           if (i < higherIndex) {
               quickSort(i, higherIndex);
           }
       }
      
       private void exchangebuffer(int i, int j) {
           String temp = this.buffer[i];
           this.buffer[i] = this.buffer[j];
           this.buffer[j] = temp;
       }
      
       public static void main(String[] args) {
           String ls[] = {"zzw", "qqqaa", "wcc", "1hh", "1bb", "1ee", "2ll"}; 

           _stringQSort sorter = new _stringQSort();
           sorter.sort(ls);
      
           for (String i : ls) {
               System.out.print(i);
               System.out.print(" ");
           }
       }


}