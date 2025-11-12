package functions.factory;

import functions.classes.ArrayTabulatedFunction;
import functions.interfaces.TabulatedFunction;

/// Реализация фабрики для ArrayTabFunc
public class ArrayTabulatedFunctionFactory  implements TabulatedFunctionFactory {

    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues) {
        return new ArrayTabulatedFunction(xValues, yValues);
    }
}
