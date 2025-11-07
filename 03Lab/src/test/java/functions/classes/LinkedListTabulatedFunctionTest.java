package functions.classes;

import exceptions.DifferentLengthOfArraysException;
import exceptions.InterpolationException;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionTest {

    private double[] xVal = {1, 2, 3, 4, 5};
    private double[] yVal = {10, 20, 30, 40, 50};
    private double delta =1e-32;
    private LinkedListTabulatedFunction func;

    @Test
    void test_getHead() {
        func = new LinkedListTabulatedFunction(xVal, yVal);

        LinkedListTabulatedFunction.Node head = func.getHead();

        assertNotNull(head);                       // Проверяем, что голова не null
        assertEquals(1, head.x);          // Проверяем значение x у головы
        assertEquals(10, head.y);
    }
    @Test
    void test_getCount() {
        double[] xVal = {1, 2, 3};
        double[] yVal = {4, 5, 6};
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xVal, yVal);

        assertEquals(3, func.getCount());
    }

    @Test
    public void test_GetXY() {
        double[] xVal = {10, 20, 30};
        double[] yVal = {100, 200, 300};
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xVal, yVal);
        for (int i = 0; i < func.getCount(); i++) {
            assertEquals(xVal[i], func.getX(i), 1e-9);
            assertEquals(yVal[i], func.getY(i), 1e-9);
        }
    }
    @Test
    public void test_GetXY_INVELID() {
        double[] xVal = {10, 20, 30};
        double[] yVal = {100, 200, 300};
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xVal, yVal);
        assertThrows(IndexOutOfBoundsException.class, () -> func.getX(505), "Куда ты идешь?");

        assertThrows(IndexOutOfBoundsException.class, () -> func.getY(-404), "Видимо не туда");

    }
    @Test
    public void test_ConstructorRangeRage() {
        double[] xVal = {1.0, 2.0};
        double[] yVal = {3.0};
        DifferentLengthOfArraysException exception = assertThrows(
                DifferentLengthOfArraysException.class,
                () -> new LinkedListTabulatedFunction(xVal, yVal)
        );

        assertEquals("arrays have different length", exception.getMessage());
    }

    @Test
    public void test_ConstructorNormal() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(new IdentityFunction(), 1, 5, 5);
        LinkedListTabulatedFunction.Node current = func.getHead();

        double expectedX = 1;
        double step = 1;
        int count = 0;
        do {
            assertEquals(expectedX, current.x, 1e-9);
            assertEquals(expectedX, current.y, 1e-9);
            expectedX += step;
            current = current.next;
            count++;
        } while (current != func.getHead() && count < 10);

        assertEquals(5, count);
    }

    @Test
    public void test_ConstructorSwapsRange() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(new IdentityFunction(), 5, 1, 5);
        LinkedListTabulatedFunction.Node head = func.getHead();

        assertNotNull(head);
        assertEquals(1, head.x, 1e-9);

    }
    @Test
    public void test_ConstructorSinglePoint() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(new IdentityFunction(), 3, 3, 4);
        LinkedListTabulatedFunction.Node head = func.getHead();

        assertNotNull(head);
        int count = 0;
        LinkedListTabulatedFunction.Node current = head;
        do {
            assertEquals(3, current.x, 1e-9);
            assertEquals(3, current.y, 1e-9);
            current = current.next;
            count++;
        } while (current != head && count < 10);

        assertEquals(4, count);
    }
    @Test
    void test_Floor_IndexOfX_valid() {
        func = new LinkedListTabulatedFunction(xVal, yVal);
        // x равен голове (1)
        assertEquals(0, func.floorIndexOfX(1.0));
        // x в середине массива (3)
        assertEquals(2, func.floorIndexOfX(3.0));
        // х больше последнего элемента(теперь не ошибка)
        assertEquals(5, func.floorIndexOfX(6.0));

    }
    @Test
    void test_Floor_NodeOfX() {
        // x меньше головы - возвращается голова
        func = new LinkedListTabulatedFunction(
                new double[]{1, 3, 5, 7, 9},
                new double[]{10, 30, 50, 70, 90});
        LinkedListTabulatedFunction.Node head = func.getHead();
        //меньше(как с индексом)
        assertThrows(IllegalArgumentException.class, () -> func.floorNodeOfX(0.5), "floorNodeOfX(0.5) должен вернуть head, GOOD");

        // x больше последнего узла - возвращается последний узел
        LinkedListTabulatedFunction.Node last = head.prev;                                // предположим, что prev последнего - это последний узел
        assertEquals(last, func.floorNodeOfX(10));

        // x находится между узлами, тестируем возвращаемый узел
        assertEquals(head.next, func.floorNodeOfX(3.5));    // 3.5 между 3 и 5, ожидается узел с x=3
        assertEquals(head.next.next, func.floorNodeOfX(5)); // x = 5, между 5 и 7, возвращается узел 5

        // x равен одному из узлов
        LinkedListTabulatedFunction.Node node7 = head.next.next.next;
        assertEquals(node7, func.floorNodeOfX(7));
    }
    @Test
    void test_Floor_NodeOfX_INVALID() {
        func = new LinkedListTabulatedFunction(new double[]{1, 3}, new double[]{10, 30});
        func.remove(0);
        func.remove(0);
        assertThrows(IllegalArgumentException.class, () -> func.floorNodeOfX(0.5), "A gde?");

    }
    @Test
    void test_Floor_IndexOfX_INVALID() {
        double[] xValues = {1.0, 3.0, 5.0};
        double[] yValues = {10.0, 30.0, 50.0};
        LinkedListTabulatedFunction list = new LinkedListTabulatedFunction(xValues, yValues);
        //меньше
        assertThrows(IllegalArgumentException.class, () -> list.floorIndexOfX(0.5), "floorNodeOfX(0.5) должен вернуть head, GOOD");
        list.remove(0);
        list.remove(0);
        list.remove(0);
        assertThrows(IllegalArgumentException.class, () -> list.floorIndexOfX(0.5), "A gde?");

    }
    /*
    @Test
    public void testConstructorWithEqualLengths() {
        double[] xVal = {1.0, 2.0};
        double[] yVal = {3.0};
        assertDoesNotThrow(() -> new LinkedListTabulatedFunction(xVal, yVal));
    }*/
    @Test
    public void test_LeftBound() {
        double[] xVal = {1.5, 2.5};
        double[] yVal = {4.0, 5.0};
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xVal, yVal);
        assertEquals(1.5, func.leftBound());
        //фокусы:
        func.remove(0);
        func.remove(0);
        assertThrows(IllegalArgumentException.class, () -> func.leftBound());
    }

    @Test
    void test_rightBound() {
        double[] xVal = {1.5, 2.5};
        double[] yVal = {4.0, 5.0};
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xVal, yVal);
        assertEquals(2.5, func.rightBound());
        //фокусы:
        func.remove(0);
        func.remove(0);
        assertThrows(IllegalArgumentException.class, () -> func.rightBound());

    }

    @Test
    void testIndexOfY() {
        func = new LinkedListTabulatedFunction(xVal, yVal);

        // Поиск существующего значения
        assertEquals(0, func.indexOfY(10.0));
        assertEquals(2, func.indexOfY(30.0));

        // Поиск значения с близким, но не точным y (с учетом допустимой погрешности)
        double nearValue = 10.0 + 1e-11;
        assertEquals(0, func.indexOfY(nearValue));

        // Поиск значения, отсутствующего в списке
        assertEquals(-1, func.indexOfY(99.9));
    }
    @Test
    void test_E_IndexOfY() {
        func = new LinkedListTabulatedFunction(new double[]{1,2}, new double[]{3,4});
        func.remove(0);
        func.remove(0);
        assertThrows(IllegalStateException.class, () -> func.indexOfY(0.5), "ФУУУ");
    }

    @Test
    void testIndexOfX() {
        func = new LinkedListTabulatedFunction(xVal, yVal);

        // Поиск существующего значения x
        assertEquals(0, func.indexOfX(1.0));
        assertEquals(3, func.indexOfX(4.0));

        // Поиск значения с малой погрешностью
        double nearValue = 1.0 + 1e-11;
        assertEquals(0, func.indexOfX(nearValue));

        // Поиск отсутствующего значения
        assertEquals(-1, func.indexOfX(99.9));

    }
    @Test
    void test_E_IndexOfX() {
        func = new LinkedListTabulatedFunction(new double[]{1,2}, new double[]{3,4});
        func.remove(0);
        func.remove(0);
        assertThrows(IllegalStateException.class, () -> func.indexOfX(0.5), "ФУУУ");
    }
    @Test
    void testApplyValid() {
        double[] xVal = {0, 1, 2};
        double[] yVal = {1, 3, 5};

        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xVal, yVal);
        // Точное совпадение с узлом
        assertEquals(1, func.apply(0));
        assertEquals(3, func.apply(1));
        assertEquals(5, func.apply(2));

        // Интерполяция
        assertEquals(2, func.apply(0.5));
        assertEquals(4, func.apply(1.5));

        // Экстраполяцию справа
        assertEquals(7, func.apply(3));
    }
    @Test
    void testApplyInvalid() {
        double[] xVal = {0, 1, 2};
        double[] yVal = {1, 3, 5};
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xVal, yVal);
        assertThrows(IllegalArgumentException.class,() -> func.apply(-1));
        assertDoesNotThrow(() -> func.apply(10));                               //2 стула:)
    }
    @Test
    void testExtrapolateLeft_singleElement() {
        double[] xValSingle = {1,2};
        double[] yValSingle = {10,1};
        func = new LinkedListTabulatedFunction(xValSingle, yValSingle);
        func.remove(0);
        assertThrows(IllegalArgumentException.class,() -> func.extrapolateLeft(2));
    }
    @Test
    void testExtrapolateLeft_multipleElements() {
        func = new LinkedListTabulatedFunction(xVal, yVal);
        double result = func.extrapolateLeft(1);
        assertEquals(yVal[0], result);
    }

    @Test
    void testExtrapolateRight_singleElement() {
        double[] xValSingle = {1,2};
        double[] yValSingle = {10,2};
        func = new LinkedListTabulatedFunction(xValSingle, yValSingle);
        func.remove(0);
        assertThrows(IllegalArgumentException.class,() -> func.extrapolateRight(2));
    }

    @Test
    void testExtrapolateRight_multipleElements() {
        func = new LinkedListTabulatedFunction(xVal, yVal);
        double result = func.extrapolateRight(5);
        assertEquals(yVal[yVal.length - 1], result);
    }

    @Test
    void test_interpolate() {
        double[] xVal= {0, 1, 2};
        double[] yVal = {1, 3, 5};
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xVal, yVal);
        assertEquals(2, func.interpolate(0.5, 0)); // между 0 и 1
        assertEquals(4, func.interpolate(1.5, 1)); // между 1 и 2
        assertEquals(1, func.interpolate(0, 0));   // точное совпадение с узлом
        assertEquals(5, func.interpolate(2, 1));   // точное совпадение с последним узлом
        assertThrows(InterpolationException.class,() -> func.interpolate(10,1));
    }
    @Test
    public void test_Insert_InsertAtEnd_CircularUpdate() {
        LinkedListTabulatedFunction list = new LinkedListTabulatedFunction(new double[]{1.0, 2.0}, new double[]{10.0, 20.0});
        list.insert(3.0, 30.0);

        //Проверим, что голова не сдвинулась, а хвост правильно связан
        assertEquals(3.0, list.getX(2), "GOOD");
        assertEquals(1.0, list.getX(0), "head не изменился, GOOD");
        assertEquals(2.0, list.getX(1), "GOOD");
        assertEquals(3.0, list.rightBound(), "GOOD");
    }
    @Test
    void test_InsertCorrect(){
        double[] x ={0.0,12.0, 14.3, 50.9};
        double[] y ={0,11.2, -14.3, 5.9};
        LinkedListTabulatedFunction func= new LinkedListTabulatedFunction(x,y);
        func.insert(5.0,4.0);
        double[] xnew ={0.0,5.0,12.0, 14.3, 50.9};
        double[] ynew ={0,4.0,11.2, -14.3, 5.9};
        for(int i=0;i<func.getCount();++i){
            assertEquals(xnew[i],func.getX(i));
            assertEquals(ynew[i],func.getY(i));
        }
        func= new LinkedListTabulatedFunction(x,y);
        func.insert(100.0,4.0);
        xnew =new double[]{0.0,12.0, 14.3, 50.9,100.0};
        ynew =new double[]{0,11.2, -14.3, 5.9,4.0};
        for(int i=0;i<func.getCount();++i){
            assertEquals(xnew[i],func.getX(i));
            assertEquals(ynew[i],func.getY(i));
        }
        func= new LinkedListTabulatedFunction(x,y);
        func.insert(0.0,0.0);
        xnew =new double[]{0.0,12.0, 14.3, 50.9};
        ynew =new double[]{0,11.2, -14.3, 5.9};
        for(int i=0;i<func.getCount();++i){
            assertEquals(xnew[i],func.getX(i));
            assertEquals(ynew[i],func.getY(i));
        }
    }
    @Test
    void test_insertIncorrect(){
        double[] x ={0.0,12.0, 14.3, 50.9};
        double[] y ={0,11.2, -14.3, 5.9};
        LinkedListTabulatedFunction func= new LinkedListTabulatedFunction(x,y);
        //assertThrows(IllegalArgumentException.class,() -> func.insert("string","4.0"));
        //assertThrows(IllegalArgumentException.class,() -> func.insert(2.3,"4.0"));
    }
    @Test
    void test_remove(){
        double[] x ={0.0,1.0,2.0,9.0};
        double[] y ={1.0,2.0,10.0,100.0};
        func= new LinkedListTabulatedFunction(x,y);
        func.remove(1);
        double[] xnew =new double[]{0.0,2.0,9.0};
        double[] ynew =new double[]{1.0,10.0,100.0};
        for(int i=0;i<func.getCount();++i){
            assertEquals(xnew[i],func.getX(i));
            assertEquals(ynew[i],func.getY(i));
        }
    }

    @Test
    void test_setX1() {
        double[] x ={2.0,4.0,5.0,9.0};
        double[] y ={1.0,2.0,10.0,100.0};
        func= new LinkedListTabulatedFunction(x,y);
        func.setX(1,3.0);
        double[] nx ={2.0,3.0,5.0,9.0};
        double[] ny ={1.0,2.0,10.0,100.0};
        ///for(int i=0;i<func.getCount();++i){
        // assertEquals(nx[i],func.getX(i));
        //assertEquals(ny[i],func.getY(i));
        //}
        for(int i=0;i<func.getCount();++i){
            //assertEquals(nx[i],func.getX(i));
            //assertEquals(ny[i],func.getY(i));
            //System.out.print(func.getX(i)+" ");
        }
        System.out.println();
        for(int i=0;i<func.getCount();++i){
            //assertEquals(nx[i],func.getX(i));
            //assertEquals(ny[i],func.getY(i));
            //System.out.print(func.getY(i)+" ");
        }
    }
    @Test
    void test_SetY_valid() {
        func = new LinkedListTabulatedFunction(xVal, yVal);

        // Устанавливаем новое значение y по индексу 2
        func.setY(2, 100.5);
        assertEquals(100.5, func.getNode(2).y);

        // Устанавливаем значение y типом Integer (Number)
        func.setY(1, 50);
        assertEquals(50.0, func.getNode(1).y);
    }
    @Test
    void test_sort() {
        func = new LinkedListTabulatedFunction(xVal, yVal);
        func.sort();                //ловушка заглушки :3
    }
    @Test
    void test_Iterator(){
        func = new LinkedListTabulatedFunction(new double[]{1,2,3},new double[]{2, 4, 6});

        var it = func.iterator();
        double value = 1;
        while (it.hasNext()){
            Point point = it.next();
            assertEquals(point.x(), value);
            assertEquals(point.y(), value*2);
            value += 1;
        }
        assertThrows(NoSuchElementException.class, () -> it.next());
        value = 1;
        for(Point point : func){
            assertEquals(point.x(), value);
            assertEquals(point.y(), value*2);
            value += 1;
        }

    }
}