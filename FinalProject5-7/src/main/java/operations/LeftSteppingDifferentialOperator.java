package operations;

import functions.interfaces.MathFunction;

/// Оператор левой производной
public class LeftSteppingDifferentialOperator extends SteppingDifferentialOperator{

    /// Конструктор
    public <T extends Number> LeftSteppingDifferentialOperator(T step){
        super(step.doubleValue());
    }

    /// Функционал возврата производной
    @Override
    public MathFunction derive(MathFunction func){
        return new MathFunction() {
            @Override
            public <T extends Number> double apply(T X) {
                double x = X.doubleValue();
                double y = (func.apply(x) - func.apply(x-step)) / step;
                return y;
            }
        };
    }
}
