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
        if (other == null ||
            getOrientation() != other.getOrientation() ||
            length() != other.length())
        {
            throw new IllegalArgumentException("Vector is null or length,orientation mismatch");
        }

        if (this == other) {
            writeLock();

            for (int i = 0; i < vector.length; i++)
                vector[i] *= 2;

            writeUnlock();
            return;
        }

        if (this.hashCode() < other.hashCode()) {
            writeLock();
            other.readLock();
        } else {
            other.readLock();
            writeLock();
        }

        for (int i = 0; i < vector.length; i++)
            vector[i] += other.vector[i];

        if (this.hashCode() < other.hashCode()) {
            other.readUnlock();
            writeUnlock();
        } else {
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

        if (other == null || length() != other.length())
            throw new IllegalArgumentException("Vector is null or length mismatch");

        double ret = 0;
        if (this.hashCode() < other.hashCode()) {
            readLock();
            other.readLock();
        } else {
            other.readLock();
            readLock();
        }

        for (int i = 0; i < vector.length; i++)
            ret += vector[i] * other.vector[i];

        if (this.hashCode() < other.hashCode()) {
            other.readUnlock();
            readUnlock();
        } else {
            readUnlock();
            other.readUnlock();
        }
        return ret;
    }

    public void vecMatMul(SharedMatrix matrix) {
        if (orientation == VectorOrientation.COLUMN_MAJOR ||
            matrix == null ||
            matrix.length() == 0)
        {
            throw new IllegalArgumentException("Vector is column or matrix is null/empty");
        }

        if (matrix.getOrientation() == VectorOrientation.ROW_MAJOR)
            vecMatMulRow(matrix);
        else if (matrix.getOrientation() == VectorOrientation.COLUMN_MAJOR)
            vecMatMulCol(matrix);
        else
            throw new IllegalArgumentException("Matrix has no orientation");
    }

    private void vecMatMulRow(SharedMatrix m) {
        // Assuming vector is a row and matrix is row major
        if (length() != m.length())
            throw new IllegalArgumentException("Vector and matrix dimensions mismatch");

        double[][] mRow = m.readRowMajor();
        double[] newVec = new double[mRow[0].length];
        readLock();

        for (int i = 0; i < vector.length; i++)
            for (int j = 0; j < mRow[0].length; j++)
                newVec[j] += vector[i] * mRow[i][j];

        readUnlock();
        writeLock();
        vector = newVec;
        writeUnlock();
    }

    private void vecMatMulCol(SharedMatrix m) {
        // Assuming vector is a row and matrix is column major
        if (length() != m.get(0).length())
            throw new IllegalArgumentException("Vector and matrix dimensions mismatch");

        double[] newVec = new double[m.length()];
        readLock();

        for (int i = 0; i < m.length(); i++)
            newVec[i] = dot(m.get(i));
        
        readUnlock();

        writeLock();
        vector = newVec;
        writeUnlock();
    }
}