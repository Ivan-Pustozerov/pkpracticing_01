package operations;

import functions.interfaces.MathFunction;

/// Оператор производной функции
public abstract class SteppingDifferentialOperator implements DifferentialOperator<MathFunction> {
///===============Служебные=Переменные======================================================
    protected double step; // Шаг дифференцирования
///=========================================================================================

    /// Конструктор
    public SteppingDifferentialOperator(double step){
        if (step < 0 || Double.isNaN(step) || Double.isInfinite(step) )
            throw new IllegalArgumentException("Некорректный шаг производной");
        this.step = step;
    }

///-------------------Геттер/Сеттер---------------------------------------------------------
    public void setStep(double step) {
        this.step = step;
    }
    public double getStep() {
        return step;
    }
}
