package functions.classes;
import functions.interfaces.TabulatedFunction;
import java.util.Iterator;

/// Обертка Неизменяемая Табулированная Функция -
public class UnmodifiableTabulatedFunction implements TabulatedFunction{
///================Служебные-переменные==========================================================
    private TabulatedFunction func;
///===============================================================================================

    /// Конструктор с аргументом
    public UnmodifiableTabulatedFunction(TabulatedFunction func){
        this.func = func;
    }

    /// Запрет по сеттерам:
    @Override
    public <T extends Number> void setY(int index,T y){
        throw new UnsupportedOperationException("Y is constant");
    }

    @Override
    public <T extends Number> void setX(int index,T x){
        throw new UnsupportedOperationException("X is constant");
    }

    /// геттер самой функции
    public TabulatedFunction getRawFunction(){
        return func;
    }




    // старое
    @Override public int getCount(){
        return func.getCount();
    }
    @Override public double getX(int index){
        return func.getX(index);
    }
    @Override public double getY(int index){
        return func.getY(index);
    }
    @Override public <T extends Number> int indexOfX(T x){
        return func.indexOfX(x);
    }
    @Override public <T extends Number> int indexOfY(T y){
        return func.indexOfY(y);
    }
    @Override public double leftBound(){
        return func.leftBound();
    }
    @Override public double rightBound(){
        return func.rightBound();
    }
    @Override public Iterator<Point> iterator(){
        return func.iterator();
    }
    @Override public <T extends Number> double apply(T x)
    {
        return func.apply(x);
    }


}
