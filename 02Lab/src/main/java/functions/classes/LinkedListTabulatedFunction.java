package functions.classes;
import functions.abstracts.AbstractTabulatedFunction;
import functions.interfaces.MathFunction;
import java.util.Arrays;
import java.util.TreeMap;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction {
    private int count;
    private Node head;

    public LinkedListTabulatedFunction(double[] xVal, double[] yVal){
        if(xVal.length != yVal.length){
            throw new IllegalArgumentException("Массивам необходимо быть одинаковой длины");
        }

        for(int i = 1; i < xVal.length; i++){
            if(xVal[i] <= xVal[i-1]){
                throw new IllegalArgumentException("Значеним необходимо строго возрастать в xVal");
            }
        }
        this.count = 0;
        this.head = null;
        for(int i = 0; i < xVal.length;i++){
            addNode(xVal[i], yVal[i]);
        }
    }
    //конструктор с дискретизацией функции
    public LinkedListTabulatedFunction(MathFunction s, double xFrom, double xTo, int count){

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

    public Node getHead(){
        return head;
    }

    private Node getNode(int index){
        if (index < 0 || index >= count){
            throw new IndexOutOfBoundsException("Индекс: " + index + ", размер: " + count);
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

    @Override
    protected int floorIndexOfX(Object X) {
        if (!(X instanceof Number)) {
            throw new IllegalArgumentException("NEED NUMBER!!!");
        }
        double x = ((Number) X).doubleValue();

        if (head == null) {
            throw new IllegalArgumentException("Err");
        }
        if (x < head.x) {
            return 0;
        }
        if (x > head.prev.x) {
            return count;
        }
        Node curr = head;
        for (int i = 0; i < count - 1; i++) {
            if (x < curr.next.x) {
                return i;
            }
            curr = curr.next;
        }
        return count - 1;
    }

    @Override
    protected double extrapolateLeft(double x) {
        if(count == 1){
            return head.y;
        }
        return interpolate(x, 0);
    }
    @Override
    protected double extrapolateRight(double x) {
        if(count == 1){
            return head.y;
        }
        return interpolate(x, count - 2);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if(count == 1){
            return head.y;
        }
        Node leftNode = getNode(floorIndex);
        Node rightNode = leftNode.next;
        return interpolate(x, leftNode.x, rightNode.x, leftNode.y, rightNode.y);
    }

    @Override
    public int getCount(){
        return count;
    }
    @Override
    public double getX(int index){
        return getNode(index).x;
    }

    @Override
    public double getY(int index){
        return getNode(index).y;
    }

    @Override
    public void setY(int index, Object val) {
        if (val instanceof Number) {
            getNode(index).y = ((Number) val).doubleValue();
        }else{
            throw new IllegalArgumentException("Y - не число");
        }
    }
    @Override
    public void setX(int index, Object X) {
        if (X instanceof Number) {
            if (index < 0 || index >= count) throw new IndexOutOfBoundsException("Индекс: " + index + ", размер: " + count);
            if (head == null) throw new IllegalStateException("Список пуст");
            //число и инедкс верные
            double s_x = ((Number) X).doubleValue();
            Node point = head;
            if(count==1){point.x=s_x;return;}
            for(int i=0;i<count;i++){point=point.next;} //просто проходим до нужного индекса

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
        }else{
            throw new IllegalArgumentException("Х - не число");
        }
    }
    @Override
    public double leftBound() {
        if(head == null){
            throw new IllegalArgumentException("Список пуст");
        }
        return head.x;
    }

    @Override
    public double rightBound() {
        if(head == null){
            throw new IllegalArgumentException("Список пуст");
        }
        return head.prev.x;
    }

    @Override
    public int indexOfY(Object Y) {
        if (!(Y instanceof Number)) {return -1;}

        double y = ((Number) Y).doubleValue();
        if (head == null){
            return -1;
        }
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
    public int indexOfX(Object X) {
        if (!(X instanceof Number)) {return -1;}

        double x = ((Number) X).doubleValue();
        if (head == null){
            return -1;
        }
        Node curr = head;
        for (int i = 0; i < count; i++) {
            if (Math.abs(curr.x - x) < 1e-10) {
                return i;
            }
            curr = curr.next;
        }
        return -1;
    }
    protected Node floorNodeOfX(double x){
        if (head == null){
            throw new IllegalStateException("Список пуст, ы");
        }
        if(x < head.x){
            return head;
        }

        if(x > head.prev.x){
            return head.prev;
        }

        Node curr = head;
        for(int i = 0; i < count - 1; i++){
            if(x < curr.next.x){
                return curr;
            }
            curr = curr.next;
        }

        return head.prev;
    }
    @Override
    public double apply(Object X) {
        if (X instanceof Number) {
            double x = ((Number) X).doubleValue();
        if(x < leftBound()){
            return extrapolateLeft(x);
        }else if(x > rightBound()){
            return extrapolateRight(x);
        }else{
            Node floorNode = floorNodeOfX(x);
            if(java.lang.Math.abs(floorNode.x - x) < 1e-10){
                return floorNode.y;
            }

            return interpolate(x, floorNode.x, floorNode.next.x, floorNode.y, floorNode.next.y);
        }
    }else {return Double.NaN;}
    }


}
