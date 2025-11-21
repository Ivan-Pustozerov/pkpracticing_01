package operations;

import functions.classes.SqrFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MiddleSteppingDifferentialOperatorTest {
    static MiddleSteppingDifferentialOperator oper;

    @Test
    void derive() {
        double delta = 1e-16;
        SqrFunction func = new SqrFunction();
        oper = new MiddleSteppingDifferentialOperator(1);

        assertEquals(0,oper.derive(func).apply(0),delta);
        assertEquals(6,oper.derive(func).apply(3),delta);

        oper = new MiddleSteppingDifferentialOperator(2);

        assertEquals(0,oper.derive(func).apply(0),delta);
        assertEquals(6,oper.derive(func).apply(3),delta);

    }
}