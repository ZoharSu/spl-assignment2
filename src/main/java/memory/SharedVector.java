package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        this.vector = vector; // should we copy the array?
        this.orientation = orientation;
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
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
        if (length() != other.length())
            throw new IllegalArgumentException("This and other are of different length");

        if (this == other) {
            writeLock();

            for (int i = 0; i < vector.length; i++)
                vector[i] *= 2;

            writeUnlock();
        }

        if (this.hashCode() < other.hashCode()) {
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
        // TODO: negate vector
        writeLock();
        
        for (int i = 0; i < vector.length; i++)
            vector[i] *= -1;

        writeUnlock();
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)

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