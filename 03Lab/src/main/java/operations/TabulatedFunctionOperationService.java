package operations;
import exceptions.InconsistentFunctionsException;
import functions.classes.Point;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import functions.interfaces.TabulatedFunction;

import static java.lang.Math.abs;


public class TabulatedFunctionOperationService {

    private TabulatedFunctionFactory factory;
    private static interface BiOperation{
        double apply(double u, double v);
    }

    private TabulatedFunction doOperation(TabulatedFunction a, TabulatedFunction b, BiOperation oper){
        double delta = 1e-16;
        Point[] aP =asPoints(a);
        Point[] bP =asPoints(b);

        if(aP.length != bP.length)
            throw new InconsistentFunctionsException("functions have different number of dots");

        int len = aP.length;

        double[] xVals = new double[len];
        double[] yVals = new double[len];

        for(int i=0; i < aP.length; ++i){
            if(abs(aP[i].x - bP[i].x) > delta)
                throw new InconsistentFunctionsException("functions have different X");

            xVals[i] = aP[i].x;
            yVals[i] = oper.apply(aP[i].y, bP[i].y);
        }
        return factory.create(xVals, yVals);
    }

    public TabulatedFunctionOperationService(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }
    public TabulatedFunctionOperationService(){
        factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }
    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public static Point[] asPoints(TabulatedFunction func){
        int i=0;
        Point[] res = new Point[func.getCount()];
        for(Point p : func){
            res[i++] = p;
        }
        return res;
    }

    public TabulatedFunction sum(TabulatedFunction a, TabulatedFunction b){
        BiOperation oper = (u, v) -> u+v;
        return doOperation(a, b, oper);
    }
    public TabulatedFunction sub(TabulatedFunction a, TabulatedFunction b){
        BiOperation oper = (u, v) -> u-v;
        return doOperation(a, b, oper);
    }
}
