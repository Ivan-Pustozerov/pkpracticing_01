package functions.classes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantFunctionTest {
    @Test
    public void testApplyReturnsConstant() {
        ConstantFunction f = new ConstantFunction(5);
        assertEquals(5, f.apply(10));
        assertEquals(5, f.apply(-3));
        assertEquals(5, f.apply(0));
    }
    @Test
    public void testGetConstReturnsConstant() {
        ConstantFunction f = new ConstantFunction(7.2);
        assertEquals(7.2, f.getConst());
    }

}