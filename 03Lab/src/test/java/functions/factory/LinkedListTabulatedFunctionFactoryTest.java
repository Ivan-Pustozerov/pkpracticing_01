package functions.factory;

import functions.classes.StrictTabulatedFunction;
import functions.classes.UnmodifiableTabulatedFunction;
import functions.interfaces.TabulatedFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionFactoryTest {
    private LinkedListTabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
    private final static double[] xVals={1,2};
    private final static double[] yVals={1,2};

    @Test
    void create() {
        assertInstanceOf(TabulatedFunction.class, factory.create(xVals, yVals));
    }

    @Test
    void createUnmodifiableTest(){
        assertInstanceOf(UnmodifiableTabulatedFunction.class, factory.createUnmodifiable(xVals, yVals));
    }
    @Test
    void createStrictTest(){
        assertInstanceOf(StrictTabulatedFunction.class, factory.createStrict(xVals, yVals));
    }
    @Test
    void createUnmodifiableStrict(){
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        TabulatedFunction function = factory.createUnmodifiableStrict(xValues, yValues);

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));
        assertEquals(10.0, function.getY(0));
        assertEquals(20.0, function.getY(1));
        assertEquals(30.0, function.getY(2));

        assertThrows(UnsupportedOperationException.class, () -> function.apply(1.5));
        assertThrows(UnsupportedOperationException.class, () -> function.setY(0, 15.0));
    }
}