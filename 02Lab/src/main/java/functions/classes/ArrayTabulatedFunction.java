package functions.classes;
import functions.abstracts.AbstractTabulatedFunction;
import functions.interfaces.Insertable;
import functions.interfaces.MathFunction;
import functions.interfaces.Removable;

import java.util.Arrays;
import java.util.TreeMap;

import static java.lang.Math.abs;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable{

    private double[] xVals;
    private double[] yVals;

    public ArrayTabulatedFunction(double[] xVals, double[] yVals){
        if(xVals.length!=yVals.length){throw new IllegalArgumentException();}
        this.count=xVals.length;
        this.xVals=new double[count];
        this.yVals=new double[count];
        this.xVals=Arrays.copyOf(xVals,this.count);
        this.yVals=Arrays.copyOf(yVals,this.count);
        this.sort();
    }
    public ArrayTabulatedFunction(MathFunction source,double xFrom,double xTo,int count){
        if(count<=0){throw new IllegalArgumentException();}

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
        return (index>=count || index <0)?  Double.NaN : xVals[index];
    }
    @Override public double getY(int index) {
        return (index>=count || index <0)? Double.NaN :yVals[index] ;
    }
    @Override public void setY(int index, Object Y) {
        if(!(Y instanceof Number) || index<0 || index>=count){throw new IllegalArgumentException();}
        else {yVals[index] = ((Number) Y).doubleValue();}
    }
    @Override public void setX(int index, Object X) {
        if(!(X instanceof Number) || index<0 || index>=count){throw new IllegalArgumentException();}
        else{
            xVals[index]=((Number)X).doubleValue();
            this.sort();//заменить на insert new+ remove old -> faster sollution
        }
    }
    @Override public int indexOfX(Object X) {
        if(!(X instanceof Number)){return -1;}
        else{
            double x=((Number)X).doubleValue();
            int i=0;
            while (i<count && abs(xVals[i]-x)>delta){++i;}
            return (i<count)? i: -1;
        }
    }
    @Override public int indexOfY(Object Y) {
        if(!(Y instanceof Number)){return -1;}
        else{
            double y=((Number)Y).doubleValue();
            int i=0;
            while (i<count && abs(yVals[i]-y)>delta){++i;}
            return (i<count)? i: -1;
        }
    }
    @Override public double leftBound() {
        return xVals[0];
    }
    @Override public double rightBound() {
        return xVals[count-1];
    }
    @Override public int floorIndexOfX(Object X) {
        if(!(X instanceof Number)) {return -1;}
        else{
            double x=((Number)X).doubleValue();;
            int i=0;
            while(i<count && (x-xVals[i])>delta){++i;}//automatically checks if x-xVals[i]>0 -> x>xVals[i]
            return (i<count)? i-1: i;
        }
    }
    @Override public void insert(Object x, Object y){
        /// ДОПИСАТЬ!!!///
    }
    @Override public void remove(int index){
        if(index<0 || index >=count){throw new IndexOutOfBoundsException();}
        for(int i=index;i<count-1;++i){
            xVals[i]=xVals[i+1];
            yVals[i]=yVals[i+1];
        }
        --count;
        xVals=Arrays.copyOf(xVals,count);
        yVals=Arrays.copyOf(yVals,count);
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
    @Override protected double extrapolateLeft(double x) {return interpolate(x,0) ;}
    @Override protected double extrapolateRight(double x) {return interpolate(x,count);}
    @Override protected double interpolate(double x, int floorIndex) {
        if(count==1){return yVals[0];}
        else if(floorIndex==count){
            return interpolate(x,xVals[count-2],xVals[count-1],yVals[count-2],yVals[count-1]);
        }
        else{
            double xleft=getX(floorIndex);
            double xright=getX(floorIndex+1);
            double yleft=getY(floorIndex);
            double yright=getY(floorIndex+1);
            return interpolate(x,xleft,xright,yleft,yright);
        }
    }

}
