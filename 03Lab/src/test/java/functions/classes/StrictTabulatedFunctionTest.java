package functions.classes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StrictTabulatedFunctionTest {

    @Test
    void test_Strict_Array(){
        var array = new ArrayTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});
        var str_ar = new StrictTabulatedFunction(array);

        assertEquals(3, str_ar.getCount());
        assertEquals(1.0, str_ar.getX(0));
        assertEquals(10.0, str_ar.getY(0));
        assertEquals(1.0, str_ar.leftBound());
        assertEquals(3.0, str_ar.rightBound());
        assertEquals(0, str_ar.indexOfX(1.0));
        assertEquals(-1, str_ar.indexOfX(5.0));
        assertEquals(0, str_ar.indexOfY(10.0));
        assertEquals(-1, str_ar.indexOfY(50.0));
        assertEquals(10, str_ar.apply(1));

        str_ar.setY(1, 25.0);
        assertEquals(25.0, str_ar.getY(1));
        assertEquals(25.0, str_ar.getY(1));
        str_ar.setX(1,2.0);
        assertEquals(1, str_ar.indexOfX(2.0));
    }
    @Test
    void test_Strict_List(){
        var list = new LinkedListTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});
        var str_l = new StrictTabulatedFunction(list);

        assertEquals(3, str_l.getCount());
        assertEquals(1.0, str_l.getX(0));
        assertEquals(10.0, str_l.getY(0));
        assertEquals(1.0, str_l.leftBound());
        assertEquals(3.0, str_l.rightBound());
        assertEquals(0, str_l.indexOfX(1.0));
        assertEquals(-1, str_l.indexOfX(5.0));
        assertEquals(0, str_l.indexOfY(10.0));
        assertEquals(-1, str_l.indexOfY(50.0));
        assertEquals(10, str_l.apply(1));

        str_l.setY(1, 25.0);
        assertEquals(25.0, str_l.getY(1));
        assertEquals(25.0, str_l.getY(1));
        str_l.setX(1,2.0);
        assertEquals(1, str_l.indexOfX(2.0));
    }
    @Test
    void test_Strict_to_Unmod(){
        var arrayFunc = new ArrayTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});
        var str_l = new StrictTabulatedFunction(arrayFunc);
        var unmod = new UnmodifiableTabulatedFunction(str_l);


        assertEquals(3, unmod.getCount());
        assertEquals(1.0, unmod.getX(0));
        assertEquals(10.0, unmod.getY(0));

        assertThrows(UnsupportedOperationException.class, () -> unmod.setY(0, 15.0));
        assertThrows(UnsupportedOperationException.class, () -> unmod.apply(1.5));

    }
    @Test
    void test_Unmod_to_Strict(){
        var arrayFunc = new ArrayTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});
        var unmod = new UnmodifiableTabulatedFunction(arrayFunc);
        var str_l = new UnmodifiableTabulatedFunction(unmod);


        assertEquals(3, str_l.getCount());
        assertEquals(1.0, str_l.getX(0));
        assertEquals(10.0, str_l.getY(0));

        assertThrows(UnsupportedOperationException.class, () -> str_l.setY(0, 15.0));
    }
    @Test
    void test_Iterator_BTW(){
        var list = new LinkedListTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});
        var str_l = new StrictTabulatedFunction(list);

        int pointer = 1;
        for(Point point: str_l){
            assertEquals(point.x, pointer);
            assertEquals(point.y, pointer * 10);
            pointer++;
        }
    }
}