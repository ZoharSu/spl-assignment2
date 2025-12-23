package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        // TODO: initialize empty matrix
        vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        // TODO: construct matrix as row-major SharedVectors
        if (matrix.length == 0 || matrix[0].length == 0) {
            vectors = new SharedVector[0];
            return;
        }

        vectors = new SharedVector[matrix.length];
        for (int i = 0; i < vectors.length; i++) {
            if (matrix[i].length != matrix[0].length)
                // Check matrix validity so far
                throw new IllegalArgumentException("Invalid matrix");
            
            vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }
    }

    public void loadRowMajor(double[][] matrix) {
        // TODO: replace internal data with new row-major matrix
        if (matrix.length == 0 || matrix[0].length == 0) {
            vectors = new SharedVector[0];
            return;
        }

        SharedVector[] newVectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i].length != matrix[0].length)
                // Check matrix validity so far
                throw new IllegalArgumentException("Invalid matrix");
            
            newVectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }
        vectors = newVectors;
    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
        if (matrix.length == 0 || matrix[0].length == 0) {
            vectors = new SharedVector[0];
            return;
        }

        // Validity check
        for (int i = 1; i < matrix.length; i++) {
            if (matrix[i].length != matrix[0].length)
                // Check matrix validity so far
                throw new IllegalArgumentException("Invalid matrix");
        }

        SharedVector[] newVectors = new SharedVector[matrix[0].length];
        for (int j = 0; j < matrix[0].length; j++) {
            double[] column = new double[matrix.length];
            for (int i = 0; i < matrix.length; i++)
                column[i] = matrix[i][j];
            
            newVectors[j] = new SharedVector(column, VectorOrientation.COLUMN_MAJOR);
        }
        vectors = newVectors;
    }

    public double[][] readRowMajor() {
        // TODO: return matrix contents as a row-major double[][]
        SharedVector[] tmp = vectors;
        if (tmp.length == 0)
            return new double[0][0];

        double[][] ret;
        acquireAllVectorReadLocks(tmp);
        
        if (tmp[0].getOrientation() == VectorOrientation.COLUMN_MAJOR) {
            ret = new double[tmp[0].length()][tmp.length];
            for (int j = 0; j < tmp.length; j++) {
                for (int i = 0; i < tmp[j].length(); i++) {
                    ret[i][j] = tmp[j].get(i);
                }
            }
        } else {
            ret = new double[tmp.length][tmp[0].length()];
            for (int i = 0; i < tmp.length; i++) {
                for (int j = 0; j < tmp[i].length(); j++) {
                    ret[i][j] = tmp[i].get(j);
                }
            }
        }
        releaseAllVectorReadLocks(tmp);
        return ret;
    }

    public SharedVector get(int index) {
        // TODO: return vector at index
        SharedVector[] tmp = vectors;
        if (index < 0 || index >= tmp.length)
            throw new IllegalArgumentException("Index out of bounds");
        
        // this is fucked up
        return tmp[index];
    }

    public int length() {
        // TODO: return number of stored vectors
        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
        // Assuming all vectors are of same orientation
        if (length() == 0)
            return null; // this is fucked up

        return vectors[0].getOrientation();
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        for (int i = 0; i < length(); i++) {
            vecs[i].readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        for (int i = 0; i < length(); i++) {
            vecs[i].readUnlock(); // should this be reversed?
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        for (int i = 0; i < length(); i++) {
            vecs[i].writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
        for (int i = 0; i < length(); i++) {
            vecs[i].writeUnlock(); // should this be reversed?
        }
    }
}
