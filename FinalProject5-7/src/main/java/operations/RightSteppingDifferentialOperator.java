package operations;

import functions.interfaces.MathFunction;

public class RightSteppingDifferentialOperator extends SteppingDifferentialOperator{
    /// Конструктор
    public <T extends Number> RightSteppingDifferentialOperator(T step){
        super(step.doubleValue());
    }

    /// Функционал возврата производной
    @Override
    public MathFunction derive(MathFunction func){
        return new MathFunction() {
            @Override
            public <T extends Number> double apply(T X) {
                double x = X.doubleValue();
                double y = (func.apply(x + step) - func.apply(x)) / step;
                return y;
            }
        };
    }
}
