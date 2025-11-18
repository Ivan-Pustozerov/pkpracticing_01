package operations;

import functions.classes.ArrayTabulatedFunction;
import functions.classes.LinkedListTabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedDifferentialOperatorTest {
    protected static final double delta=1e-3;
    ///------------------------------------------------------------------------------------------
    @Test
    void test_Linkedlist_DeriveWithLinearFunction(){
        var oper = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());
        var func = new LinkedListTabulatedFunction(new double[]{0.0, 1.0, 2.0, 3.0, 4.0}, new double[]{3.0, 5.0, 7.0, 9.0, 11.0});
        //шаг = 2
        var derivative = oper.derive(func);

        assertInstanceOf(LinkedListTabulatedFunction.class, derivative);
        assertEquals(2.0, derivative.getY(0), delta);
        assertEquals(2.0, derivative.getY(1), delta);
        assertEquals(2.0, derivative.getY(2), delta);
        assertEquals(2.0, derivative.getY(3), delta);
        assertEquals(2.0, derivative.getY(4), delta);

    }
    @Test
    void test_Array_DeriveWithLinearFunction(){
        var oper = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());
        var func = new ArrayTabulatedFunction(new double[]{0.0, 1.0, 2.0, 3.0, 4.0}, new double[]{3.0, 5.0, 7.0, 9.0, 11.0});
        //шаг = 2
        var derivative = oper.derive(func);

        assertInstanceOf(ArrayTabulatedFunction.class, derivative);
        assertEquals(2.0, derivative.getY(0), delta);
        assertEquals(2.0, derivative.getY(1), delta);
        assertEquals(2.0, derivative.getY(2), delta);
        assertEquals(2.0, derivative.getY(3), delta);
        assertEquals(2.0, derivative.getY(4), delta);

    }
    ///------------------------------------------------------------------------------------------
    @Test
    void test_2POINTS(){
        var oper = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());
        var func = new ArrayTabulatedFunction(new double[]{0.0, 1.0}, new double[]{3.0, 5.0});
        //шаг = 2
        var derivative = oper.derive(func);
        assertEquals(2.0, derivative.getY(0), delta);
        assertEquals(2.0, derivative.getY(1), delta);
    }
    @Test
    void test_Quadratic(){
        var oper = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());
        var func = new ArrayTabulatedFunction(new double[]{0.0, 1.0, 2.0, 3.0, 4.0}, new double[]{0.0, 1.0, 4.0, 9.0, 16.0});
        //шаг = 2
        var derivative = oper.derive(func);
        assertEquals(1.0, derivative.getY(0), 1e-10);
        assertEquals(3.0, derivative.getY(1), 1e-10);
        assertEquals(5.0, derivative.getY(2), 1e-10);
        assertEquals(7.0, derivative.getY(3), 1e-10);
        assertEquals(7.0, derivative.getY(4), 1e-10);
    }
    ///------------------------------------------------------------------------------------------
    @Test
    void test_GET_Factorio(){
        var factory = new ArrayTabulatedFunctionFactory();
        var operator = new TabulatedDifferentialOperator(factory);
        TabulatedFunctionFactory G_F = operator.getFactory();
        assertInstanceOf(ArrayTabulatedFunctionFactory.class, G_F);
    }
    //3 зайца одним выстрелом:
    @Test
    void test_SET_Factorio(){
        var operator = new TabulatedDifferentialOperator();
        assertInstanceOf(ArrayTabulatedFunctionFactory.class, operator.getFactory());

        operator.setFactory(new LinkedListTabulatedFunctionFactory());
        assertInstanceOf(LinkedListTabulatedFunctionFactory.class, operator.getFactory());
    }
    ///------------------------------------------------------------------------------------------
    @Test
    void testDeriveSynchronously() {
        var operator = new TabulatedDifferentialOperator();
        var function = new ArrayTabulatedFunction(new double[]{0.0, 1.0, 2.0, 3.0, 4.0}, new double[]{0.0, 1.0, 4.0, 9.0, 16.0});

        var derivative = operator.deriveSynchronously(function);

        assertEquals(1.0, derivative.getY(0), 1e-10);
        assertEquals(3.0, derivative.getY(1), 1e-10);
        assertEquals(5.0, derivative.getY(2), 1e-10);
        assertEquals(7.0, derivative.getY(3), 1e-10);
        assertEquals(7.0, derivative.getY(4), 1e-10);
    }
}