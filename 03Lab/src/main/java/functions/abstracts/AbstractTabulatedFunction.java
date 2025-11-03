package functions.abstracts;

import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import functions.interfaces.TabulatedFunction;

import static java.lang.Math.abs;

public abstract class AbstractTabulatedFunction implements TabulatedFunction {
    protected int count;
    protected final static double delta=1e-32;
    protected static void checkLengthIsTheSame(double[] xVals, double[] yVals){
        if(xVals.length != yVals.length){
            throw new DifferentLengthOfArraysException("Arrays have different length");
        }
    }
    protected static void checkSorted(double[] xVals){
        for(int i=0; i < xVals.length-1; ++i){
            if (xVals[i] > xVals[i+1] && abs(xVals[i] - xVals[i+1]) > delta ){
                throw new ArrayIsNotSortedException("Arrays are not sorted");
            }
        }
    }


    protected abstract void sort();
    protected abstract int floorIndexOfX(double x);
    protected abstract double extrapolateLeft(double x);
    protected abstract double extrapolateRight(double x);
    protected abstract double interpolate(double x,int floorIndex);

    protected double interpolate(double x,double leftX,double rightX,double leftY,double rightY){
        return leftY+(rightY-leftY)*(x-leftX)/(rightX-leftX);
    }

    @Override
    public int getCount(){return count;}

    @Override
    public <T extends Number> double apply(T X)
    {

        double x = X.doubleValue();
        /*
        if(x < leftBound()){
            return extrapolateLeft(x);
        }
        else if(x > rightBound()){
            return extrapolateRight(x);
        }
        else{
            int x_ind = indexOfX(x);
            if(x_ind == -1){
                return interpolate(x,floorIndexOfX(x));
            }
            else{
                return getY(x_ind);
            }
        }*/
        if (x < leftBound()) return extrapolateLeft(x);
        if (x > rightBound()) return extrapolateRight(x);

        int pos = indexOfX(x);
        if (pos != -1) return getY(pos);

        int floorPos = floorIndexOfX(x);
        return interpolate(x, floorPos);
    }
}
