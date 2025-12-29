package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        if (numThreads <= 0)
            throw new IllegalArgumentException("Number of threads must be positive");

        executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced

        // TODO: what do you think?
        try {
            while (computationRoot.getNodeType() != ComputationNodeType.MATRIX) {
                ComputationNode toResolve = computationRoot.findResolvable();
                toResolve.associativeNesting();
                toResolve = toResolve.findResolvable();
                loadAndCompute(toResolve);
            }
        } finally {
            try {
                executor.shutdown();
            } catch (InterruptedException e) {}
        }
        return computationRoot;
    }

    public void loadAndCompute(ComputationNode node) {
        if (!computableNode(node))
            throw new IllegalArgumentException("Uncomputable node");

        ComputationNode leftNode = node.getChildren().get(0);
        leftMatrix.loadRowMajor(leftNode.getMatrix());

        if (node.getNodeType() == ComputationNodeType.MULTIPLY ||
            node.getNodeType() == ComputationNodeType.ADD)
        {
            ComputationNode rightNode = node.getChildren().get(1);

            if (node.getNodeType() == ComputationNodeType.MULTIPLY)
                rightMatrix.loadColumnMajor(rightNode.getMatrix());
            else
                rightMatrix.loadRowMajor(rightNode.getMatrix());
        }


        switch (node.getNodeType()) {
            case ADD           : executor.submitAll(createAddTasks());       break;
            case MULTIPLY      : executor.submitAll(createMultiplyTasks());  break;
            case NEGATE        : executor.submitAll(createNegateTasks());    break;
            case TRANSPOSE     : executor.submitAll(createTransposeTasks()); break;
            case null, default : throw new IllegalArgumentException();
        }

        node.resolve(leftMatrix.readRowMajor());
    }

    public List<Runnable> createAddTasks() {
        // TODO: verify matrices dimensions and orientations
        Runnable[] tasks = new Runnable[leftMatrix.length()];

        for (int i = 0; i < leftMatrix.length(); i++) {
            SharedVector lhs = leftMatrix.get(i),
                         rhs = rightMatrix.get(i);

            tasks[i] = () -> lhs.add(rhs);
        }
        return List.of(tasks);
    }

    public List<Runnable> createMultiplyTasks() {
        // TODO: verify matrices dimensions and orientations
        Runnable[] tasks = new Runnable[leftMatrix.length()];

        for (int i = 0; i < leftMatrix.length(); i++) {
            SharedVector lhs = leftMatrix.get(i);
            tasks[i] = () -> lhs.vecMatMul(rightMatrix);
        }

        return List.of(tasks);
    }

    public List<Runnable> createNegateTasks() {
        Runnable[] tasks = new Runnable[leftMatrix.length()];

        for (int i = 0; i < leftMatrix.length(); i++) {
            SharedVector lhs = leftMatrix.get(i);
            tasks[i] = () -> lhs.negate();
        }

        return List.of(tasks);
    }

    public List<Runnable> createTransposeTasks() {
        Runnable[] tasks = new Runnable[leftMatrix.length()];

        for (int i = 0; i < leftMatrix.length(); i++) {
            SharedVector lhs = leftMatrix.get(i);
            tasks[i] = () -> lhs.transpose();
        }

        return List.of(tasks);
    }

    public String getWorkerReport() {
        return executor.getWorkerReport();
    }

    private boolean computableNode(ComputationNode node) {
        if (node == null ||
            node.getNodeType() == null ||
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
