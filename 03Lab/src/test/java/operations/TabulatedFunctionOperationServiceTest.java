package operations;

import exceptions.InconsistentFunctionsException;
import functions.classes.ArrayTabulatedFunction;
import functions.classes.LinkedListTabulatedFunction;
import functions.classes.Point;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import functions.interfaces.TabulatedFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionOperationServiceTest {

    private static final double[] xs = {1, 2, 3, 4, 5};
    private static final double[] ys = {10, 20, 30, 40, 50};
    private Point[] pnts;
    private ArrayTabulatedFunction Afunc;
    private LinkedListTabulatedFunction Lfunc;
    private TabulatedFunctionOperationService service;

    @BeforeEach
    void setUp(){
        Afunc = new ArrayTabulatedFunction(xs,ys);
        Lfunc = new LinkedListTabulatedFunction(xs,ys);

        pnts = new Point[xs.length];
        for(int i=0; i<xs.length; ++i){
            pnts[i] = new Point(xs[i], ys[i]);
        }
        service = new TabulatedFunctionOperationService();
    }

    @Test
    void constructorArgsTest(){
        TabulatedFunctionFactory Afactory = new ArrayTabulatedFunctionFactory();
        TabulatedFunctionFactory Lfactory = new LinkedListTabulatedFunctionFactory();

        service = new TabulatedFunctionOperationService(Afactory);
        assertNotNull(service); service = null;

        service = new TabulatedFunctionOperationService(Lfactory);
        assertNotNull(service);
    }
    @Test
    void constructorTest(){
        assertNotNull(service);
    }

    @Test
    void asPointsTest() {
        for(int i=0; i<pnts.length; ++i){
            assertEquals(pnts[i], TabulatedFunctionOperationService.asPoints(Afunc)[i]);
            assertEquals(pnts[i], TabulatedFunctionOperationService.asPoints(Lfunc)[i]);
        }
    }


    @Test
    void getFactory() {
        assertInstanceOf(ArrayTabulatedFunctionFactory.class, service.getFactory());
    }

    @Test
    void setFactory() {
        service.setFactory(new LinkedListTabulatedFunctionFactory());
        assertInstanceOf(LinkedListTabulatedFunctionFactory.class, service.getFactory());
    }

    @Test
    void DifferentLenArgsOperation(){
        final double[] x1MisLen = {1,2,3};
        final double[] y1MisLen = {4,5,6};

        final double[] x2MisLen = {1,2,3,4};
        final double[] y2MisLen = {4,5,6,3};

        Afunc = new ArrayTabulatedFunction(x1MisLen,y1MisLen);
        Lfunc = new LinkedListTabulatedFunction(x2MisLen,y2MisLen);

        assertThrows(InconsistentFunctionsException.class, () -> service.sum(Afunc, Lfunc));
    }

    @Test
    void DifferentXArgsOperation(){
        final double[] x1MisX = {1,2,3};
        final double[] y1MisX = {4,5,6};

        final double[] x2MisX = {1,3,4};
        final double[] y2MisX = {4,5,6};

        Afunc = new ArrayTabulatedFunction(x1MisX,y1MisX);
        Lfunc = new LinkedListTabulatedFunction(x2MisX,y2MisX);

        assertThrows(InconsistentFunctionsException.class, () -> service.sum(Afunc, Lfunc));
    }

    @Test
    void sum() {
        int i=0;
        TabulatedFunction res;

        ///with Array factory:
        //Array + List
        res = service.sum(Afunc, Lfunc);
        assertInstanceOf(ArrayTabulatedFunction.class, res);
        for(Point p : res){
            assertEquals(Lfunc.getX(i), p.x());
            assertEquals(Afunc.getY(i) + Lfunc.getY(i), p.y());
            ++i;
        }
        i=0;
        //Array + Array:
        res = service.sum(Afunc, Afunc);
        assertInstanceOf(ArrayTabulatedFunction.class, res);
        for(Point p : res){
            assertEquals(Afunc.getX(i), p.x());
            assertEquals(Afunc.getY(i) + Afunc.getY(i), p.y());
            ++i;
        }
        i=0;
        //List + List:
        res = service.sum(Lfunc, Lfunc);
        assertInstanceOf(ArrayTabulatedFunction.class, res);
        for(Point p : res){
            assertEquals(Lfunc.getX(i), p.x());
            assertEquals(Lfunc.getY(i) + Lfunc.getY(i), p.y());
            ++i;
        }
        i=0;

        ///with List factory:
        //Array + List
        service = new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());
        res = service.sum(Afunc, Lfunc);
        assertInstanceOf(LinkedListTabulatedFunction.class, res);
        for(Point p : res){
            assertEquals(Lfunc.getX(i), p.x());
            assertEquals(Afunc.getY(i) + Lfunc.getY(i), p.y());
            ++i;
        }
        i=0;
        //Array + Array:
        res = service.sum(Afunc, Afunc);
        assertInstanceOf(LinkedListTabulatedFunction.class, res);
        for(Point p : res){
            assertEquals(Afunc.getX(i), p.x());
            assertEquals(Afunc.getY(i) + Afunc.getY(i), p.y());
            ++i;
        }
        i=0;
        //List + List:
        res = service.sum(Lfunc, Lfunc);
        assertInstanceOf(LinkedListTabulatedFunction.class, res);
        for(Point p : res){
            assertEquals(Lfunc.getX(i), p.x());
            assertEquals(Lfunc.getY(i) + Lfunc.getY(i), p.y());
            ++i;
        }
    }

    @Test
    void sub() {
        int i=0;
        TabulatedFunction res;

        ///with Array factory:
        //Array + List
        res = service.sub(Afunc, Lfunc);
        assertInstanceOf(ArrayTabulatedFunction.class, res);
        for(Point p : res){
            assertEquals(Lfunc.getX(i), p.x());
            assertEquals(Afunc.getY(i) - Lfunc.getY(i), p.y());
            ++i;
        }
        i=0;
        //Array + Array:
        res = service.sub(Afunc, Afunc);
        assertInstanceOf(ArrayTabulatedFunction.class, res);
        for(Point p : res){
            assertEquals(Afunc.getX(i), p.x());
            assertEquals(Afunc.getY(i) - Afunc.getY(i), p.y());
            ++i;
        }
        i=0;
        //List + List:
        res = service.sub(Lfunc, Lfunc);
        assertInstanceOf(ArrayTabulatedFunction.class, res);
        for(Point p : res){
            assertEquals(Lfunc.getX(i), p.x());
            assertEquals(Afunc.getY(i) - Afunc.getY(i), p.y());
            ++i;
        }
        i=0;

        ///with List factory:
        //Array + List
        service = new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());
        res = service.sub(Afunc, Lfunc);
        assertInstanceOf(LinkedListTabulatedFunction.class, res);
        for(Point p : res){
            assertEquals(Afunc.getX(i), p.x());
            assertEquals(Afunc.getY(i) - Lfunc.getY(i), p.y());
            ++i;
        }
        i=0;
        //Array + Array:
        res = service.sub(Afunc, Afunc);
        assertInstanceOf(LinkedListTabulatedFunction.class, res);
        for(Point p : res){
            assertEquals(Afunc.getX(i), p.x());
            assertEquals(Afunc.getY(i) - Afunc.getY(i), p.y());
            ++i;
        }
        i=0;
        //List + List:
        res = service.sub(Lfunc, Lfunc);
        assertInstanceOf(LinkedListTabulatedFunction.class, res);
        for(Point p : res){
            assertEquals(Lfunc.getX(i), p.x());
            assertEquals(Lfunc.getY(i) - Lfunc.getY(i), p.y());
            ++i;
        }
    }
}