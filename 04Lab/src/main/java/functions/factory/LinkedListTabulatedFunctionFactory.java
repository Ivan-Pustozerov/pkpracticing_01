package functions.factory;

import functions.classes.LinkedListTabulatedFunction;
import functions.interfaces.TabulatedFunction;


/// Реализация фабрики для ListTabFunc
public class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {

    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues) {
        return new LinkedListTabulatedFunction(xValues, yValues);
    }

}
