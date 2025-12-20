package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        this.vector = vector;
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
        // TODO: return vector length
        return vector.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        return orientation;
    }

    public void writeLock() {
        // TODO: acquire write lock
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        // TODO: release write lock
        lock.writeLock().unlock();
    }

    public void readLock() {
        // TODO: acquire read lock
        lock.readLock().lock();
    }

    public void readUnlock() {
        // TODO: release read lock
        lock.readLock().unlock();
    }

    public void transpose() {
        // TODO: transpose vector
        writeLock(); // should we?
        if (orientation == VectorOrientation.COLUMN_MAJOR)
            orientation = VectorOrientation.ROW_MAJOR;
        else
            orientation = VectorOrientation.COLUMN_MAJOR;
        writeUnlock();
    }

    public void add(SharedVector other) {
        // TODO: add two vectors
        if (length() != other.length())
            throw new IllegalArgumentException("This and other are of different length");

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
        // TODO: compute row-vector × matrix

    }
}