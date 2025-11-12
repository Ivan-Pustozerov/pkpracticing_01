package functions.interfaces;

import functions.classes.CompositeFunction;


/// Общий функционал математической функции от одной перменной
public interface MathFunction {
    <T extends Number> double apply(T x);
    default CompositeFunction andThen(MathFunction afterFunction){
        return new CompositeFunction(afterFunction,this);
    }
}
