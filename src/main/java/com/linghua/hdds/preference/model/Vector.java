package com.linghua.hdds.preference.model;

import java.util.Map;

public class Vector {
}

class SparseVector {

    private static SparseVector ZERO = new SparseVector(0, null, null);

    public int nnz;
    public int[] indics;
    public double[] values;

    public SparseVector() {
    }

    private SparseVector(int nnz, int[] indics, double[] values) {
        this.nnz = nnz;
        if (nnz == 0) {
            this.indics = new int[0];
            this.values = new double[0];
        }
    }

    public static SparseVector toSparseVector(Map<Integer, Float> vector) {

        if (vector == null || vector.size() == 0)
            return ZERO;
        SparseVector sparse = new SparseVector();
        sparse.nnz = vector.size();
        sparse.indics = new int[sparse.nnz];
        sparse.values = new double[sparse.nnz];
        int i = 0;
        for (Map.Entry<Integer, Float> entry : vector.entrySet()) {
            sparse.indics[i] = entry.getKey();
            sparse.values[i] = entry.getValue();
            i++;
        }

        return sparse;
    }

    // y := alpha*A +beta*Y
    private double axpy(double a, double b, SparseVector y) {

        return 0;
    }

}

class DenseVector {

    double[] values;

    public static DenseVector toDenseVector(int length, Map<Integer, Double> vector) {

        int size = vector.size();
        if (size <= length) {
            DenseVector dense = new DenseVector();
            dense.values = new double[length];
            vector.forEach((l, r) -> dense.values[l] = r);
            return dense;
        }

        DenseVector dense = new DenseVector();
        dense.values = new double[length];
        vector.forEach((l, r) -> {
            if (l < length)
                dense.values[l] = r;
        });
        return dense;

    }

    public double axpy(double a, double b, SparseVector y) {
        if (a == 0 && b == 0) {

            return 0;
        } else if (a == 0) {

            return b;
        } else if (b == 0) {

            return a;
        }

        int yLength = y.indics.length;
        int[] yIndics = y.indics;
        double[] yValues = y.values;

        int i = 0;
        double r = 0;
        while (i < yLength) {
            r += values[yIndics[i]] * yValues[i];
            i+=1;
        }

        return r;
    }

}