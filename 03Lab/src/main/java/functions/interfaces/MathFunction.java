package functions.interfaces;

import functions.classes.CompositeFunction;

public interface MathFunction {

    <T extends Number> double apply(T x);

    default CompositeFunction andThen(MathFunction afterFunction){
        return new CompositeFunction(afterFunction,this);
    }
}
