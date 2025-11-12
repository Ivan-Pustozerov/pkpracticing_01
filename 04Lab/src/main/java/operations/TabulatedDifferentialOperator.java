package operations;

import functions.classes.Point;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import functions.interfaces.TabulatedFunction;


public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction>{
    ///=====Фабрика===================================================================================
    TabulatedFunctionFactory factory;
    ///==========Конструкторы=========================================================================
    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory){
        this.factory=factory;
    }
    public TabulatedDifferentialOperator(){
        this.factory=new ArrayTabulatedFunctionFactory();
    }
    ///==================Геттер+Сеттер=================================================================
    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }
    ///-------------------МЕТОДЫ-----------------------------------------------------------------------
    ///Диффренциирование табулированных функция(попытка нумер 1)
    @Override
    public TabulatedFunction derive(TabulatedFunction function){
        ///Получаем точки
        Point[] points = TabulatedFunctionOperationService.asPoints(function);
        int count = points.length;
        ///Создаем массивы той же длинны
        double[] xVal = new double[count];
        double[] yVal = new double[count];
        ///заполнение
        for (int i = 0; i < count; i++) {
            xVal[i] = points[i].x();
        }
        ///Численной дифференцирование
        //Метод из методички(логично =)  )
        //Можно было и более точно, но и так сойдет

        //Для первых n-1 точек используем правую разностную производную
        for (int i = 0; i < count - 1; i++) {
            yVal[i] = (points[i + 1].y() - points[i].y()) / (points[i + 1].x() - points[i].x());}
        //"последнее значение вычисляется по формуле левой производной, т.е. оно такое же, как и предпоследнее."
        yVal[count - 1] = yVal[count - 2];

        return factory.create(xVal, yVal);

        }
    ///===============================================================================================

}
