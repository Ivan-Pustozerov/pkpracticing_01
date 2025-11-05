package functions.classes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class UnitFunctionTest {
    @Test
    public void test1() {
        UnitFunction test = new UnitFunction();
        assertEquals(1.0, test.apply(10));
        assertEquals(1.0, test.apply(-3));
        assertEquals(1.0, test.apply(0));
    }
    @Test
    public void testGetConst() {
        UnitFunction test = new UnitFunction();
        assertEquals(1.0, test.getConst());
    }

}