package operations;

import functions.classes.SqrFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RightSteppingDifferentialOperatorTest {
    static RightSteppingDifferentialOperator oper;

    @Test
    void derive() {
        double delta = 1e-16;
        SqrFunction func = new SqrFunction();
        oper = new RightSteppingDifferentialOperator(1);

        assertEquals(1,oper.derive(func).apply(0),delta);
        assertEquals(7,oper.derive(func).apply(3),delta);

        oper = new RightSteppingDifferentialOperator(2);

        assertEquals(2,oper.derive(func).apply(0),delta);
        assertEquals(8,oper.derive(func).apply(3),delta);

    }
}