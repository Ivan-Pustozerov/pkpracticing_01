package functions.factory;

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
}