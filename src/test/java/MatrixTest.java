import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import memory.SharedMatrix;
import memory.VectorOrientation;
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

    @Test
    public void readRowMajorTest() {
        SharedMatrix m1 = new SharedMatrix(new double[][]{});

        double[][] arr = m1.readRowMajor();
        assertEquals(0, arr.length);

        // Testing row major matrix
        m1.loadRowMajor(new double[][]{{}});

        arr = m1.readRowMajor();
        assertEquals(1, arr.length);
        assertEquals(0, arr[0].length);

        m1.loadRowMajor(new double[][]{{1}});

        arr = m1.readRowMajor();
        assertEquals(1, arr.length);
        assertEquals(1, arr[0].length);
        assertEquals(1, arr[0][0]);

        m1.loadRowMajor(new double[][]{{1,2,3},{4,5,6}});

        arr = m1.readRowMajor();
        assertEquals(2, arr.length);
        assertEquals(3, arr[0].length);
        assertEquals(3, arr[1].length);
        assertEquals(1, arr[0][0]);
        assertEquals(2, arr[0][1]);
        assertEquals(3, arr[0][2]);
        assertEquals(4, arr[1][0]);
        assertEquals(5, arr[1][1]);
        assertEquals(6, arr[1][2]);


        // Testing column major matrix
        m1.loadColumnMajor(new double[][]{{}});

        arr = m1.readRowMajor();
        assertEquals(0, arr.length);

        m1.loadColumnMajor(new double[][]{{1}});

        arr = m1.readRowMajor();
        assertEquals(1, arr.length);
        assertEquals(1, arr[0].length);
        assertEquals(1, arr[0][0]);

        m1.loadColumnMajor(new double[][]{{1,2,3},{4,5,6}});

        arr = m1.readRowMajor();
        assertEquals(2, arr.length);
        assertEquals(3, arr[0].length);
        assertEquals(3, arr[1].length);
        assertEquals(1, arr[0][0]);
        assertEquals(2, arr[0][1]);
        assertEquals(3, arr[0][2]);
        assertEquals(4, arr[1][0]);
        assertEquals(5, arr[1][1]);
        assertEquals(6, arr[1][2]);
    }

    @Test
    public void getTest() {
        SharedMatrix m1 = new SharedMatrix(new double[][]{});
        assertThrows(IllegalArgumentException.class, () -> m1.get(0));

        // Testing row major
        m1.loadRowMajor(new double[][]{{}});
        assertDoesNotThrow(() -> m1.get(0));
        assertEquals(0, m1.get(0).length());
        assertThrows(IllegalArgumentException.class, () -> m1.get(-1));
        assertThrows(IllegalArgumentException.class, () -> m1.get(1));

        m1.loadRowMajor(new double[][]{{1}});
        assertDoesNotThrow(() -> m1.get(0));
        assertEquals(1, m1.get(0).length());
        assertEquals(1, m1.get(0).get(0));
        assertThrows(IllegalArgumentException.class, () -> m1.get(1));

        m1.loadRowMajor(new double[][]{{1,2,3},{4,5,6}});
        assertEquals(2, m1.length());
        assertEquals(3, m1.get(0).length());
        assertEquals(3, m1.get(1).length());
        assertEquals(1, m1.get(0).get(0));
        assertEquals(2, m1.get(0).get(1));
        assertEquals(3, m1.get(0).get(2));
        assertEquals(4, m1.get(1).get(0));
        assertEquals(5, m1.get(1).get(1));
        assertEquals(6, m1.get(1).get(2));

        // Testing column major
        m1.loadColumnMajor(new double[][]{{}});
        assertThrows(IllegalArgumentException.class, () -> m1.get(0));
        assertThrows(IllegalArgumentException.class, () -> m1.get(-1));
        assertThrows(IllegalArgumentException.class, () -> m1.get(1));

        m1.loadColumnMajor(new double[][]{{1}});
        assertDoesNotThrow(() -> m1.get(0));
        assertEquals(1, m1.get(0).length());
        assertEquals(1, m1.get(0).get(0));
        assertThrows(IllegalArgumentException.class, () -> m1.get(1));

        m1.loadColumnMajor(new double[][]{{1,2,3},{4,5,6}});
        assertEquals(3, m1.length());
        assertEquals(2, m1.get(0).length());
        assertEquals(2, m1.get(1).length());
        assertEquals(2, m1.get(2).length());
        assertEquals(1, m1.get(0).get(0));
        assertEquals(4, m1.get(0).get(1));
        assertEquals(2, m1.get(1).get(0));
        assertEquals(5, m1.get(1).get(1));
        assertEquals(3, m1.get(2).get(0));
        assertEquals(6, m1.get(2).get(1));
    }

    @Test
    public void lengthTest() {
        SharedMatrix m1 = new SharedMatrix(new double[][]{});
        assertEquals(0, m1.length());

        m1.loadRowMajor(new double[][]{{}});
        assertEquals(1, m1.length());

        m1.loadRowMajor(new double[][]{{1,2}});
        assertEquals(1, m1.length());

        m1.loadRowMajor(new double[][]{{1},{2},{3},{4}});
        assertEquals(4, m1.length());

        m1.loadColumnMajor(new double[][]{{}});
        assertEquals(0, m1.length());

        m1.loadColumnMajor(new double[][]{{1}});
        assertEquals(1, m1.length());

        m1.loadColumnMajor(new double[][]{{1},{2},{3},{4}});
        assertEquals(1, m1.length());

        m1.loadColumnMajor(new double[][]{{1,2,3,4,5},{1,2,3,4,5}});
        assertEquals(5, m1.length());
    }

    @Test
    public void getOrientationTest() {
        SharedMatrix m1 = new SharedMatrix();
        assertEquals(null, m1.getOrientation());

        m1.loadRowMajor(new double[][]{{}});
        assertEquals(VectorOrientation.ROW_MAJOR, m1.getOrientation());

        m1.loadRowMajor(new double[][]{{1}});
        assertEquals(VectorOrientation.ROW_MAJOR, m1.getOrientation());

        m1.loadRowMajor(new double[][]{{1},{2},{3}});
        assertEquals(VectorOrientation.ROW_MAJOR, m1.getOrientation());

        m1.loadColumnMajor(new double[][]{});
        assertEquals(null, m1.getOrientation());

        m1.loadColumnMajor(new double[][]{{}});
        assertEquals(null, m1.getOrientation());

        m1.loadColumnMajor(new double[][]{{1}});
        assertEquals(VectorOrientation.COLUMN_MAJOR, m1.getOrientation());

        m1.loadColumnMajor(new double[][]{{1},{2},{3}});
        assertEquals(VectorOrientation.COLUMN_MAJOR, m1.getOrientation());
    }
}