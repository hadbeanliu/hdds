package test;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.util.*;

import java.util.Random;

public class MitrixLearning {
    private static MitrixLearning ourInstance = new MitrixLearning();

    public static MitrixLearning getInstance() {
        return ourInstance;
    }

    private MitrixLearning(){
    }


    public static void main(String[] args){
        Random r=new Random();
        double[][] data=new double[2][2];
        data[0][0]=1;
        data[0][1]=2;
        data[1][0]=3;
        data[1][1]=-1;
        System.out.println();
        RealMatrix matrix1=MatrixUtils.createRealMatrix(data);
        RealMatrix squalMatrix = matrix1.multiply(matrix1.transpose());
        System.out.println(squalMatrix);
        RealMatrix result=squalMatrix.multiply(getInverse(squalMatrix));
        System.out.println(result);

    }
    public static RealMatrix getInverse(RealMatrix matrix){
        RealMatrix realMatrix = new LUDecomposition(matrix).getSolver().getInverse();
        System.out.println(new LUDecomposition(matrix).getL());
        System.out.println(new LUDecomposition(matrix).getU() );
        System.out.println(new LUDecomposition(matrix).getP() );
        return realMatrix;
    }
}
