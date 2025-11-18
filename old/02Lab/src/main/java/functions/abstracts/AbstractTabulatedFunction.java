package functions.abstracts;

import functions.interfaces.MathFunction;
import functions.interfaces.TabulatedFunction;

public abstract class AbstractTabulatedFunction implements TabulatedFunction {
    protected int count;
    protected final double delta=1e-32;

    protected abstract void sort();
    protected abstract int floorIndexOfX(Object x);
    protected abstract double extrapolateLeft(double x);
    protected abstract double extrapolateRight(double x);
    protected abstract double interpolate(double x,int floorIndex);

    protected double interpolate(double x,double leftX,double rightX,double leftY,double rightY){
        return leftY+(rightY-leftY)*(x-leftX)/(rightX-leftX);
    }

    @Override public int getCount(){return count;}
    @Override public double apply(Object X){
        if(X instanceof Number)
        {
            double x=((Number)X).doubleValue();
            if(x<leftBound()){
                return extrapolateLeft(x);
            }
            else if(x>rightBound()){
                return extrapolateRight(x);
            }
            else{
                int x_ind=indexOfX(x);
                if(x_ind==-1){
                    return interpolate(x,floorIndexOfX(x));
                }
                else{
                    return getY(x_ind);
                }
            }
        }
        else{
            return Double.NaN;
        }
    }
}
