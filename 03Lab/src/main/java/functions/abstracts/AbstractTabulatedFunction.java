package functions.abstracts;

import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import functions.classes.Point;
import functions.interfaces.TabulatedFunction;
import operations.TabulatedFunctionOperationService;

import java.io.Serial;
import java.io.Serializable;

import static java.lang.Math.abs;

/// конкретный функционал табулированных функций
public abstract class AbstractTabulatedFunction implements TabulatedFunction, Serializable {
 ///================Служебные-переменные==========================================================
    @Serial
    private static final long serialVersionUID = -5779587941366237851L;
    protected final static double delta=1e-32;  // Для сравнения double чисел
    protected int count;                       // Количество точек в фунции
///===============================================================================================

    ///общий механизм функции
    @Override
    public <T extends Number> double apply(T X)
    {
        double x = X.doubleValue();

        if (x < leftBound()) {return extrapolateLeft(x);}     // интерполяция
        if (x > rightBound()) {return extrapolateRight(x);}  //  на границах

        //Интерполяция посередине | взятие данных из таблицы
        int x_ind = indexOfX(x);
        return (x_ind == -1)? interpolate(x,floorIndexOfX(x)) : getY(x_ind);

    }

    /// Валидность длин массивов точек
    protected static void checkLengthIsTheSame(double[] xVals, double[] yVals){
        if(xVals.length != yVals.length){
            throw new DifferentLengthOfArraysException("arrays have different length");
        }
    }
    /// Валидность xVals для корректности функции
    protected static void checkSorted(double[] xVals){
        for(int i=0; i < xVals.length-1; ++i){
            if (xVals[i] > xVals[i+1] && abs(xVals[i] - xVals[i+1]) > delta ){
                throw new ArrayIsNotSortedException("arrays are not sorted");
            }
        }
    }

    /// стандартный функционал табулированных функций
    protected abstract void sort();
    protected abstract int floorIndexOfX(double x);
    protected abstract double extrapolateLeft(double x);
    protected abstract double extrapolateRight(double x);
    protected abstract double interpolate(double x,int floorIndex);

    /// Интерполяция
    protected double interpolate(double x,double leftX,double rightX,double leftY,double rightY){
        return leftY + (rightY-leftY) * (x-leftX) / (rightX-leftX);
    }

    // Количество точек - геттер
    @Override
    public int getCount(){return count;}

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append(getClass().getSimpleName()).append(" size = ").append(getCount() + "\n");

        int i=0;
        for(; i<getCount()-1;i++){
            sb.append("[").append(getX(i)).append("; ").append(getY(i)).append("]\n");
        }
        sb.append("[").append(getX(i)).append("; ").append(getY(i)).append("]");

        return sb.toString();
    }
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TabulatedFunction otherTab)) return false;

        int count = otherTab.getCount();
        if (getCount() != otherTab.getCount()) return false;

        Point[] ourPoints = TabulatedFunctionOperationService.asPoints(this);
        Point[] otherPoints = TabulatedFunctionOperationService.asPoints(otherTab);
        for (int i = 0; i < count; ++i) {
            if (abs(ourPoints[i].x()-otherPoints[i].x())>delta) return false;
            if (abs(ourPoints[i].y()-otherPoints[i].y())>delta) return false;
        }

        return true;
    }
}
