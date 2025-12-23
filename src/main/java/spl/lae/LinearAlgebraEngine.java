package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.ArrayList;
import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // TODO: create executor with given thread count
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        return null;
    }

    public void loadAndCompute(ComputationNode node) {
        assert node != null && node.getNodeType() != ComputationNodeType.MATRIX;
        ComputationNode leftNode = node.getChildren().get(0);
        assert leftNode.getNodeType() == ComputationNodeType.MATRIX;

        leftMatrix.loadRowMajor(leftNode.getMatrix());

        if (node.getNodeType() == ComputationNodeType.MULTIPLY ||
            node.getNodeType() == ComputationNodeType.ADD) {
            assert node.getChildren().size() == 2;
            ComputationNode rightNode = node.getChildren().get(1);
            assert rightNode.getNodeType() == ComputationNodeType.MATRIX;

            rightMatrix.loadRowMajor(rightNode.getMatrix());
        }

        executor.submitAll(switch (node.getNodeType()) {
            case ADD           -> createAddTasks();
            case MULTIPLY      -> createMultiplyTasks();
            case NEGATE        -> createNegateTasks();
            case TRANSPOSE     -> createTransposeTasks();
            case null, default -> throw new IllegalArgumentException();
        });

        node.resolve(leftMatrix.readRowMajor());
    }

    public List<Runnable> createAddTasks() {
        List<Runnable> tasks = new ArrayList<>(leftMatrix.length());

        for (int i = 0; i < leftMatrix.length(); i++) {
            SharedVector lhs = leftMatrix.get(i),
                         rhs = rightMatrix.get(i);

            tasks.add(() -> lhs.add(rhs));
        }
        return tasks;
    }

    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
        return null;
    }

    public List<Runnable> createNegateTasks() {
        // TODO: return tasks that negate rows
        return null;
    }

    public List<Runnable> createTransposeTasks() {
        // TODO: return tasks that transpose rows
        return null;
    }

    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return null;
    }

    private boolean computableNode(ComputationNode node) {
        if (node == null ||
            node.getNodeType() == ComputationNodeType.MATRIX ||
            node.getChildren() == null)
            return false;

        List<ComputationNode> children = node.getChildren();

        for (ComputationNode child : children)
            if (child.getNodeType() != ComputationNodeType.MATRIX)
                return false;

        switch (node.getNodeType()) {
            case MULTIPLY, ADD:
                if (children.size() != 2)
                    return false;
                break;

            case TRANSPOSE, NEGATE:
                if (children.size() != 1)
                    return false;
                break;

            case MATRIX: return false;
            case null:   return false;
        }
        return true;
    }
}
