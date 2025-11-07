package functions.classes;
import functions.interfaces.MathFunction;

/// Реализация Постоянной функции f(x) = Const
public class ConstantFunction implements MathFunction {
///================Служебные-переменные======================================
    private final double Num;
///==========================================================================

    /// Конструктор с аргументом
    public <T extends Number> ConstantFunction(T a){
        Num=a.doubleValue();
    }

    @Override /// Основной механизм функции
    public <T extends Number> double apply(T x){return Num;}

    // геттер
    public double getConst(){
        return Num;
    }
}
