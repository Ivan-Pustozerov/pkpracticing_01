package concurrent;

import functions.classes.Point;
import functions.interfaces.TabulatedFunction;
import operations.TabulatedFunctionOperationService;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SynchronizedTabulatedFunction implements TabulatedFunction {
    private final TabulatedFunction tfunc; //final чтобы была корректная публикация + атомарность конструктора
    public SynchronizedTabulatedFunction(TabulatedFunction tfunc) {
        this.tfunc = tfunc;
    }
    @Override
    public synchronized int getCount(){
        return tfunc.getCount();
    }
    @Override
    public synchronized double getX(int index){
        return tfunc.getX(index);

    };
    @Override
    public synchronized double getY(int index){
        return tfunc.getY(index);
    };

    @Override
    public synchronized<T extends Number> void setY(int index, T y){
        tfunc.setY(index,y);
    };
    @Override
    public synchronized<T extends Number> void setX(int index, T x){
        tfunc.setX(index,x);
    };
    @Override
    public synchronized<T extends Number> int indexOfX(T x){
        return tfunc.indexOfX(x);
    };
    @Override
    public synchronized<T extends Number> int indexOfY(T y){
        return tfunc.indexOfY(y);
    };
    @Override
    public synchronized double leftBound(){
        return tfunc.leftBound();
    };
    @Override
    public synchronized double rightBound(){
        return tfunc.rightBound();

    };
    @Override
    public synchronized<T extends Number> double apply(T x) {
        return tfunc.apply(x);
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
        synchronized (tfunc) {
            return operation.apply(this);
        }
    }
}
