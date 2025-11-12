package functions.abstracts;

import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import functions.classes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractTabulatedFunctionTest {
    static double x1 =0;
    static double x2 =1;
    static double y1 =2;
    static double y2 =0;
    static double delta = 1e-32;
    @Mock
    AbstractTabulatedFunction func;

    @Test
    void testGetXandYValidIndexes() {
        when(func.getX(0)).thenReturn(x1);
        when(func.getY(0)).thenReturn(y1);
        when(func.getX(1)).thenReturn(x2);
        when(func.getY(1)).thenReturn(y2);

        assertEquals(x1,func.getX(0),delta);
        assertEquals(x2,func.getX(1),delta);
        assertEquals(y1,func.getY(0),delta);
        assertEquals(y2,func.getY(1),delta);
    }

    @Test
    void testIndexOfXOfY() {
        when(func.indexOfX(x1)).thenReturn(0);
        when(func.indexOfY(y1)).thenReturn(0);
        when(func.indexOfX(x2)).thenReturn(1);
        when(func.indexOfY(y2)).thenReturn(1);

        assertEquals(0,func.indexOfX(x1),delta);
        assertEquals(1,func.indexOfX(x2),delta);
        assertEquals(0,func.indexOfY(y1),delta);
        assertEquals(1,func.indexOfY(y2),delta);
    }
    @Test
    void testBounds() {
        when(func.leftBound()).thenReturn(x1);
        when(func.rightBound()).thenReturn(x2);

        assertEquals(x1,func.leftBound(),delta);
        assertEquals(x2,func.rightBound(),delta);
    }

    @Test
    void testInterpolation() {
        when(func.extrapolateLeft(x1)).thenReturn(1.0);
        when(func.extrapolateRight(x1)).thenReturn(1.0);

        assertEquals(1,func.extrapolateLeft(x1),delta);
        assertEquals(1,func.extrapolateRight(x1),delta);
    }

    @Test
    void testDoReturns() {
        doNothing().when(func).sort();
        when(func.interpolate(0,0)).thenReturn(1.0);
        doNothing().when(func).setX(1,x1);
        doNothing().when(func).setY(1,y1);
        doNothing().when(func).setX(0,x2);
        doNothing().when(func).setY(0,y2);



        func.sort();
        func.interpolate(0,0);
        func.setX(1,x1);
        func.setY(1,y1);
        func.setX(0,x2);
        func.setY(0,y2);

        verify(func).sort();
        verify(func).interpolate(0,0);
        verify(func).setX(1,x1);
        verify(func).setY(1,y1);
        verify(func).setX(0,x2);
        verify(func).setY(0,y2);
    }
    @Test
    void testToString() {
        var function = new ArrayTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});
        var result = function.toString();

        assertEquals("ArrayTabulatedFunction size = 3\n[1.0; 10.0]\n[2.0; 20.0]\n[3.0; 30.0]", result);
    }

    @Disabled
    void apply() {
    }

    @Test
    void checkLengthIsTheSame() {
        assertThrows(DifferentLengthOfArraysException.class,() -> new ArrayTabulatedFunction(new double[]{1,-10,3},new double[]{1,3}));
    }

    @Test
    void checkSorted() {
        assertThrows(ArrayIsNotSortedException.class,() -> new ArrayTabulatedFunction(new double[]{1,-10,3},new double[]{1,2,3}));
    }

    @Test
    void getCount() {
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(new double[]{1,2,3},new double[]{1,2,3});
        assertEquals(3,func.getCount());
    }

    @Test
    void testEquals() {
        assertEquals(1,1);
    }
}