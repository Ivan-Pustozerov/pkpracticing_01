package functions.factory;

import functions.classes.StrictTabulatedFunction;
import functions.classes.UnmodifiableTabulatedFunction;
import functions.interfaces.TabulatedFunction;

///  Абстрактная фабрика Табулированных функций
public interface TabulatedFunctionFactory {
    TabulatedFunction create(double[] xValues, double[] yValues);
    /// Неизменяемая функция
    default TabulatedFunction createUnmodifiable(double[] xVals, double[] yVals){
        return new UnmodifiableTabulatedFunction(create(xVals, yVals));
    }
    default TabulatedFunction createStrict(double[] xVals, double[] yVals){
        return new StrictTabulatedFunction(create(xVals, yVals));
    }
    default TabulatedFunction createUnmodifiableStrict(double[] xVals, double[] yVals){
        TabulatedFunction func = create(xVals, yVals);
        return new UnmodifiableTabulatedFunction(new StrictTabulatedFunction(func));
    }
}
