package functions.classes;
import functions.abstracts.AbstractTabulatedFunction;
import functions.interfaces.MathFunction;

import java.util.Arrays;
import java.util.TreeMap;

import static java.lang.Math.abs;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction{

    protected double[] xVals;
    protected double[] yVals;

    public ArrayTabulatedFunction(double[] xVals,double[] yVals){
        if(xVals.length==yVals.length){
            this.count=xVals.length;
            this.xVals=new double[count];
            this.yVals=new double[count];

            for(int i=0;i<count;++i){
                this.xVals[i]=xVals[i];
                this.yVals[i]=yVals[i];
            }
            //quick sort???
            //insertion sort - for setX = o(n)
        }
        ///else error!
    }
    public ArrayTabulatedFunction(MathFunction source,double xFrom,double xTo,int count){
        if(abs(xFrom-xTo)<1e-32) {
            for (int i = 0; i < count; ++i) {
                this.xVals[i] = xTo;
                this.yVals[i] = source.apply(xTo);
            }
        }
        else{
            double buffer;
            double step;
            if(xTo<xFrom){
                buffer=xFrom;
                xFrom=xTo;
                xTo=buffer;
            }
            step=(xTo-xFrom)/count;
            buffer=xFrom;
            for(int i=0;i<count-1;++i){
                this.xVals[i] = buffer;
                this.yVals[i] = source.apply(buffer);
                buffer+=step;
            }
            this.xVals[count-1] = xTo;
            this.yVals[count-1] = source.apply(xTo);
        }
    }
    public void insert(Object X,Object Y){
        if (!(X instanceof Number) || !(Y instanceof Number)) {
            return;
        }
        double x=((Number)X).doubleValue();
        double y=((Number)Y).doubleValue();

        int index=floorIndexOfX(X);
        if(index==0){index=(x<xVals[0])? index=-1: index;}

        double[] xTemp=Arrays.copyOf(xVals,count+1);
        double[] yTemp=Arrays.copyOf(yVals,count+1);

        for(int i=count;i>index+1;--i){
            xTemp[i]=xTemp[i-1];
            yTemp[i]=yTemp[i-1];
        }
        xTemp[index+1]=x;
        yTemp[index+1]=y;

        xVals=xTemp;
        yVals=yTemp;
        ++count;
    }

    @Override public double getX(int index) {
        return xVals[index];
    }

    @Override public double getY(int index) {
        return yVals[index];
    }

    @Override public void setY(int index, Object val) {
        double y;
        if(val instanceof Number) {
            y=((Number)val).doubleValue();
            yVals[index]=y;
        }
        ///else{error}
    }

    @Override public void setX(int index, Object X) {
        double x;
        if(X instanceof Number) {
            x=((Number)X).doubleValue();
            xVals[index]=x;
        }
        /// else error
        this.sort();
    }

    @Override public int indexOfX(Object X) {
        double x;
        int i=-1;
        if(X instanceof Number) {
            x=((Number)X).doubleValue();
            for(i=0;i<count && abs(x-xVals[i])<1e-32 ;++i);
        }
        return (i!=-1 && i<count)? i:-1;
    }

    @Override public int indexOfY(Object Y) {
        double y;
        int i=-1;
        if(Y instanceof Number) {
            y=((Number)Y).doubleValue();
            for(i=0;i<count && abs(y-yVals[i])<1e-32;++i){}
        }
        return (i!=-1 && i<count)? i:-1;
    }

    @Override public double leftBound() {
        return xVals[0];
    }

    @Override public double rightBound() {
        return xVals[count-1];
    }

    @Override public int floorIndexOfX(Object X) {
        double x;
        if(X instanceof Number) {
            int i=0;
            x=((Number)X).doubleValue();
            for (; i < count && abs(x - xVals[i]) < 1e-32 && x < xVals[i]; ++i);
            if(i==count){return count-1;}
            return (x < xVals[i]) ? i - 1 : i;
        }
        return -1;
    }

    /// ////////////////////////
    protected void sort(){
        TreeMap<Double,Double>dots =new TreeMap<>();
        for(int i=0;i<count;++i){
            dots.put(xVals[i],yVals[i]);
        }
        int j=0;
        for(double x : dots.keySet()){
            xVals[j]=x;
            yVals[j]=dots.get(x);
            ++j;
        }
    }

    @Override protected double extrapolateLeft(double x) {
        return interpolate(x,0) ;
    }

    @Override protected double extrapolateRight(double x) {
        return interpolate(x,count-2);
    }

    @Override protected double interpolate(double x, int floorIndex) {
        if(floorIndex<0 || floorIndex>count-2){
            return Double.NaN;
        }
        else if(count==1){return yVals[0];}
        else{
            double xleft=getX(floorIndex);
            double xright=getX(floorIndex+1);
            double yleft=getY(floorIndex);
            double yright=getX(floorIndex+1);
            return interpolate(x,xleft,xright,yleft,yright);
        }
    }

}
