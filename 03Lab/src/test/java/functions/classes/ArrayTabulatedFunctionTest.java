package functions.classes;

import exceptions.DifferentLengthOfArraysException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTabulatedFunctionTest {

    private double[] xs = {1, 2, 3, 4, 5};
    private double[] ys = {10, 20, 30, 40, 50};
    private double delta =1e-32;
    private ArrayTabulatedFunction func;

    @BeforeEach
    void setup() {
        func = new ArrayTabulatedFunction(xs, ys);
    }

    @Test
    void testConstructor() {
        func = new ArrayTabulatedFunction(xs, ys);
        for (int i = 1; i < xs.length; i++) {
            assertEquals(xs[i],func.getxVals()[i],delta);
            assertEquals(ys[i],func.getyVals()[i],delta);
        }
    }

    @Test
    void testConstructorWithMismatchedArrays() {
        double[] xsBad = {1, 2, 3};
        double[] ysBad = {10, 20};
        assertThrows(DifferentLengthOfArraysException.class, () -> new ArrayTabulatedFunction(xsBad, ysBad));
    }

    @Test
    void testGetXandYValidIndexes() {
        for (int i = 0; i < xs.length; i++) {
            assertEquals(xs[i], func.getX(i),delta);
            assertEquals(ys[i], func.getY(i),delta);
        }
    }

    @Test
    void testGetXandYInvalidIndexes() {
        assertThrows(ArrayIndexOutOfBoundsException.class,() ->func.getX(-1));
        assertThrows(ArrayIndexOutOfBoundsException.class,() ->func.getY(-1));
    }

    @Test
    void testSetXValid() {
        func.setX(1, 8.0);
        assertEquals(8.0, func.rightBound(),delta);
        //checks if sorted:
        double[] sorted = func.getxVals();
        for (int i = 1; i < sorted.length; i++) {
            assertTrue(sorted[i] - sorted[i-1]>delta);
        }
    }

    @Test
    void testSetXInvalid() {
        //assertThrows(IllegalArgumentException.class, () -> func.setX(1, "string"));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> func.setX(-1, 1.0));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> func.setX(xs.length, 1.0));
    }

    @Test
    void testSetYValid() {
        func.setY(2, 100.0);
        assertEquals(100.0, func.getY(2),delta);
    }

    @Test
    void testSetYInvalid() {
        //assertThrows(IllegalArgumentException.class, () -> func.setY(2, "string"));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> func.setY(-1, 10.0));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> func.setY(ys.length, 10.0));
    }

    @Test
    void testIndexOfXWithValidAndInvalid() {
        assertEquals(0, func.indexOfX(1.0));
        //assertEquals(-1, func.indexOfX("string"));
        assertEquals(xs.length - 1, func.indexOfX(5.0));
    }
    @Test
    void testIndexOfYWithValidAndInvalid() {
        assertEquals(0, func.indexOfY(10));
        assertEquals(-1, func.indexOfY(5000));
        //assertEquals(-1, func.indexOfY("string"));
        assertEquals(xs.length - 1, func.indexOfY(50));
    }

    @Test
    void testSortMaintainsOrdering() {
        func.setX(0, 10.0);
        double[] vals = func.getxVals();
        for (int i = 1; i < vals.length; i++) {
            assertTrue(vals[i] >= vals[i - 1]);
        }
    }
    @Test
    void testInterpolationWithinRange() {
        double xMid = 2.5;
        double expectedY = 25.0;
        double resultY = func.apply(xMid);
        assertEquals(expectedY, resultY,delta);
    }

    @Test
    void testfloorIndexOfXValidAndInvalid() {
        //assertEquals(-1, func.floorIndexOfX("string"));
        assertEquals(func.getCount(), func.floorIndexOfX(10000.0));
        assertEquals(func.getCount(), func.floorIndexOfX(10000.0));
    }
    @Test
    void testInterpolationAtBounds() {
        assertEquals(ys[0], func.apply(xs[0]),delta);
        assertEquals(ys[ys.length - 1], func.apply(xs[xs.length - 1]),delta);
    }

    /*
    @Test
    void testConstructorFromFunction() {
        MathFunction mf = x -> 2 * x.doubleValue() + 1;
        ArrayTabulatedFunction genFunc = new ArrayTabulatedFunction(mf, 0.0, 5.0, 6);
        assertEquals(6, genFunc.getCount());
        for (int i = 0; i < genFunc.getCount(); i++) {
            double x = genFunc.getX(i);
            double y = genFunc.getY(i);
            assertEquals(mf.apply(x), y,delta);
        }
        assertThrows(IllegalArgumentException.class,() ->new ArrayTabulatedFunction(mf, 0.0, 5.0, 0));
        genFunc = new ArrayTabulatedFunction(mf, 0.0, 0.0, 6);
        genFunc = new ArrayTabulatedFunction(mf, 5.0, 0.0, 6);
    }
    /*
    @Test
    void testConstructorWithMathFunction() {
        // Тестирование конструктора с математической функцией
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(new IdentityFunction(), 1, 5, 4);

        assertEquals(4, func.getCount());
        assertEquals(1, func.leftBound());
        assertEquals(5, func.rightBound());
        assertEquals(1, func.getX(0));
        assertEquals(1, func.getY(0));

    }
*/
    @Test
    void testRemoveCorrect() {
        func.remove(2);
        double[] xvalsnew= {1, 2, 4, 5};
        double[] yvalsnew = {10, 20, 40, 50};
        for(int i=0;i<func.getCount();++i){
            assertEquals(xvalsnew[i],func.getX(i));
            assertEquals(yvalsnew[i],func.getY(i));
        }
    }
    @Test
    void testRemoveIncorrect() {
        assertThrows(IndexOutOfBoundsException.class,() ->func.remove(-1));//создание и вызов анонимной лямба -функции
        //которая вернет тип Executable
        assertThrows(IndexOutOfBoundsException.class,() ->func.remove(func.getCount()+1));
    }
    //private double[] xs = {1, 2, 3, 4, 5};            <---- просто напоминаение о примере
    //private double[] ys = {10, 20, 30, 40, 50};
    @Test
    void testInc() {
        func.insert(2,5);
        double[] nxs = {1, 2, 3, 4, 5};                     //заменим значение под икс=2 на y=5(вместо 20)
        double[] nys = {10, 5, 30, 40, 50};
        for(int i=0;i<func.getCount();++i){
            assertEquals(nxs[i],func.getX(i));
            assertEquals(nys[i],func.getY(i));
        }
    }
    @Test
    void testInc2() {
        func.insert(13,42);
        double[] nxs = {1, 2, 3, 4, 5, 13};                     //попробуем вставить элемент с x=13 и y=42
        double[] nys = {10, 20, 30, 40, 50, 42};
        for(int i=0;i<func.getCount();++i){
            assertEquals(nxs[i],func.getX(i));
            assertEquals(nys[i],func.getY(i));
        }
    }
    @Test
    void testEdgeCasesEmptyAndSingleElement() {
        double[] singleX = {1.0};
        double[] singleY = {10.0};
        ArrayTabulatedFunction singleFunc = new ArrayTabulatedFunction(singleX, singleY);
        assertEquals(10.0, singleFunc.apply(1.0),delta);
        //assertEquals(10.0, singleFunc.apply(0.0),delta);
        //assertEquals(10.0, singleFunc.apply(2.0),delta);
    }
}