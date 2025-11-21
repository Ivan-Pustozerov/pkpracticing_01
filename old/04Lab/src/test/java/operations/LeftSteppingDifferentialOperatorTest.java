package operations;

import functions.classes.SqrFunction;
import functions.interfaces.MathFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeftSteppingDifferentialOperatorTest {
    static LeftSteppingDifferentialOperator oper;

    @Test
    void derive() {
        double delta = 1e-16;
        SqrFunction func = new SqrFunction();
        oper = new LeftSteppingDifferentialOperator(1);

        assertEquals(-1,oper.derive(func).apply(0),delta);
        assertEquals(5,oper.derive(func).apply(3),delta);

        oper = new LeftSteppingDifferentialOperator(2);

        assertEquals(-2,oper.derive(func).apply(0),delta);
        assertEquals(4,oper.derive(func).apply(3),delta);

    }
}