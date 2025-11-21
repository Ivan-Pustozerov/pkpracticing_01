package functions.interfaces;

import functions.classes.ConstantFunction;
import functions.classes.IdentityFunction;
import functions.classes.SqrFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class MathFunctionTest {
    protected static LinkedList<MathFunction> funcs= new LinkedList<>();
    protected static double d=1e-16;
    {
        funcs.add(new SqrFunction());//0
        funcs.add(new IdentityFunction());//1
        funcs.add(new ConstantFunction(5.0));//2
        funcs.add(new MathFunction() {
            @Override
            public <T extends Number> double apply(T x) {
                return x.doubleValue();
            }
        }); //ln(x); 3
    }
    @Test
    void andThen() {
        //на некоммутативность:
        assertNotEquals(funcs.get(3).andThen(funcs.get(0)),funcs.get(0).andThen(funcs.get(3)));
        assertEquals(16.0,funcs.get(0).andThen(funcs.get(0)).apply(2.0),d);
        assertEquals(16.0,funcs.get(0).andThen(funcs.get(0)).apply(2.0),d);
        assertEquals(16.0,funcs.get(0).andThen(funcs.get(1)).andThen(funcs.get(0)).apply(2.0),d);
    }
}