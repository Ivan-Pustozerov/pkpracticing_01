package operations;
import exceptions.InconsistentFunctionsException;
import functions.classes.Point;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import functions.interfaces.TabulatedFunction;

import static java.lang.Math.abs;

/// Сервис операций над Табулированными функциями
public class TabulatedFunctionOperationService {
///================Служебные-переменные==========================================================
    private TabulatedFunctionFactory factory;  // Фабрика для создания результата нужного формата

    private interface BiOperation{           //    Интерфейс
        double apply(double u, double v);   // бинарной операции
    }
///===============================================================================================

    /// Конструктор с аргументами
    public TabulatedFunctionOperationService(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }
    /// Конструктор по умолчанию
    public TabulatedFunctionOperationService(){
        factory = new ArrayTabulatedFunctionFactory();
    }

    ///представление функции в виде массива точек
    public static Point[] asPoints(TabulatedFunction func){
        int i=0;
        Point[] res = new Point[func.getCount()];
        for(Point p : func){
            res[i++] = p;
        }
        return res;
    }

    ///-------------------Операции-----------------------------------------------------------------
    public TabulatedFunction sum(TabulatedFunction a, TabulatedFunction b){
        BiOperation oper = (u, v) -> u+v;
        return doOperation(a, b, oper);
    }
    public TabulatedFunction sub(TabulatedFunction a, TabulatedFunction b){
        BiOperation oper = (u, v) -> u-v;
        return doOperation(a, b, oper);
    }
    public TabulatedFunction mul(TabulatedFunction a, TabulatedFunction b){
        BiOperation oper = (u, v) -> u*v;
        return doOperation(a, b, oper);
    }
    public TabulatedFunction div(TabulatedFunction a, TabulatedFunction b){
        BiOperation oper = (u, v) -> u/v;
        return doOperation(a, b, oper);
    }
    ///--------------------------------------------------------------------------------------------

    /// Фабрика применения операций
    private TabulatedFunction doOperation(TabulatedFunction a, TabulatedFunction b, BiOperation oper){
        double delta = 1e-16;
        // Если несовместны по количеству точек
        if(a.getCount()!= b.getCount())
            throw new InconsistentFunctionsException("functions have different number of dots");

        Point[] aP =asPoints(a);
        Point[] bP =asPoints(b);

        int len = aP.length;

        double[] xVals = new double[len];
        double[] yVals = new double[len];

        for(int i=0; i < len; ++i){
            if(abs(aP[i].x() - bP[i].x()) > delta)// Если xVals отличны
                throw new InconsistentFunctionsException("functions have different X");

            xVals[i] = aP[i].x();
            yVals[i] = oper.apply(aP[i].y(), bP[i].y());
        }
        return factory.create(xVals, yVals);
    }


    //геттеры/сеттеры
    public TabulatedFunctionFactory getFactory() {
        return factory;
    }
    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }
}
