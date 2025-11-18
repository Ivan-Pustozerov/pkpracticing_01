package functions.classes;
import java.lang.Math;
import java.util.HashMap;
import functions.interfaces.MathFunction;
import functions.interfaces.MathFunction2args;

import static java.lang.Math.round;


/// Реализация Метода Рунге-Кутты для ДУ заданного f(x,y)
public class RungeKuttaMethod implements MathFunction {
///================Служебные-переменные==========================================================
    protected final double step;                               // Шаг
    protected double x0;                                      // Начальное условие - ЗК
    protected MathFunction2args dfunc;                       // ДУ f(x,y)
    protected HashMap<Double,Double> Y_x = new HashMap<>(); // Таблица сохранения результатов
///===============================================================================================

    /// Конструктор с Условиями Коши
    public RungeKuttaMethod(MathFunction2args dfunc, double x0, double y0, double step){
        this.dfunc=dfunc;
        this.x0=x0;
        this.step=step;
        Y_x.put(x0,y0);
    }

    /// Решение задачи Коши - поведение восстановленной функции
    @Override
    public <T extends Number> double apply(T X)
    {
        double x= X.doubleValue();

        if(Y_x.containsKey(x)) return Y_x.get(x);

        else{
            int steps = (int)round( (Math.abs(x - x0)) / step);
            int sign = (int)( (x-x0) / Math.abs(x-x0) );

            double x_curr=x0;
            double y_curr=Y_x.get(x0);

            return cycle(x_curr, y_curr, sign*step, steps);
        }
    }

    ///-------------------Служебное----------------------------------------------------
    protected double Ynext(double x, double y, double step_){
        double k1=dfunc.apply(x,y);
        double k2=dfunc.apply(x + step/2, y + k1*step/2);
        double k3=dfunc.apply(x + step/2, y + k2*step/2);
        double k4=dfunc.apply(x + step, y + k3*step);

        return y + step_*(k1 + 2*k2 + 2*k3 + k4) / 6;
    }

    protected double cycle(double x_curr, double y_curr, double step_, int steps){
        for(int i = 0; i<steps; ++i){
            if(!Y_x.containsKey(x_curr)){
                y_curr=Ynext(x_curr, y_curr, step_);
                Y_x.put(x_curr, y_curr);
            }
            else{
                y_curr=Y_x.get(x_curr);
            }
            x_curr+=step_;
        }
        return y_curr;
    }



}










