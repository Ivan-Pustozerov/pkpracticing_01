package functions.classes;
import functions.abstracts.AbstractTabulatedFunction;
import functions.interfaces.Insertable;
import functions.interfaces.MathFunction;
import functions.interfaces.Removable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import static java.lang.Math.abs;


/// Табулированная функция на массиве
public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable, Serializable {
///================Служебные-переменные==========================================================
    private double[] xVals;  // точки по X
    private double[] yVals; // точки по Y
    @Serial private static final long serialVersionUID = -4633298896101580232L;
///===============================================================================================


    /// Конструктор через массивы double[]
    public ArrayTabulatedFunction(double[] xVals, double[] yVals){
        //+ здесь проверка на длину таблы >2    //
        checkLengthIsTheSame(xVals, yVals);    //    Проверки на валидность
        checkSorted(xVals);                   //

        this.count=xVals.length;
        this.xVals=new double[count];
        this.yVals=new double[count];
        this.xVals=Arrays.copyOf(xVals,this.count);
        this.yVals=Arrays.copyOf(yVals,this.count);
    }

    /// Конструктор через функцию и промежуток
    public ArrayTabulatedFunction(MathFunction source,double xFrom,double xTo,int count){
                                                            //
        if(count<=0){throw new IllegalArgumentException();}//    Проверки на валидность
                                                          //

        this.xVals=new double[count];
        this.yVals=new double[count];
        double x;
        double y;

        if(abs(xFrom-xTo)<delta) { // Если конец и начало совпадают
            x=xFrom;
            y=source.apply(x);
            for (int i = 0; i < count; ++i) {  //
                this.xVals[i] = x;            // Заполняем одним значением
                this.yVals[i] = y;           //
            }
        }
        else{
            double step;
            if(xTo<xFrom){ // Если конец и начало перепутаны
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

    /// Вставка элемента по точке
    @Override public <T extends Number> void insert(T X, T Y){
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
        /// ----------------------------------------------------------
        //(откуда,начало,куда,куданачало,до чего)   - насколько разобрался
        //да, кустарное решение, но как есть
        /// Коммент лучше писать формально ;)
        /// ----------------------------------------------------------
        System.arraycopy(xVals, 0, NxVals, 0, pos);
        System.arraycopy(yVals, 0, NyVals, 0, pos);
        NxVals[pos] = x;
        NyVals[pos] = y;
        System.arraycopy(xVals, pos, NxVals, pos+1,count-pos );
        System.arraycopy(yVals, pos, NyVals, pos+1, count-pos);

        xVals=NxVals;
        yVals=NyVals;
    }

    /// Удаление элемента по индексу
    @Override public void remove(int index){
        if(index<0 || index >=count) throw new IndexOutOfBoundsException(); // Валидность

        for(int i=index;i<count-1;++i){ //  Сдвигаем массив
            xVals[i]=xVals[i+1];       //   с индекса влево
            yVals[i]=yVals[i+1];      //      переписываем
        }
        --count;
        xVals=Arrays.copyOf(xVals,count);
        yVals=Arrays.copyOf(yVals,count);
    }

    /// Итератор:
    @Override public Iterator<Point>  iterator(){
        Iterator<Point> ITERATOR = new Iterator<Point>()  {
            //------------Служебное-------------
            private int i=0;
            //----------------------------------

            // Проверка - есть ли следующий
            @Override
            public boolean hasNext(){
                return i<count;
            }

            // Возврат текущего + сдвиг указателя
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

    ///-----------------Геттеры-Точек--------------------------------------------------------------------------
    public double[] getxVals() {
        return Arrays.copyOf(xVals, count);
    }
    public double[] getyVals() {
        return Arrays.copyOf(yVals, count);
    }

    @Override public double getX(int index) {
        if (index>=count || index <0) throw new ArrayIndexOutOfBoundsException("Out of Index");
        return xVals[index];
    }
    @Override public double getY(int index) {
        if (index>=count || index <0) throw new ArrayIndexOutOfBoundsException("Out of Index");
        return yVals[index] ;
    }

    @Override public double leftBound() {
        return xVals[0];
    }
    @Override public double rightBound() {
        return xVals[count-1];
    }
    /// --------------------------------------------------------------------------------------------------------


    ///-----------------Геттеры-Индексов-------------------------------------------------------------------------
    @Override public <T extends Number> int indexOfX(T X) {
        double x = X.doubleValue();
        int i = 0;
        while (i<count && abs(xVals[i]-x)>delta){++i;}
        return (i<count)? i: -1;
    }
    @Override public <T extends Number> int indexOfY(T Y) {
        double y = Y.doubleValue();
        int i = 0;
        while (i<count && abs(yVals[i]-y)>delta){++i;}
        return (i<count)? i: -1;
    }
    /// --------------------------------------------------------------------------------------------------------


    /// ----------------Сеттеры-Точек---------------------------------------------------------------------------
    @Override public <T extends Number> void setY(int index, T Y) {
        yVals[index] = Y.doubleValue();
    }
    @Override public <T extends Number> void setX(int index, T X) {
        xVals[index] = X.doubleValue();
        this.sort();
    }
    /// ----------------------------------------------------------------------------------------------------------


    ///==================СЛУЖЕБНОЕ================================================================================

    /// Сортировка
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

    /// Ограничивающий интервал x
    @Override protected int floorIndexOfX(double x) {
        int i=0;
        while(i<count && (x-xVals[i])>delta){++i;}//automatically checks if x-xVals[i]>0 -> x>xVals[i]
        return (i<count)? i-1: i;
    }

    /// Экстра-Интерполяции:
    @Override protected double extrapolateLeft(double x) {return interpolate(x,0) ;}
    @Override protected double extrapolateRight(double x) {return interpolate(x,count);}
    @Override protected double interpolate(double x, int floorIndex) {

        if(count==1) return yVals[0]; //если точка одна - возврат начала

        if(floorIndex==count) //если точка последняя - возврат НАСИЛЬНЫЙ интреполяции послед интервала
            return interpolate(x,xVals[count-2],xVals[count-1],yVals[count-2],yVals[count-1]);

        double xleft=getX(floorIndex);                        //
        double xright=getX(floorIndex+1);                    //  Высчет нужного
        double yleft=getY(floorIndex);                      //      интервала
        double yright=getY(floorIndex+1);                  //      интерполяции
        return interpolate(x,xleft,xright,yleft,yright);  //
    }

}
