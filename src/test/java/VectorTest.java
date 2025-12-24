import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import memory.SharedVector;
import memory.VectorOrientation;

@Disabled
public class VectorTest {
    @Test
    public void addTest() {
        double[] arr =  {1, 2, 3};
        SharedVector v = new SharedVector(arr, VectorOrientation.ROW_MAJOR);

        double[] uarr = {1, 1, 1};
        SharedVector u = new SharedVector(uarr, VectorOrientation.ROW_MAJOR);
        v.add(u);

        assertEquals(v.get(1), 2);
    }
}
