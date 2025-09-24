package functions.classes;
import functions.interfaces.MathFunction;

import java.io.Serializable;

import static java.lang.Math.pow;//for using without namespaces

public class SqrFunction implements MathFunction {
    @Override
    public double apply(double x){
        return pow(x,2.0);
    }
    //overload variation for other prime types
    public double apply(Object x)
    {
        //System.out.println(x);
        return (x instanceof Number)? apply(((Number) x).doubleValue()) :(Double.NaN);
    }
}
