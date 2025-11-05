package functions.factory;

import functions.classes.UnmodifiableTabulatedFunction;
import functions.interfaces.TabulatedFunction;

///  Абстрактная фабрика Табулированных функций
public interface TabulatedFunctionFactory {

    TabulatedFunction create(double[] xValues, double[] yValues);

    /// Неизменяемая функция
    default TabulatedFunction createUnmodifiable(double[] xVals, double[] yVals){
        return new UnmodifiableTabulatedFunction(create(xVals, yVals));
    }
}
