package functions.classes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZeroFunctionTest {
    @Test
    public void testAZero() {
        ZeroFunction test = new ZeroFunction();
        assertEquals(0, test.apply(10));
        assertEquals(0, test.apply(-3));
        assertEquals(0, test.apply(0));
    }
    @Test
    public void testGetConst() {
        ZeroFunction test = new ZeroFunction();
        assertEquals(0, test.getConst());
    }

}