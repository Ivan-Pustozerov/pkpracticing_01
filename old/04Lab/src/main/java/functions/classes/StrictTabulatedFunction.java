package functions.classes;

import functions.interfaces.TabulatedFunction;

import java.util.Iterator;

public class StrictTabulatedFunction implements TabulatedFunction {
    private final TabulatedFunction func;
    ///А надо ли? private final
    /// Да, можно, т.к это оптимизация компиля
    /// Но в будущем может поменяться что-либо
    /// Кстати, зацени Класс Point
    public StrictTabulatedFunction(TabulatedFunction func){
        this.func=func;
    }
    @Override
    public int getCount() {
        return func.getCount();
    }
    @Override
    public double getX(int index){
        return func.getX(index);
    }

    @Override
    public double getY(int index){
        return func.getY(index);
    }

    @Override
    public  <T extends Number> void setY(int index,T y){
        func.setY(index,y);
    }
    @Override
    public <T extends Number> void setX(int index,T x){
         func.setX(index,x);
    }
    @Override
    public <T extends Number> int indexOfX(T x){
        return func.indexOfX(x);
    }
    @Override
    public <T extends Number> int indexOfY(T y){
        return func.indexOfY(y);
    }

    @Override
    public double leftBound(){
        return func.leftBound();
    }
    @Override
    public double rightBound(){
        return func.rightBound();
    }
    @Override
    public <T extends Number>  double apply(T x){
        int ind = func.indexOfX(x);
        if(ind != -1){
            return func.getY(ind);
        }
        throw new UnsupportedOperationException("А по голове");

    }
    @Override
    public Iterator<Point> iterator(){
        return func.iterator();
    }
}
