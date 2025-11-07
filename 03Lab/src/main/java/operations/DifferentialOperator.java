package operations;

import functions.interfaces.MathFunction;

public interface DifferentialOperator <T extends MathFunction>{
    T derive(T function);
}
