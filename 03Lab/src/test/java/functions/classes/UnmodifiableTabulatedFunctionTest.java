package functions.classes;

import functions.interfaces.TabulatedFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnmodifiableTabulatedFunctionTest {
    private final static double[] xVals = {1, 2, 3, 4, 5};
    private final static double[] yVals = {10, 20, 30, 40, 50};
    private static TabulatedFunction array ;
    private static TabulatedFunction list;
    private UnmodifiableTabulatedFunction Afunc;
    private UnmodifiableTabulatedFunction Lfunc;

    @BeforeEach
    void setUp() {
        array = new ArrayTabulatedFunction(xVals,yVals);
        list = new LinkedListTabulatedFunction(xVals,yVals);
        Afunc = new UnmodifiableTabulatedFunction(array);
        Lfunc = new UnmodifiableTabulatedFunction(list);
    }

    @Test
    void getCount() {
        assertEquals(array.getCount(),Afunc.getCount());
        assertEquals(list.getCount(),Lfunc.getCount());

    }

    @Test
    void getX() {
        for(int i =0; i < xVals.length; ++i) {
            assertEquals(array.getX(i), Afunc.getX(i));
            assertEquals(list.getX(i), Lfunc.getX(i));
        }
    }

    @Test
    void getY() {
        for(int i =0; i < yVals.length; ++i) {
            assertEquals(array.getY(i), Afunc.getY(i));
            assertEquals(list.getY(i), Lfunc.getY(i));
        }
    }

    @Test
    void setY() {
        assertThrows(UnsupportedOperationException.class,() -> Afunc.setY(2,1.2));
        assertThrows(UnsupportedOperationException.class,() -> Lfunc.setY(2,1.2));
    }

    @Test
    void setX() {
        assertThrows(UnsupportedOperationException.class,() -> Afunc.setX(2,1.2));
        assertThrows(UnsupportedOperationException.class,() -> Lfunc.setX(2,1.2));
    }

    @Test
    void indexOfX() {
        assertEquals(array.indexOfX(xVals[0]), Afunc.indexOfX(xVals[0]));
        assertEquals(list.indexOfX(xVals[0]), Lfunc.indexOfX(xVals[0]));
    }


    @Test
    void indexOfY() {
        assertEquals(array.indexOfY(yVals[0]), Afunc.indexOfY(yVals[0]));
        assertEquals(list.indexOfY(yVals[0]), Lfunc.indexOfY(yVals[0]));
    }

    @Test
    void leftBound() {
        assertEquals(array.leftBound(), Afunc.leftBound());
        assertEquals(list.leftBound(), Lfunc.leftBound());
    }

    @Test
    void rightBound() {
        assertEquals(array.rightBound(), Afunc.rightBound());
        assertEquals(list.rightBound(), Lfunc.rightBound());
    }
    /*
    @Test
    void iterator() {
        assertEquals(array.iterator(), Afunc.iterator());
        assertEquals(list.iterator(), Lfunc.iterator());
    }
    */
    //НЕЛЬЗЯ ТАК! БУДЕТ ОШИБКА. ОНИ БУДУТ РАЗНЫЕ(ВРОДЕ)
    //понял как
    @Test
    void test_Iterator_BTW(){
        var arrayFunc = new ArrayTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});
        var unmod = new UnmodifiableTabulatedFunction(arrayFunc);

        int pointer = 1;
        for(Point point: unmod){
            assertEquals(point.x, pointer);
            assertEquals(point.y, pointer * 10);
            pointer++;
        }
    }
    @Test
    void apply() {
        assertEquals(array.apply(xVals[0]), Afunc.apply(xVals[0]));
        assertEquals(list.apply(xVals[0]), Lfunc.apply(xVals[0]));
    }

    @Test
    void getRawFunction() {
        assertEquals(array, Afunc.getRawFunction());
        assertEquals(list, Lfunc.getRawFunction());
    }
}