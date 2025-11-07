package functions.classes;

import exceptions.InterpolationException;
import functions.abstracts.AbstractTabulatedFunction;
import functions.interfaces.Insertable;
import functions.interfaces.MathFunction;
import functions.interfaces.Removable;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Insertable,Removable {
    ///================Nesquik(Nested) класс Node==========================================================
    public static class Node {

        public Node next;
        public Node prev;
        public double x;
        public double y;

        public Node(double x, double y){
            this.x = x;
            this.y = y;
            this.next = null;
            this.prev = null;
        }
    }
    ///================Служебные-переменные==========================================================

    private int count;
    private Node head;
    ///===============================================================================================

    ///================Конструкторы==========================================================
    //Конструктор через массивы double[]
    public LinkedListTabulatedFunction(double[] xVal, double[] yVal){

        if (xVal.length < 2) {throw new IllegalArgumentException("Length must be >2");}

        checkLengthIsTheSame(xVal, yVal);
        checkSorted(xVal);

        this.count = 0;
        this.head = null;
        for(int i = 0; i < xVal.length;i++){
            addNode(xVal[i], yVal[i]);
        }
    }
    //конструктор с дискретизацией функции
    public LinkedListTabulatedFunction(MathFunction s, double xFrom, double xTo, int count){
        if (count < 2) {throw new IllegalArgumentException("Must be >2 points");}

        if (xFrom > xTo){
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        this.count = 0;
        this.head = null;

        if(xFrom == xTo){
            double yVal = s.apply(xFrom);
            for(int i = 0; i < count; i++){
                addNode(xFrom, yVal);
            }
        }else{
            double step = (xTo - xFrom) / (count - 1);
            for(int i = 0; i < count; i++){
                double x = xFrom + i * step;
                double y = s.apply(x);
                addNode(x, y);
            }
        }
    }
    ///================Итератор==========================================================
    @Override
    public Iterator<Point> iterator(){
        return new Iterator <Point>(){
            private Node current = head;
            private int itcount = 0;
            @Override
            public boolean hasNext(){
                return itcount<count;
            }
            @Override
            public Point next(){
                if(!hasNext()){throw new NoSuchElementException("There is no more elements");}

                Point Pi = new Point(current.x, current.y);
                current = current.next;
                itcount++;
                return Pi;
            }
        };
    }
    ///================Удаление/Вставка==========================================================
    @Override
    public <T extends Number> void insert(T X,T Y){
        double x=X.doubleValue();
        double y=Y.doubleValue();
        if (head == null) {
            addNode(x, y);
            return;
        }
        Node curr = head;
        do {
            if (Math.abs(curr.x - x) < 1e-10) {
                curr.y = y;
                return;
            }
            if (x < curr.x) {
                Node newNode = new Node(x, y);

                Node prevNode = curr.prev;
                prevNode.next = newNode;
                newNode.prev = prevNode;

                newNode.next = curr;
                curr.prev = newNode;

                if (curr == head) {
                    head = newNode;
                }

                count++;
                return;
            }

            curr = curr.next;
        } while (curr != head);

        Node last = head.prev;
        Node newNode = new Node(x, y);

        last.next = newNode;
        newNode.prev = last;

        newNode.next = head;
        head.prev = newNode;

        count++;
    }

    @Override
    public void remove(int index){
        if(index<0 || index >=count){throw new IndexOutOfBoundsException();}
        Node point =head;
        for(int i=0;i<index;++i){point=point.next;}
        if(count==1){head=null;}else{
            //как обычно,перенаправляем указатели
            point.prev.next = point.next;
            point.next.prev = point.prev;
            // Если удаляем голову, сдвигаем head
            if (point == head) {head = point.next;}
        }
        count--;

    }
    ///================Геттеры========================================================================
    ///-----------------Геттеры-1-----------------------------------------------------------------
    public Node getHead(){
        return head;
    }

    public Node getNode(int index){
        if (index < 0 || index >= count){
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + count);
        }
        if(index < count / 2){
            Node curr = head;
            for(int i = 0; i < index; i++){
                curr = curr.next;
            }
            return curr;
        } else{
            Node curr = head.prev;
            for (int i = count - 1; i > index; i--){
                curr = curr.prev;
            }
            return curr;
        }
    }
    ///-----------------Геттеры-Точек-----------------------------------------------------------------
    @Override
    public double leftBound() {
        if(head == null){throw new IllegalArgumentException("EMPTY LIST ERR");}

        return head.x;
    }

    @Override
    public double rightBound() {
        if(head == null){throw new IllegalArgumentException("EMPTY LIST ERR");}

        return head.prev.x;
    }
    @Override
    public double getX(int index){
        if (index < 0 || index >= count) {throw new IndexOutOfBoundsException("GetX index out of bounds");}

        return getNode(index).x;
    }

    @Override
    public double getY(int index){
        if (index < 0 || index >= count) {throw new IndexOutOfBoundsException("GetY index out of bounds");}

        return getNode(index).y;
    }
    ///-----------------Геттеры-Индексов-----------------------------------------------------------------
    @Override
    public <T extends Number> int indexOfY(T Y)
    {
        if (head == null){throw new IllegalStateException("EMPTY LIST ERR");}

        double y = Y.doubleValue();
        Node curr = head;
        for (int i = 0; i < count; i++) {
            if (Math.abs(curr.y - y) < 1e-10) {
                return i;
            }
            curr = curr.next;
        }
        return -1;
    }
    @Override
    public <T extends Number> int indexOfX(T X)
    {
        if (head == null){throw new IllegalStateException("EMPTY LIST ERR");}

        double x = X.doubleValue();
        Node curr = head;
        for (int i = 0; i < count; i++) {
            if (Math.abs(curr.x - x) < 1e-10) {
                return i;
            }
            curr = curr.next;
        }
        return -1;
    }
    ///-----------------Геттеры-Числа-----------------------------------------------------------------
    @Override
    public int getCount(){
        return count;
    }
    ///-------------------------------------------------------------------------------------------------
    ///===============================================================================================

    ///================Сеттеры========================================================================

    @Override
    public <T extends Number> void setY(int index, T val)
    {
        if (index < 0 || index >= count) {throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + count);}

        getNode(index).y = val.doubleValue();
    }
    ///==!!!WIP-UNDER-RE-CONCTRUCTION!!!!=============!!!
    @Override
    public <T extends Number> void setX(int index, T X)
    {
        if (index < 0 || index >= count) throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + count);
        if (head == null) throw new IllegalStateException("Список пуст");
        //число и инедкс верные
        double s_x = X.doubleValue();
        Node point = head;
        if(count==1){point.x=s_x;return;}
        for(int i=0;i<index;i++){point=point.next;} //просто проходим до нужного индекса

        if(point == head){  head = point.next;}     //если голова(решил заранее ,т.к. потом изменится point)
        point.next.prev=point.prev;                 //меняем указатели
        point.prev.next=point.next;
        point.x=s_x;

        Node point_pos=head;                        //ищем место
        while(point_pos.x<=s_x){point_pos=point_pos.next;}

        point.next=point_pos;                       //перепривязываем
        point.prev=point_pos.prev;
        point_pos.prev.next=point;
        point_pos.prev=point;
        if (head.x>s_x && point_pos==head){head=point;}//Если вставили вперед - меняем голову
    }
    /// ----------------------------------------------------------------------------------------------------------


    ///==================СЛУЖЕБНОЕ================================================================================
    @Override
    protected int floorIndexOfX(double x)
    {
        if(head == null){   throw new IllegalArgumentException("EMPTY LIST ERR");}

        if (x < head.x) {   throw new IllegalArgumentException("x = " + x + " < LEFT BOUND " + head.x);}

        if(x > head.prev.x)return count;

        Node curr = head;
        for(int i = 0; i < count - 1; i++){
            if(x < curr.next.x){
                return i;
            }
            curr = curr.next;
        }


        return count - 1;
    }
    protected Node floorNodeOfX(double x){
        if (head == null){    throw new IllegalArgumentException("EMPTY LIST ERR");}

        if (x < head.x){   throw new IllegalArgumentException("x = " + x + " < LEFT BOUND " + head.x);}

        if (x > head.prev.x)return head.prev;

        Node curr = head;
        for(int i = 0; i < count - 1; i++){
            if(x < curr.next.x){
                return curr;
            }
            curr = curr.next;
        }
        return head.prev;
    }

    private void addNode(double x, double y){
        Node nwNode = new Node(x, y);

        if (head == null){
            head = nwNode;
            head.next = head;
            head.prev = head;
        }else{
            Node last = head.prev;

            last.next = nwNode;
            nwNode.prev = last;

            nwNode.next = head;
            head.prev = nwNode;
        }

        count++;
    }

    @Override
    protected double extrapolateLeft(double x) {
        if (count < 2) throw new IllegalArgumentException("EXTR: <2 ELEM");
        //return interpolate(x, 0);
        int floorIndex = 0;
        Node leftNode = getNode(floorIndex);
        Node rightNode = leftNode.next;
        return interpolate(x, leftNode.x, rightNode.x, leftNode.y, rightNode.y);
    }
    @Override
    protected double extrapolateRight(double x) {
        if (count < 2) throw new IllegalArgumentException("EXTR: <2 ELEM");
        //return interpolate(x, count - 2);
        int floorIndex = count-2;
        Node leftNode = getNode(floorIndex);
        Node rightNode = leftNode.next;
        return interpolate(x, leftNode.x, rightNode.x, leftNode.y, rightNode.y);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        Node leftNode = getNode(floorIndex);
        Node rightNode = leftNode.next;
        //System.out.println(leftNode.x);
        //System.out.println(rightNode.x);
        //System.out.println(x);
        if (x < leftNode.x || x > rightNode.x) {throw new InterpolationException("X out of inter range");}
        return interpolate(x, leftNode.x, rightNode.x, leftNode.y, rightNode.y);
    }

    @Override
    public <T extends Number> double apply(T X) {
        double x = X.doubleValue();

        Node floorNode = floorNodeOfX(x);

        if (x < head.x) return extrapolateLeft(x);
        if (x > head.prev.x) return extrapolateRight(x);
        if (x == floorNode.x) return floorNode.y;

        Node nextNode = floorNode.next;
        return interpolate(x, floorNode.x, nextNode.x, floorNode.y, nextNode.y);
    }
    protected void sort(){                             //заглушка

    }
    /*
    protected void sort(){                             //Общая финкция сортировки
        if(head.next==head||head==null){return;}    //проверяем на пустой список
        head.prev.next=null;                        //разрываем список(делаем линейным, а не циклическим)
        head.prev=null;

        head=mergesort(head);
        Node tails =head;
        while (tails.next != null) {tails = tails.next;}
        tails.next=head;                            //связываем обратно список
        head.prev=tails;
    }
    private Node GetMiddle(Node x) {                //Поиск серединного
        if (x == null) return x;

        Node slow = x;
        Node fast = x;

        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;}
        return slow;
    }
    private Node mergesort(Node x){                 //Разбитие на кусочки и слияние
        if(x.next==null||x==null){return x;}

        Node mid = GetMiddle(x);
        Node mid_next = mid.next;
        mid.next=null;                              //разрываем
        if (mid_next != null) {mid_next.prev = null;}

        Node left = mergesort(x);
        Node right = mergesort(mid_next);
        return Merge(left,right);
    }
    private Node Merge(Node first,Node second) {    //Слияние
        if (first == null) return second;
        if (second == null) return first;
        Node res;

        if(first.x<=second.x){
            res=first;
            res.next=Merge(first.next,second);      //значит первый нод меньше второго
            if (res.next != null) {res.next.prev = res;}  //пересвязываем
            res.prev = null;
        }else{
            res=second;
            res.next=Merge(first,second.next);      //значит второго нод меньше первого
            if (res.next != null) {res.next.prev = res;}
            res.prev = null;
        }
        return res;
    }
    */

}
