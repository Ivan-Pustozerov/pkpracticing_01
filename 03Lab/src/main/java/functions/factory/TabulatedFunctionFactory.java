package functions.factory;

import functions.classes.UnmodifiableTabulatedFunction;
import functions.interfaces.TabulatedFunction;

public interface TabulatedFunctionFactory {
    TabulatedFunction create(double[] xVals, double[] yVals);

    default TabulatedFunction createUnmodifiable(double[] xVals, double[] yVals){
        return new UnmodifiableTabulatedFunction(create(xVals, yVals));
    }
}
