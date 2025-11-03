package factory;

import functions.classes.LinkedListTabulatedFunction;
import functions.interfaces.TabulatedFunction;

public class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory{
    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues){return new LinkedListTabulatedFunction(xValues, yValues);}
}
