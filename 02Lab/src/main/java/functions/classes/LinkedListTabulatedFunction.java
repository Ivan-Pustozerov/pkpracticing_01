package functions.classes;
import functions.abstracts.AbstractTabulatedFunction;
import functions.interfaces.Insertable;
import functions.interfaces.MathFunction;


public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Insertable {
    private int count;
    private Node head;

    public LinkedListTabulatedFunction(double[] xVal, double[] yVal){
        if(xVal.length != yVal.length){
            throw new IllegalArgumentException("Массивам необходимо быть одинаковой длины");
        }
        this.count = 0;
        this.head = null;
        for(int i = 0; i < xVal.length;i++){
            addNode(xVal[i], yVal[i]);
        }
        this.sort();                    //дописал потом ХD
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

    public Node getNode(int index){
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

    public void sort(){                             //Общая финкция сортировки
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

    public void insert(Object X,Object Y){
        if(!(X instanceof Number) || !(Y instanceof Number)){throw new IllegalArgumentException();}
        double x=((Number)X).doubleValue();
        double y=((Number)Y).doubleValue();
        Node where=floorNodeOfX(x);
        if(where.next==this.head){addNode(x,y);}
        else{
            Node temp=new Node(x,y);
            temp.prev=where;
            temp.next=where.next;
            where.next=temp;
            ++count;
        }
    }

}
