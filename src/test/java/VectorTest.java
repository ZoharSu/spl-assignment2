import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import memory.SharedMatrix;
import memory.SharedVector;
import memory.VectorOrientation;

@Disabled
public class VectorTest {
    @Test
    public void addTest() {

        double[] v1arr =  {1, 2, 3};
        SharedVector v1 = new SharedVector(v1arr, VectorOrientation.ROW_MAJOR);

        double[] v2arr = {1, 1, 1};
        SharedVector v2 = new SharedVector(v2arr, VectorOrientation.ROW_MAJOR);

        assertThrows(IllegalArgumentException.class, () -> v1.add(null));

        v1.add(v2);
        assertEquals(2, v1.get(0));
        assertEquals(3, v1.get(1));
        assertEquals(4, v1.get(2));

        v2.add(v2);
        assertEquals(2, v2.get(0));
        assertEquals(2, v2.get(1));
        assertEquals(2, v2.get(2));

        double[] v3arr = {};
        SharedVector v3 = new SharedVector(v3arr, VectorOrientation.ROW_MAJOR);

        double[] v4arr = {};
        SharedVector v4 = new SharedVector(v4arr, VectorOrientation.ROW_MAJOR);

        assertDoesNotThrow(() -> v3.add(v4));
        assertThrows(IllegalArgumentException.class, () -> v1.add(v3));

        double[] v5arr = {1};
        SharedVector v5 = new SharedVector(v5arr, VectorOrientation.ROW_MAJOR);

        double[] v6arr = {1};
        SharedVector v6 = new SharedVector(v6arr, VectorOrientation.COLUMN_MAJOR);

        assertThrows(IllegalArgumentException.class, () -> v5.add(v6));
    }

    @Test
    public void ctorTest() {
        assertThrows(IllegalArgumentException.class,
            () -> new SharedVector(null, null));
        assertThrows(IllegalArgumentException.class,
            () -> new SharedVector(new double[]{1}, null));
        assertThrows(IllegalArgumentException.class,
            () -> new SharedVector(null, VectorOrientation.COLUMN_MAJOR));
        assertDoesNotThrow(() -> new SharedVector(new double[0], VectorOrientation.COLUMN_MAJOR));
        assertDoesNotThrow(() -> new SharedVector(new double[]{1}, VectorOrientation.ROW_MAJOR));
    }

    @Test
    public void getTest() {
        double[] v1arr =  {1,2,3};
        SharedVector v1 = new SharedVector(v1arr, VectorOrientation.ROW_MAJOR);

        assertEquals(1, v1.get(0));
        assertEquals(2, v1.get(1));
        assertEquals(3, v1.get(2));
        assertThrows(IllegalArgumentException.class,
            () -> v1.get(3));
        assertThrows(IllegalArgumentException.class,
            () -> v1.get(-1));
    }

    @Test
    public void lengthTest() {
        double[] v1arr =  {};
        SharedVector v1 = new SharedVector(v1arr, VectorOrientation.ROW_MAJOR);
        assertEquals(0, v1.length());

        double[] v2arr =  {1};
        SharedVector v2 = new SharedVector(v2arr, VectorOrientation.ROW_MAJOR);
        assertEquals(1, v2.length());

        double[] v3arr =  {1,2};
        SharedVector v3 = new SharedVector(v3arr, VectorOrientation.ROW_MAJOR);
        assertEquals(2, v3.length());
    }

    @Test
    public void orientationTest() {
        double[] v1arr =  {};
        SharedVector v1 = new SharedVector(v1arr, VectorOrientation.ROW_MAJOR);
        assertEquals(VectorOrientation.ROW_MAJOR, v1.getOrientation());

        double[] v2arr =  {1};
        SharedVector v2 = new SharedVector(v2arr, VectorOrientation.COLUMN_MAJOR);
        assertEquals(VectorOrientation.COLUMN_MAJOR, v2.getOrientation());
    }

    @Test
    public void transposeTest() {
        double[] v1arr =  {};
        SharedVector v1 = new SharedVector(v1arr, VectorOrientation.ROW_MAJOR);

        v1.transpose();
        assertEquals(VectorOrientation.COLUMN_MAJOR, v1.getOrientation());
        v1.transpose();
        assertEquals(VectorOrientation.ROW_MAJOR, v1.getOrientation());
    }

    @Test
    public void negateTest() {
        double[] v1arr =  {};
        SharedVector v1 = new SharedVector(v1arr, VectorOrientation.ROW_MAJOR);

        assertDoesNotThrow(() -> v1.negate());

        double[] v2arr =  {1};
        SharedVector v2 = new SharedVector(v2arr, VectorOrientation.ROW_MAJOR);

        v2.negate();
        assertEquals(-1, v2.get(0));
        v2.negate();
        assertEquals(1, v2.get(0));
    }

    @Test
    public void dotTest() {
        double[] v1arr =  {};
        SharedVector v1 = new SharedVector(v1arr, VectorOrientation.ROW_MAJOR);

        assertThrows(IllegalArgumentException.class, () -> v1.dot(null));

        double[] v2arr =  {};
        SharedVector v2 = new SharedVector(v2arr, VectorOrientation.ROW_MAJOR);

        assertDoesNotThrow(() -> v1.dot(v2));
        assertEquals(0, v1.dot(v2));

        double[] v3arr =  {1,2,3};
        SharedVector v3 = new SharedVector(v3arr, VectorOrientation.ROW_MAJOR);

        assertThrows(IllegalArgumentException.class, () -> v3.dot(v1));
        assertDoesNotThrow(() -> v3.dot(v3));
        assertEquals(14, v3.dot(v3));

        double[] v4arr =  {5,5,5};
        SharedVector v4 = new SharedVector(v4arr, VectorOrientation.ROW_MAJOR);

        assertEquals(30, v3.dot(v4));
        assertEquals(30, v4.dot(v3));
    }

    @Test
    public void vecMatMulTest() {
        double[][] m1arr = {
            {1,2,3,4},
            {5,6,7,8},
            {9,10,11,12},
        };

        SharedMatrix m1 = new SharedMatrix(m1arr);

        // General exception checking
        double[] v1arr = {1,2,3};
        SharedVector v1 = new SharedVector(v1arr, VectorOrientation.COLUMN_MAJOR);

        assertThrows(IllegalArgumentException.class, () -> v1.vecMatMul(m1));

        double[] v2arr = {1};
        SharedVector v2 = new SharedVector(v2arr, VectorOrientation.ROW_MAJOR);

        assertThrows(IllegalArgumentException.class, () -> v2.vecMatMul(null));
        assertThrows(IllegalArgumentException.class, () -> v2.vecMatMul(m1));

        // Checking row * row major
        v1.transpose();
        assertDoesNotThrow(() -> v1.vecMatMul(m1));
        assertEquals(4, v1.length());
        assertEquals(38, v1.get(0));
        assertEquals(44, v1.get(1));
        assertEquals(50, v1.get(2));
        assertEquals(56, v1.get(3));

        // Checking row * column major
        for (int i = 0; i < m1.length(); i++)
            m1.get(i).transpose();

        double[] v3arr = {1,1,1,1};
        SharedVector v3 = new SharedVector(v3arr, VectorOrientation.ROW_MAJOR);

        assertDoesNotThrow(() -> v3.vecMatMul(m1));
        assertEquals(3, v3.length());
        assertEquals(10, v3.get(0));
        assertEquals(26, v3.get(1));
        assertEquals(42, v3.get(2));
    }

}
