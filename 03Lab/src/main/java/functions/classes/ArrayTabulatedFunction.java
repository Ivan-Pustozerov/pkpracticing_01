package functions.classes;
import functions.abstracts.AbstractTabulatedFunction;
import functions.interfaces.Insertable;
import functions.interfaces.MathFunction;
import functions.interfaces.Removable;

import exceptions.InterpolationException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import static java.lang.Math.abs;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable{

    private double[] xVals;
    private double[] yVals;

    public ArrayTabulatedFunction(double[] xVals, double[] yVals){
        //+ здесь проверка на длину таблы >2
        checkLengthIsTheSame(xVals, yVals);
        checkSorted(xVals);

        this.count=xVals.length;
        this.xVals=new double[count];
        this.yVals=new double[count];
        this.xVals=Arrays.copyOf(xVals,this.count);
        this.yVals=Arrays.copyOf(yVals,this.count);
    }

    public ArrayTabulatedFunction(MathFunction source,double xFrom,double xTo,int count){
        if(count<=2){throw new IllegalArgumentException("Length of tabulated function must be at least 2");}

        this.xVals=new double[count];
        this.yVals=new double[count];
        double x;
        double y;

        if(abs(xFrom-xTo)<delta) {
            x=xFrom;
            y=source.apply(x);
            for (int i = 0; i < count; ++i) {
                this.xVals[i] = x;
                this.yVals[i] = y;
            }
        }
        else{
            double step;
            if(xTo<xFrom){
                x=xFrom;
                xFrom=xTo;
                xTo=x;
            }

            step=(xTo-xFrom)/count;
            x=xFrom;

            for(int i=0;i<count-1;++i){
                this.xVals[i] = x;
                this.yVals[i] = source.apply(x);
                x+=step;
            }
            this.xVals[count-1] = xTo;
            this.yVals[count-1] = source.apply(xTo);
        }
        this.count=count;
    }

    public double[] getxVals() {
        return Arrays.copyOf(xVals, count);
    }
    public double[] getyVals() {
        return Arrays.copyOf(yVals, count);
    }

    @Override public double getX(int index) {
        if (index < 0 || index >= count) {
            throw new ArrayIndexOutOfBoundsException("INDEX OUT ARRAY");
        }
        return xVals[index];
    }
    @Override public double getY(int index) {
        if (index < 0 || index >= count) {
            throw new ArrayIndexOutOfBoundsException("INDEX OUT ARRAY");
        }
        return yVals[index];

    }
    @Override
    public <T extends Number> void setY(int index, T Y) {
        if (index < 0 || index >= count) {
            throw new ArrayIndexOutOfBoundsException("INDEX OUT ARRAY");
        }
        yVals[index] = Y.doubleValue();
    }
    @Override
    public <T extends Number> void setX(int index, T X) {
        xVals[index] = X.doubleValue();
        this.sort();//заменить на insert new+ remove old -> faster sollution
    }
    @Override
    public <T extends Number> int indexOfX(T X) {
        double x = X.doubleValue();
        int i = 0;
        while (i<count && abs(xVals[i]-x)>delta){++i;}
        return (i<count)? i: -1;
    }
    @Override
    public <T extends Number> int indexOfY(T Y) {
        double y = Y.doubleValue();
        int i = 0;
        while (i<count && abs(yVals[i]-y)>delta){++i;}
        return (i<count)? i: -1;
    }

    @Override public double leftBound() {
        return xVals[0];
    }
    @Override public double rightBound() {
        return xVals[count-1];
    }

    @Override
    public <T extends Number> void insert(T X, T Y){
        double x = X.doubleValue();
        double y = Y.doubleValue();
        int count = xVals.length;
                                                    //1 случай
        for(int i=0;i<count;++i){
            if(xVals[i]==x){yVals[i]=y;return;}
        }
                                                    //2 случай
        int pos=0;
        while(pos<count && xVals[pos]<x)pos++;
        double[] NxVals = new double[count + 1];
        double[] NyVals = new double[count + 1];
        //(откуда,начало,куда,куданачало,до чего)   - насколько разобрался
        //да, кустарное решение, но как есть
        System.arraycopy(xVals, 0, NxVals, 0, pos);
        System.arraycopy(yVals, 0, NyVals, 0, pos);
        NxVals[pos] = x;
        NyVals[pos] = y;
        System.arraycopy(xVals, pos, NxVals, pos+1,count-pos );
        System.arraycopy(yVals, pos, NyVals, pos+1, count-pos);

        xVals=NxVals;
        yVals=NyVals;
    }
    @Override public void remove(int index){
        if(index<0 || index >=count){throw new ArrayIndexOutOfBoundsException("INDEX OUT ARRAY");}
        for(int i=index;i<count-1;++i){
            xVals[i]=xVals[i+1];
            yVals[i]=yVals[i+1];
        }
        --count;
        xVals=Arrays.copyOf(xVals,count);
        yVals=Arrays.copyOf(yVals,count);
    }


    /// Фабрика итераторов:
    @Override
    public Iterator<Point>  iterator(){
        Iterator<Point> ITERATOR = new Iterator<Point>()  {
            private int i=0;
            @Override
            public boolean hasNext(){
                return i<count;
                ///Анонимный nested класс имеет неявную ссылку на объект внешнего
                ///соответственно, можно безопасно использовать count
            }
            @Override
            public Point next() {
                if(!hasNext()) throw new NoSuchElementException("No element left");
                Point res = new Point(xVals[i],yVals[i]);
                ++i;
                return res;
            }
        };
        return ITERATOR;
    }



    @Override protected void sort(){
        TreeMap<Double,Double>dots =new TreeMap<>();
        for(int i=0;i<count;++i){dots.put(xVals[i],yVals[i]);}
        int j=0;
        for(double x : dots.keySet()){
            xVals[j]=x;
            yVals[j]=dots.get(x);
            ++j;
        }
    }
    @Override protected int floorIndexOfX(double x) {
        int i=0;
        while(i<count && (x-xVals[i])>delta){++i;}//automatically checks if x-xVals[i]>0 -> x>xVals[i]
        return (i<count)? i-1: i;
    }
    @Override protected double extrapolateLeft(double x) {return interpolate(x,0) ;}
    @Override protected double extrapolateRight(double x) {return interpolate(x,count);}
    @Override protected double interpolate(double x, int floorIndex) {
        /*if(count==1){return yVals[0];}
        else if(floorIndex==count){
            return interpolate(x,xVals[count-2],xVals[count-1],yVals[count-2],yVals[count-1]);
        }
        else{
            double xleft=getX(floorIndex);
            double xright=getX(floorIndex+1);
            double yleft=getY(floorIndex);
            double yright=getY(floorIndex+1);
            return interpolate(x,xleft,xright,yleft,yright);
        }*/
        double ourX = getX(floorIndex);
        double nextX = getX(floorIndex + 1);
        if (x < ourX || x > nextX) throw new InterpolationException();
        return interpolate(x, ourX, nextX, getY(floorIndex), getY(floorIndex + 1));
    }

}
