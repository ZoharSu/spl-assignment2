package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        if (matrix == null)
            throw new IllegalArgumentException("Matrix is null");

        if (matrix.length == 0) {
            vectors = new SharedVector[0];
            return;
        }

        // Validity check
        for (int i = 1; i < matrix.length; i++) {
            if (matrix[i].length != matrix[0].length)
                // Check matrix validity so far
                throw new IllegalArgumentException("Invalid matrix");
        }
            
        vectors = new SharedVector[matrix.length];

        for (int i = 0; i < vectors.length; i++)
            vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
    }

    public void loadRowMajor(double[][] matrix) {
        if (matrix == null)
            throw new IllegalArgumentException("Matrix is null");

        if (matrix.length == 0) {
            vectors = new SharedVector[0];
            return;
        }

        // Validity check
        for (int i = 1; i < matrix.length; i++) {
            if (matrix[i].length != matrix[0].length)
                // Check matrix validity so far
                throw new IllegalArgumentException("Invalid matrix");
        }

        SharedVector[] newVectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++)
            newVectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);

        vectors = newVectors;
    }

    public void loadColumnMajor(double[][] matrix) {
        if (matrix == null)
            throw new IllegalArgumentException("Matrix is null");

        if (matrix.length == 0) {
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
        SharedVector[] tmp = vectors;
        if (tmp.length == 0)
            return new double[0][0];

        double[][] ret;
        acquireAllVectorReadLocks(tmp);
        
        if (tmp[0].getOrientation() == VectorOrientation.COLUMN_MAJOR) {
            ret = new double[tmp[0].length()][tmp.length];
            for (int j = 0; j < tmp.length; j++)
                for (int i = 0; i < tmp[j].length(); i++)
                    ret[i][j] = tmp[j].get(i);
            
        } else {
            ret = new double[tmp.length][tmp[0].length()];
            for (int i = 0; i < tmp.length; i++)
                for (int j = 0; j < tmp[i].length(); j++)
                    ret[i][j] = tmp[i].get(j);
            
        }
        releaseAllVectorReadLocks(tmp);
        return ret;
    }

    public SharedVector get(int index) {
        SharedVector[] tmp = vectors;
        if (index < 0 || index >= tmp.length)
            throw new IllegalArgumentException("Index out of bounds");
        
        return tmp[index];
    }

    public int length() {
        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        SharedVector[] tmp = vectors;
        if (tmp.length == 0)
            return null;

        // Assuming all vectors are of same orientation
        return tmp[0].getOrientation();
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        if (vecs == null) return;

        for (int i = 0; i < vecs.length; i++)
            vecs[i].readLock();
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        if (vecs == null) return;

        for (int i = vecs.length - 1; i >= 0; i--)
            vecs[i].readUnlock();
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        if (vecs == null) return;

        for (int i = 0; i < vecs.length; i++)
            vecs[i].writeLock();
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        if (vecs == null) return;

        for (int i = vecs.length - 1; i >= 0; i--)
            vecs[i].writeUnlock();
    }
}
