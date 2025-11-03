package functions.classes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqrFunctionTest {
    @Test
    public void testapply(){
        SqrFunction test = new SqrFunction();

        assertEquals(4.0, test.apply(2));
        assertEquals(9.0, test.apply(3));
        assertEquals(0.0, test.apply(0));
    }
}