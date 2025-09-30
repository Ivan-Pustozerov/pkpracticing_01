package functions.classes;

import LazyTester.LazyTester;
import functions.interfaces.MathFunction;
import functions.interfaces.MathFunction2args;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


class ArrayTabulatedFunctionTest extends LazyTester {
    protected static ArrayList<ArrayTabulatedFunction> funcs=new ArrayList<>();
    protected static LazyHolder h;
    protected static final String TYPE_OUT="DOUBLE";
    protected static final String FILE="/data1-1.csv";
    protected static final double delta=1e-3;
    protected static final double x0=0.0;
    protected static final double y0=0.0;
    protected static final double step=0.00001;

    @Test
    public void constructorTest(){
        double[] xVals ={0,3,2,1.6,-1.8,4.4,7,5,9,6};
        double[] yVals ={1,2,4,-4,23,0,11,-5,59,3.1};
        int index =0;
        funcs.add(new ArrayTabulatedFunction(xVals,yVals));
        ArrayTabulatedFunction func=funcs.get(index++);
        Assertions.assertNotNull(func,"Object not Created!");
        Assertions.assertNotNull(func.getxVals());
        Assertions.assertNotNull(func.getyVals());

        MathFunction f = x->new IdentityFunction().apply(x);
        funcs.add(new ArrayTabulatedFunction(f,-1.0,10.0,11));
        func=funcs.get(index++);
        Assertions.assertNotNull(func,"Object not Created!");
        Assertions.assertNotNull(func.getxVals());
        Assertions.assertNotNull(func.getyVals());

        funcs.add(new ArrayTabulatedFunction(f,0.0,0.0,5));
        func=funcs.get(index++);
        Assertions.assertNotNull(func,"Object not Created!");
        Assertions.assertNotNull(func.getxVals());
        Assertions.assertNotNull(func.getyVals());

        funcs.add(new ArrayTabulatedFunction(f,10,-0.4,5));
        func=funcs.get(index++);
        Assertions.assertNotNull(func,"Object not Created!");
        Assertions.assertNotNull(func.getxVals());
        Assertions.assertNotNull(func.getyVals());

    }
    @Disabled
    void insert() {
        for(ArrayTabulatedFunction func : funcs){
            func.insert(2.0,1.0);
            func.insert(2.0,0.1);
            func.insert("2.1",0.1);
            func.insert(2.1,"0.1");
            func.insert(func.leftBound(),0.3);
            func.insert(func.leftBound()-1.0,0.3);
            /*System.out.println(Arrays.toString(func.getxVals()));
            System.out.println(Arrays.toString(func.getyVals()));
            System.out.println("dsfaas\n");*/
            /*System.out.println(Arrays.toString(func.getxVals()));
            System.out.println(Arrays.toString(func.getyVals()));
            System.out.println("dsfaas\n");*/
        }
    }
    @Test
    void getX() {
        for(ArrayTabulatedFunction func : funcs){
            assertEquals(func.leftBound(),func.getX(0),delta);
            assertEquals(func.rightBound(),func.getX(func.getCount()-1),delta);
            assertEquals(Double.NaN,func.getX(func.getCount()+1),delta);
            assertEquals(Double.NaN,func.getX(-23),delta);
        }
    }

    @Test
    void getY() {
        for(ArrayTabulatedFunction func : funcs){
            assertEquals(func.apply(func.leftBound()),func.getY(0),delta);
            assertEquals(func.apply(func.leftBound()),func.getY(func.getCount()-1),delta);
            assertEquals(Double.NaN,func.getY(func.getCount()+1),delta);
            assertEquals(Double.NaN,func.getY(-23),delta);
        }
    }

    @Test
    void setY() {
        for(ArrayTabulatedFunction func : funcs){
            func.setY(2,2.3);
            assertEquals(func.getY(2),func.getY(2),delta);
            func.setY(2,"q.3");
            assertEquals(func.getY(2),func.getY(2),delta);
            func.setY(-2,2.3);
            assertEquals(func.getY(2),func.getY(2),delta);
        }
    }

    @Test
    void setX() {
        for(ArrayTabulatedFunction func : funcs){
            func.setX(3,-1243);
            assertEquals(-1243,func.getX(3),delta);
            func.setX(3,"-1243");
            assertEquals(-1243,func.getX(3),delta);
            func.setX(-4,-1243);
            double a=func.getX(4);
            assertEquals(a,func.getX(4),delta);
        }
    }
    @Disabled
    @Test
    void indexOfX() {
        for(ArrayTabulatedFunction func : funcs){
            assertEquals(0,func.indexOfX(func.leftBound()));
            assertEquals(func.getCount()-1,func.indexOfX(func.rightBound()));
            assertEquals(-1,func.indexOfX("-10000000.0"));
        }
    }
    @Disabled
    @Test
    void indexOfY() {
        for(ArrayTabulatedFunction func : funcs){
            assertEquals(0,func.indexOfY(func.apply(func.leftBound())));
            assertEquals(func.getCount()-1,func.indexOfY(func.apply(func.rightBound())));
            assertEquals(-1,func.indexOfY("-10000000.0"));
        }
    }
    @Disabled
    @Test
    void floorIndexOfX() {
    }
}