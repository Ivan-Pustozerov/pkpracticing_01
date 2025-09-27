package functions.classes;
import functions.interfaces.MathFunction;

import java.io.Serializable;

import static java.lang.Math.pow;//for using without namespaces

public class SqrFunction implements MathFunction {
    //variation for other prime types
    @Override
    public double apply(Object x)
    {
        return (x instanceof Number)? pow(((Number) x).doubleValue(),2.0) :Double.NaN;
    }
}
