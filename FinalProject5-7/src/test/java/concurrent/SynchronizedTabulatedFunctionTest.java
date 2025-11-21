package concurrent;

import functions.classes.ArrayTabulatedFunction;
import functions.classes.Point;
import functions.interfaces.TabulatedFunction;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SynchronizedTabulatedFunctionTest {
    private static TabulatedFunction func = new ArrayTabulatedFunction(new double[]{1,2,3},new double[]{1,2,3});
    private static SynchronizedTabulatedFunction Sfunc = new SynchronizedTabulatedFunction(func);

    @Test
    void getCount() {
        assertEquals(func.getCount(),Sfunc.getCount());
    }

    @Test
    void getX() {
        for(int i = 0; i< func.getCount();++i){
            assertEquals(func.getX(i),Sfunc.getX(i));
        }
    }

    @Test
    void getY() {
        for(int i = 0; i< func.getCount();++i){
            assertEquals(func.getY(i),Sfunc.getY(i));
        }
    }

    @Test
    void setY() {
        func.setX(2,2);
        Sfunc.setX(2,2);
        assertEquals(func.getY(2),Sfunc.getY(2));
    }

    @Test
    void setX() {
        func.setY(2,2);
        Sfunc.setY(2,2);
        assertEquals(func.getX(2),Sfunc.getX(2));
    }

    @Test
    void indexOfX() {
        assertEquals(func.indexOfX(1),Sfunc.indexOfX(1));
    }

    @Test
    void indexOfY() {
        assertEquals(func.indexOfY(1),Sfunc.indexOfY(1));
    }

    @Test
    void leftBound() {
        assertEquals(func.leftBound(),Sfunc.leftBound());
    }

    @Test
    void rightBound() {
        assertEquals(func.rightBound(),Sfunc.rightBound());
    }

    @Test
    void apply() {
        assertEquals(func.apply(2),Sfunc.apply(2));
    }

    @Test
    void iterator() {
        Iterator<Point> fi = func.iterator();
        Iterator<Point> sfi = Sfunc.iterator();
        while(sfi.hasNext()){
            assertEquals(fi.next(),sfi.next());
        }
        assertThrows(NoSuchElementException.class,() -> sfi.next());
    }

    @Test
    void doSynchronously() {
        Sfunc.doSynchronously(new SynchronizedTabulatedFunction.Operation<Void>(){
            @Override
            public Void apply(SynchronizedTabulatedFunction Func){
                for(int i =0; i< Func.getCount();++i){
                    Func.setY(i,Func.getY(i)+1);
                }
                return null;
            }
        });
        for(int i =0; i<Sfunc.getCount();++i){
            assertEquals(i+2,Sfunc.getY(i));//func изменится тоже! т.к является опубликованным в тесте внутренним объектом
        }

    }
}