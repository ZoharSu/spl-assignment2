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
        executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced

        // ERROR handling
        
        while (computationRoot.getNodeType() != ComputationNodeType.MATRIX) {
            ComputationNode toResolve = computationRoot.findResolvable();
            toResolve.associativeNesting();
            toResolve = toResolve.findResolvable();
            loadAndCompute(toResolve);
        }

        return null;
    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        return null;
    }

    public List<Runnable> createMultiplyTasks() {

        // ERROR HANDLING

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            SharedVector lhs = leftMatrix.get(i);
            tasks.add(() -> lhs.vecMatMul(rightMatrix));
        }

        return tasks;
    }

    public List<Runnable> createNegateTasks() {

        // ERROR HANDLING

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            SharedVector lhs = leftMatrix.get(i);
            tasks.add(() -> lhs.negate());
        }

        return tasks;
    }

    public List<Runnable> createTransposeTasks() {

        // ERROR HANDLING

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            SharedVector lhs = leftMatrix.get(i);
            tasks.add(() -> lhs.transpose());
        }

        return tasks;
    }

    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return null;
    }
}
