package functions.abstracts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @BeforeEach
    void setup(){
        //НЕЛЬЗЯ ДЕЛАТЬ STATIC!!! - MOCK ДЛЯ ЭТОГО НЕ ПРЕДНАЗНАЧЕН!!!

        when(func.getX(0)).thenReturn(x1);
        when(func.getY(0)).thenReturn(y1);
        when(func.indexOfX(x1)).thenReturn(0);
        when(func.indexOfY(y1)).thenReturn(0);
        when(func.floorIndexOfX(x1)).thenReturn(0);

        doReturn(1).when(func).setY(0,y2);
        doReturn(1).when(func).setX(0,x2);
        /// ///
        when(func.getX(1)).thenReturn(x2);
        when(func.getY(1)).thenReturn(y2);
        when(func.indexOfX(x2)).thenReturn(1);
        when(func.indexOfY(y2)).thenReturn(1);
        when(func.floorIndexOfX(x2)).thenReturn(1);

        doReturn(1).when(func).setY(1,y1);
        doReturn(1).when(func).setX(1,x1);

        when(func.extrapolateLeft(x1)).thenReturn(func.interpolate(x1,0));
        when(func.extrapolateRight(x1)).thenReturn(func.interpolate(x1,1));
        when(func.leftBound()).thenReturn(x1);
        when(func.rightBound()).thenReturn(x2);
        when(func.getCount()).thenReturn(2);

        doReturn(1).when(func.interpolate(0,0));
        doReturn(1).when(func).sort();
    }

    @Test
    void testGetXandYValidIndexes() {
        assertEquals(x1,func.getX(0),delta);
        assertEquals(x2,func.getX(1),delta);
        assertEquals(y1,func.getY(0),delta);
        assertEquals(y2,func.getY(1),delta);
    }

    @Test
    void testIndexOfXOfY() {
        assertEquals(0,func.indexOfX(x1),delta);
        assertEquals(1,func.indexOfX(x2),delta);
        assertEquals(0,func.indexOfY(y1),delta);
        assertEquals(1,func.indexOfY(y2),delta);
    }
    @Test
    void testBounds() {
        assertEquals(x1,func.leftBound(),delta);
        assertEquals(x2,func.rightBound(),delta);
    }

    @Test
    void testInterpolation() {
        assertEquals(1,func.extrapolateLeft(x1),delta);
        assertEquals(1,func.extrapolateRight(x1),delta);
    }

    @Test
    void testDoReturns() {
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
}