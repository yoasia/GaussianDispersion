/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author joanna
 */
public class QuickSortResult {
    private JSONArray array;
    private int length;
 
    public void sort(JSONArray inputArr) {
         
        if (inputArr == null || inputArr.size() == 0) {
            return;
        }
        this.array = inputArr;
        length = inputArr.size();
        quickSort(0, length - 1);
    }
 
    private void quickSort(int lowerIndex, int higherIndex) {
         
        int i = lowerIndex;
        int j = higherIndex;
        // calculate pivot number, I am taking pivot as middle index number
        JSONObject obj = (JSONObject) array.get(lowerIndex+(higherIndex-lowerIndex)/2);
        double pivot = (double) obj.get("value");
        // Divide into two arrays
        while (i <= j) {
            /**
             * In each iteration, we will identify a number from left side which 
             * is greater then the pivot value, and also we will identify a number 
             * from right side which is less then the pivot value. Once the search 
             * is done, then we exchange both numbers.
             */
            JSONObject iObj = (JSONObject) array.get(i);
            JSONObject jObj = (JSONObject) array.get(j);

            while ((double)iObj.get("value") < pivot) {
                i++;
            }
            while ((double)jObj.get("value") > pivot) {
                j--;
            }
            if (i <= j) {
                exchangeNumbers(i, j);
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        // call quickSort() method recursively
        if (lowerIndex < j)
            quickSort(lowerIndex, j);
        if (i < higherIndex)
            quickSort(i, higherIndex);
    }
 
    private void exchangeNumbers(int i, int j) {
        JSONObject temp = (JSONObject) array.get(i);
        JSONObject jObj = (JSONObject) array.get(j);
//        int temp = array[i];
//        array[i] = array[j];
//        array[j] = temp;
    }
    
}
