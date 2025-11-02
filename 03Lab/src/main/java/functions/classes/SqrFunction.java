package functions.classes;
import functions.interfaces.MathFunction;

import static java.lang.Math.pow;//for use without namespaces

public class SqrFunction implements MathFunction {

    @Override
    public <T extends Number> double apply(T x)
    {
        return pow(x.doubleValue(), 2.0);
    }
}
