package spl.lae;
import java.io.IOException;

import memory.SharedVector;
import memory.VectorOrientation;
import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
      // TODO: main
      testInit();
    }

  public static void testInit() {
    try {
    	SharedVector v1 = new SharedVector(
        	null, VectorOrientation.COLUMN_MAJOR);
    	SharedVector v2 = new SharedVector(
        	new double[]{}, VectorOrientation.ROW_MAJOR);
    	SharedVector v3 = new SharedVector(
    		new double[]{1}, VectorOrientation.COLUMN_MAJOR);
    	SharedVector v4 = new SharedVector(
			new double[]{1,2,3,4}, VectorOrientation.COLUMN_MAJOR);
		SharedVector[] arr = {v1,v2,v3,v4};
		for (SharedVector v : arr)
			printArr(v.vector);

    } catch (Exception e) {
    	e.printStackTrace();
    }
  }

  public static void p(Object o) {
    System.out.println(o);
  }

  public static <T> void printArr(T[] a) {
    for (T t : a) {
      p(t);
    }
  }

  public static <T> void printMatRow(T[][] a) {
    for (T[] tArr : a) {
      for (T t : tArr) {
        p(t);
      }
    }
  }

  // public static <T> void printMatCol(T[][] a) {
  //   if (a.length == 0) return;

  //   for (int j = 0; j < a[0].length; j++) {
  //     for (T t : tArr) {
  //       p(t);
  //     }
  //   }
  // }

}
