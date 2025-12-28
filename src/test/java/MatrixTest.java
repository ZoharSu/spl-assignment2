import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import memory.SharedMatrix;
import parser.ComputationNode;
import spl.lae.LinearAlgebraEngine;

import java.util.Arrays;
import java.util.List;

public class MatrixTest {
    
    @Test
    public void ctorTest() {
        assertThrows(IllegalArgumentException.class, () -> new SharedMatrix(null));
        assertDoesNotThrow(() -> new SharedMatrix());
        assertDoesNotThrow(() -> new SharedMatrix(new double[][]{}));
        assertDoesNotThrow(() -> new SharedMatrix(new double[][]{{}}));
        assertDoesNotThrow(() -> new SharedMatrix(new double[][]{{},{},{}}));
        assertDoesNotThrow(() -> new SharedMatrix(new double[][]{{1}}));
        assertDoesNotThrow(() -> new SharedMatrix(new double[][]{{1},{2},{3}}));
        assertDoesNotThrow(() -> new SharedMatrix(new double[][]{{1,2},{3,4},{5,6}, {7,8}, {9,10}}));
        assertThrows(IllegalArgumentException.class,
            () -> new SharedMatrix(new double[][]{{},{1}}));
        assertThrows(IllegalArgumentException.class,
            () -> new SharedMatrix(new double[][]{{1},{}}));
        assertThrows(IllegalArgumentException.class,
            () -> new SharedMatrix(new double[][]{{1,2,3},{4}}));
    }

    @Test
    public void loadRowMajorTest() {
        SharedMatrix m1 = new SharedMatrix();
        assertThrows(IllegalArgumentException.class, () -> m1.loadRowMajor(null));
        assertDoesNotThrow(() -> m1.loadRowMajor(new double[][]{}));
        assertEquals(0, m1.length());
        assertDoesNotThrow(() -> m1.loadRowMajor(new double[][]{{}}));
        assertEquals(1, m1.length());
        assertDoesNotThrow(() -> m1.loadRowMajor(new double[][]{{},{}}));
        assertEquals(2, m1.length());

        m1.loadRowMajor(new double[][]{{1}});
        assertEquals(1, m1.length());
        assertEquals(1, m1.get(0).get(0));

        double[][] m1arr = {
            {1,2},
            {3,4},
            {5,6},
        };

        m1.loadRowMajor(m1arr);
        assertEquals(3, m1.length());
        assertEquals(1, m1.get(0).get(0));
        assertEquals(2, m1.get(0).get(1));
        assertEquals(3, m1.get(1).get(0));
        assertEquals(4, m1.get(1).get(1));
        assertEquals(5, m1.get(2).get(0));
        assertEquals(6, m1.get(2).get(1));
    }

    @Test
    public void loadColumnMajorTest() {
        SharedMatrix m1 = new SharedMatrix();
        assertThrows(IllegalArgumentException.class, () -> m1.loadRowMajor(null));
        assertDoesNotThrow(() -> m1.loadColumnMajor(new double[][]{}));
        assertEquals(0, m1.length());
        assertDoesNotThrow(() -> m1.loadColumnMajor(new double[][]{{}}));
        assertEquals(0, m1.length());
        assertDoesNotThrow(() -> m1.loadColumnMajor(new double[][]{{},{}}));
        assertEquals(0, m1.length());

        m1.loadColumnMajor(new double[][]{{1}});
        assertEquals(1, m1.length());
        assertEquals(1, m1.get(0).get(0));

        double[][] m1arr = {
            {1,2},
            {3,4},
            {5,6},
        };

        m1.loadColumnMajor(m1arr);
        assertEquals(2, m1.length());
        assertEquals(1, m1.get(0).get(0));
        assertEquals(3, m1.get(0).get(1));
        assertEquals(5, m1.get(0).get(2));
        assertEquals(2, m1.get(1).get(0));
        assertEquals(4, m1.get(1).get(1));
        assertEquals(6, m1.get(1).get(2));
    }
}