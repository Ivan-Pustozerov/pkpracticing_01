package concurrent;

import functions.classes.Point;
import functions.interfaces.TabulatedFunction;
import operations.TabulatedFunctionOperationService;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Iterator;
import java.util.NoSuchElementException;

@ThreadSafe
public class SynchronizedTabulatedFunction implements TabulatedFunction {
    private final TabulatedFunction tfunc; //final чтобы была корректная публикация + атомарность конструктора
    private final static Object EditLock = new Object();
    private final static Object CountLock = new Object();
    private final static Object getXLock = new Object();
    private final static Object getYLock = new Object();
    private final static Object IndXLock = new Object();
    private final static Object IndYLock = new Object();
    private final static Object leftBondLock = new Object();
    private final static Object rightBondLock = new Object();
    private final static Object applyLock = new Object();
    //остальные методы из AbstractTabFunc не нужны?

    public SynchronizedTabulatedFunction(TabulatedFunction tfunc) {
        this.tfunc = tfunc;
    }
    @Override
    public int getCount(){
        synchronized (CountLock){
            synchronized (EditLock) {
                return tfunc.getCount();
            }
        }
    }
    @Override
    public double getX(int index){
        synchronized (getXLock){
            synchronized (EditLock) {
                return tfunc.getX(index);
            }
        }
    };
    @Override
    public double getY(int index){
        synchronized (getYLock){
            return tfunc.getY(index);
        }
    };

    @Override
    public <T extends Number> void setY(int index, T y){
        synchronized (EditLock){
            tfunc.setY(index,y);
        }
    };
    @Override
    public <T extends Number> void setX(int index, T x){
        synchronized (EditLock) {
            synchronized (EditLock) {
                tfunc.setX(index, x);
            }
        }
    };
    @Override
    public <T extends Number> int indexOfX(T x){
        synchronized (IndXLock){
            synchronized (EditLock) {
                return tfunc.indexOfX(x);
            }
        }
    };
    @Override
    public <T extends Number> int indexOfY(T y){
        synchronized (IndYLock) {
            synchronized (EditLock) {
                return tfunc.indexOfY(y);
            }
        }
    };
    @Override
    public double leftBound(){
        synchronized (leftBondLock) {
            synchronized (EditLock) {
                return tfunc.leftBound();
            }
        }
    };
    @Override
    public double rightBound(){
        synchronized (rightBondLock) {
            synchronized (EditLock) {
                return tfunc.rightBound();
            }
        }
    };
    @Override
    public <T extends Number> double apply(T x) {
        synchronized (applyLock) {
            synchronized (EditLock) {
                return tfunc.apply(x);
            }
        }
    }
    @Override
    public Iterator<Point> iterator() {
        Point[] points;
        synchronized(tfunc) {
            points = TabulatedFunctionOperationService.asPoints(tfunc);
        }
        return new Iterator<Point>() {
            private int index = 0;
            private final Point[] pointsCopy = points;
            @Override
            public boolean hasNext() {
                    return index < pointsCopy.length;
                }
            @Override
            public Point next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return pointsCopy[index++];
            }
        };
    }
    public interface Operation<T> {
        T apply(SynchronizedTabulatedFunction func);
    }

    public <T> T doSynchronously(Operation<? extends T> operation) {
        synchronized (EditLock) {
            return operation.apply(this);
        }
    }
}
