import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import parser.ComputationNode;
import spl.lae.LinearAlgebraEngine;

import java.util.List;

public class EngineTest {
    @Test
    public void ctorTest() {
        assertThrows(IllegalArgumentException.class, () -> new LinearAlgebraEngine(-1));
        assertThrows(IllegalArgumentException.class, () -> new LinearAlgebraEngine(0));
        assertDoesNotThrow(() -> new LinearAlgebraEngine(1));
        assertDoesNotThrow(() -> new LinearAlgebraEngine(100));
    }

    @Test
    public void negateTest() {
        LinearAlgebraEngine lae1 = new LinearAlgebraEngine(2);

        ComputationNode root1 = new ComputationNode("-", List.of());
        assertThrows(IllegalArgumentException.class, () -> lae1.run(root1));

        LinearAlgebraEngine lae2 = new LinearAlgebraEngine(2);
        ComputationNode child1 = new ComputationNode(new double[][]{{1,-2,3}});
        ComputationNode root2 = new ComputationNode("-", List.of(child1));
        ComputationNode res = lae2.run(root2);
        double[][] resArr = res.getMatrix();
        assertEquals(1, resArr.length);
        assertEquals(-1, resArr[0][0]);
        assertEquals(2, resArr[0][1]);
        assertEquals(-3, resArr[0][2]);

        LinearAlgebraEngine lae3 = new LinearAlgebraEngine(2);
        ComputationNode child2 = new ComputationNode(new double[][]{{1,-2,3}});
        ComputationNode child3 = new ComputationNode(new double[][]{{1,-2,3}});
        ComputationNode root3 = new ComputationNode("-", List.of(child2, child3));
        assertThrows(IllegalArgumentException.class, () -> lae3.run(root3));
    }

    @Test
    public void transposeTest() {
        LinearAlgebraEngine lae1 = new LinearAlgebraEngine(2);

        ComputationNode root1 = new ComputationNode("T", List.of());
        assertThrows(IllegalArgumentException.class, () -> lae1.run(root1));

        LinearAlgebraEngine lae2 = new LinearAlgebraEngine(2);
        ComputationNode child1 = new ComputationNode(new double[][]{{1,2,3}});
        ComputationNode root2 = new ComputationNode("T", List.of(child1));
        ComputationNode res = lae2.run(root2);
        double[][] resArr = res.getMatrix();
        assertEquals(3, resArr.length);
        assertEquals(1, resArr[0][0]);
        assertEquals(2, resArr[1][0]);
        assertEquals(3, resArr[2][0]);

        LinearAlgebraEngine lae3 = new LinearAlgebraEngine(2);
        ComputationNode child2 = new ComputationNode(new double[][]{{1,2,3}});
        ComputationNode child3 = new ComputationNode(new double[][]{{1,2,3}});
        ComputationNode root3 = new ComputationNode("T", List.of(child2, child3));
        assertThrows(IllegalArgumentException.class, () -> lae3.run(root3));
    }

    @Test
    public void multiplyTest() {
        LinearAlgebraEngine lae1 = new LinearAlgebraEngine(2);

        ComputationNode root1 = new ComputationNode("*", List.of());
        assertThrows(IllegalArgumentException.class, () -> lae1.run(root1));

        LinearAlgebraEngine lae2 = new LinearAlgebraEngine(2);
        ComputationNode child1 = new ComputationNode(new double[][]{{1,2,3}});
        ComputationNode root2 = new ComputationNode("*", List.of(child1));
        assertThrows(IllegalArgumentException.class, () -> lae2.run(root2));

        LinearAlgebraEngine lae3 = new LinearAlgebraEngine(2);
        ComputationNode child2 = new ComputationNode(new double[][]{{1,2,3}});
        ComputationNode child3 = new ComputationNode(new double[][]{{1,2,3}});
        ComputationNode root3 = new ComputationNode("*", List.of(child2, child3));
        assertThrows(IllegalArgumentException.class, () -> lae3.run(root3));

        LinearAlgebraEngine lae4 = new LinearAlgebraEngine(2);
        ComputationNode child4 = new ComputationNode(new double[][]{{1,2,3},{4,5,6}});
        ComputationNode child5 = new ComputationNode(new double[][]{{1,4},{2,5},{3,6}});
        ComputationNode root4 = new ComputationNode("*", List.of(child4, child5));
        ComputationNode res = lae4.run(root4);

        double[][] resArr = res.getMatrix();
        assertEquals(2, resArr.length);
        assertEquals(2, resArr[0].length);
        assertEquals(2, resArr[1].length);
        assertEquals(14, resArr[0][0]);
        assertEquals(32, resArr[0][1]);
        assertEquals(32, resArr[1][0]);
        assertEquals(77, resArr[1][1]);
    }

    @Test
    public void addTest() {
        LinearAlgebraEngine lae1 = new LinearAlgebraEngine(2);

        ComputationNode root1 = new ComputationNode("+", List.of());
        assertThrows(IllegalArgumentException.class, () -> lae1.run(root1));

        LinearAlgebraEngine lae2 = new LinearAlgebraEngine(2);
        ComputationNode child1 = new ComputationNode(new double[][]{{1,2,3}});
        ComputationNode root2 = new ComputationNode("+", List.of(child1));
        assertThrows(IllegalArgumentException.class, () -> lae2.run(root2));

        LinearAlgebraEngine lae3 = new LinearAlgebraEngine(2);
        ComputationNode child2 = new ComputationNode(new double[][]{{1,2,3}});
        ComputationNode child3 = new ComputationNode(new double[][]{{1},{2},{3}});
        ComputationNode root3 = new ComputationNode("+", List.of(child2, child3));
        assertThrows(IllegalArgumentException.class, () -> lae3.run(root3));

        LinearAlgebraEngine lae4 = new LinearAlgebraEngine(2);
        ComputationNode child4 = new ComputationNode(new double[][]{{1,2,3},{4,5,6}});
        ComputationNode child5 = new ComputationNode(new double[][]{{7,8,9}, {10,11,12}});
        ComputationNode root4 = new ComputationNode("+", List.of(child4, child5));
        ComputationNode res = lae4.run(root4);

        double[][] resArr = res.getMatrix();
        assertEquals(2, resArr.length);
        assertEquals(3, resArr[0].length);
        assertEquals(3, resArr[1].length);
        assertEquals(8, resArr[0][0]);
        assertEquals(10, resArr[0][1]);
        assertEquals(12, resArr[0][2]);
        assertEquals(14, resArr[1][0]);
        assertEquals(16, resArr[1][1]);
        assertEquals(18, resArr[1][2]);
    }
}
