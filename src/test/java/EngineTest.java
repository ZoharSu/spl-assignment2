import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import parser.ComputationNode;
import spl.lae.LinearAlgebraEngine;

import java.util.Arrays;
import java.util.List;

@Disabled
public class EngineTest {
    @Test
    public void negateTest() {
        LinearAlgebraEngine lae = new LinearAlgebraEngine(2);
        double[][] m1arr = {
            {1, -2, 3}
        };

        ComputationNode[] children1 = {new ComputationNode(m1arr)};
        ComputationNode root = new ComputationNode("-", List.of(children1));

        lae.run(root);
        printMatrix(root.getMatrix());
    }

    private void printMatrix(double[][] m) {
        for (int i = 0; i < m.length; i++)
            System.out.println(Arrays.toString(m[i]));
    }
}
