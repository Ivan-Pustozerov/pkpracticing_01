package functions.classes;
import functions.interfaces.MathFunction;

public class IdentityFunction implements MathFunction{
    @Override
    public double apply(Object x) {
        return (x instanceof Number) ? ((Number) x).doubleValue() : Double.NaN;
    }

}
