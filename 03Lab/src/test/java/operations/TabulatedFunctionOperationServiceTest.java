package operations;

import functions.classes.ArrayTabulatedFunction;
import functions.classes.LinkedListTabulatedFunction;
import functions.classes.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionOperationServiceTest {

    private static final double[] xs = {1, 2, 3, 4, 5};
    private static final double[] ys = {10, 20, 30, 40, 50};
    private Point[] pnts;
    private ArrayTabulatedFunction Afunc;
    private LinkedListTabulatedFunction Lfunc;

    @BeforeEach
    void setUp(){
        Afunc = new ArrayTabulatedFunction(xs,ys);
        Lfunc = new LinkedListTabulatedFunction(xs,ys);
        pnts = new Point[xs.length];
        for(int i=0; i<xs.length; ++i){
            pnts[i] = new Point(xs[i], ys[i]);
        }
    }

    @Test
    void asPoints() {
        for(int i=0; i<pnts.length; ++i){
            assertEquals(pnts[i], TabulatedFunctionOperationService.asPoints(Afunc)[i]);
            assertEquals(pnts[i], TabulatedFunctionOperationService.asPoints(Lfunc)[i]);
        }
    }
}