package functions.classes;
import functions.interfaces.MathFunction;

/// Функия f(x) = x
public class IdentityFunction implements MathFunction{

    @Override
    public <T extends Number> double apply(T x) {
        return x.doubleValue();
    }
}

