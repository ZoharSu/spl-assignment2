package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        if (vector == null || orientation == null) 
            throw new IllegalArgumentException("Vector or orientation are null");
        
        this.vector = new double[vector.length];
        for (int i = 0; i < vector.length; i++)
            this.vector[i] = vector[i];

        this.orientation = orientation;
    }

    public double get(int index) {
        if (index < 0 || index >= length())
            throw new IllegalArgumentException("Index out of bounds");

        readLock();
        double ret = vector[index];
        readUnlock();
        
        return ret;
    }

    public int length() {
        // Is it volatile?
        return vector.length;
    }

    public VectorOrientation getOrientation() {
        return orientation;
    }

    public void writeLock() {
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        lock.writeLock().unlock();
    }

    public void readLock() {
        lock.readLock().lock();
    }

    public void readUnlock() {
        lock.readLock().unlock();
    }

    public void transpose() {
        writeLock();
        orientation = orientation == VectorOrientation.ROW_MAJOR ?
            VectorOrientation.COLUMN_MAJOR : VectorOrientation.ROW_MAJOR;
        writeUnlock();
    }

    public void add(SharedVector other) {
        if (getOrientation() != other.getOrientation())
            throw new IllegalArgumentException("Vectors are of different orientation");

        if (length() != other.length())
            throw new IllegalArgumentException("Vectors are of different length");

        if (this == other) {
            writeLock();

            for (int i = 0; i < vector.length; i++)
                vector[i] *= 2;

            writeUnlock();
        } else if (this.hashCode() < other.hashCode()) {
            writeLock();
            other.readLock();

            for (int i = 0; i < vector.length; i++)
                vector[i] += other.vector[i];

            other.readUnlock();
            writeUnlock();
        } else {
            other.readLock();
            writeLock();

            for (int i = 0; i < vector.length; i++)
                vector[i] += other.vector[i];

            writeUnlock();
            other.readUnlock();
        }
    }

    public void negate() {
        writeLock();
        
        for (int i = 0; i < vector.length; i++)
            vector[i] *= -1;

        writeUnlock();
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        // Does this need to be row and other column?
        // Or should we just check length?

        // Add case where this == other? though it still works
        // Also can rearrange locking and logic

        if (length() != other.length())
            throw new IllegalArgumentException("This and other are of different length");

        double ret = 0;
        if (this.hashCode() < other.hashCode()) {
            readLock();
            other.readLock();

            for (int i = 0; i < vector.length; i++)
                ret += vector[i] * other.vector[i];

            other.readUnlock();
            readUnlock();
        } else {
            other.readLock();
            readLock();

            for (int i = 0; i < vector.length; i++)
                ret += vector[i] * other.vector[i];

            readUnlock();
            other.readUnlock();
        }
        return ret;
    }

    public void vecMatMul(SharedMatrix matrix) {
        // Are there constraints?
        // Does this need to be a row?
        // Does the matrix need to be column major?

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