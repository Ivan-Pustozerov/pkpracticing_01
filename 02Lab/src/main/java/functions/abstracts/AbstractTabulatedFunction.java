package functions.abstracts;

import functions.interfaces.MathFunction;
import functions.interfaces.TabulatedFunction;

public abstract class AbstractTabulatedFunction implements TabulatedFunction {
    private int count;
    protected abstract int floorIndexOfX(Object x);
    protected abstract double extrapolateLeft(Object x);
    protected abstract double extrapolateRight(Object x);
    protected abstract double interpolate(Object x,int floorIndex);
    protected double interpolate(double x,double leftX,double rightX,double leftY,double rightY){
        return leftY+(rightY-leftY)*(x-leftX)/(rightX-leftX);
    }
    public int getCount(){return count;}

    public double apply(Object X){
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
