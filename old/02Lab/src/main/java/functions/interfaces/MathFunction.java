package functions.interfaces;

import functions.classes.CompositeFunction;

public interface MathFunction {
    double apply(Object x);//any method will be auto public in implemented class
    default CompositeFunction andThen(MathFunction afterFunction){
        return new CompositeFunction(afterFunction,this);
    }
}
