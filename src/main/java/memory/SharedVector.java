package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        this.vector = vector;
        this.orientation = orientation;
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        return 0;
    }

    public int length() {
        return vector.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        return null;
    }

    public void writeLock() {
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        // TODO: release write lock
    }

    public void readLock() {
        lock.readLock().lock();
    }

    public void readUnlock() {
        // TODO: release read lock
    }

    public void transpose() {
        writeLock();
        orientation = orientation == VectorOrientation.ROW_MAJOR ?
            VectorOrientation.COLUMN_MAJOR : VectorOrientation.ROW_MAJOR;
        writeUnlock();
    }

    public void add(SharedVector other) {
        // TODO: add two vectors
    }

    public void negate() {
        writeLock();
        for (int i = 0; i < vector.length; i++)
            vector[i] = -vector[i];
        writeUnlock();
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        return 0;
    }

    public void vecMatMul(SharedMatrix matrix) {
        writeLock();

        if (orientation == VectorOrientation.ROW_MAJOR)
            vecMatMulRow(matrix);

        if (orientation == VectorOrientation.COLUMN_MAJOR)
            vecMatMulCol(matrix);

        writeUnlock();
    }

    private void vecMatMulRow(SharedMatrix m) {
        // TODO: implement
        if (orientation != VectorOrientation.ROW_MAJOR)
            return;

    }

    private void vecMatMulCol(SharedMatrix m) {
        // TODO: implement
        if (orientation != VectorOrientation.COLUMN_MAJOR)
            return;
    }
}
