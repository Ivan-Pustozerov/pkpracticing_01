package functions.classes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionTest {

    private double[] xVal = {1, 2, 3, 4, 5};
    private double[] yVal = {10, 20, 30, 40, 50};
    private double delta =1e-32;
    private LinkedListTabulatedFunction func;
    @Test
    void getHead() {
        func = new LinkedListTabulatedFunction(xVal, yVal);

        Node head = func.getHead();

        assertNotNull(head);                       // Проверяем, что голова не null
        assertEquals(1, head.x);          // Проверяем значение x у головы
        assertEquals(10, head.y);
    }
    @Test
    public void testConstructorRangeRage() {
        double[] xVal = {1.0, 2.0};
        double[] yVal = {3.0};

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new LinkedListTabulatedFunction(xVal, yVal)
        );

        assertEquals("Массивам необходимо быть одинаковой длины", exception.getMessage());
    }

    @Test
    public void testConstructorNormal() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(new IdentityFunction(), 1, 5, 5);
        Node current = func.getHead();

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
    public void testConstructorSwapsRange() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(new IdentityFunction(), 5, 1, 5);
        Node head = func.getHead();

        assertNotNull(head);
        assertEquals(1, head.x, 1e-9);

    }
    @Test
    public void testConstructorSinglePoint() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(new IdentityFunction(), 3, 3, 4);
        Node head = func.getHead();

        assertNotNull(head);
        int count = 0;
        Node current = head;
        do {
            assertEquals(3, current.x, 1e-9);
            assertEquals(3, current.y, 1e-9);
            current = current.next;
            count++;
        } while (current != head && count < 10);

        assertEquals(4, count);
    }
    @Test
    void testFloorIndexOfX_validInput() {
        func = new LinkedListTabulatedFunction(xVal, yVal);

        // x меньше головы (1)
        assertEquals(0, func.floorIndexOfX(0.5));

        // x равен голове (1)
        assertEquals(0, func.floorIndexOfX(1.0));

        // x в середине массива (3)
        assertEquals(2, func.floorIndexOfX(3.0));

        // x больше последнего значения (5)
        assertEquals(xVal.length, func.floorIndexOfX(6.0));
    }

    @Test
    void testFloorIndexOfX_nullOrNonNumber() {
        func = new LinkedListTabulatedFunction(xVal, yVal);

        // Передан null
        IllegalArgumentException exNull = assertThrows(IllegalArgumentException.class, () -> func.floorIndexOfX(null));
        assertEquals("NEED NUMBER!!!", exNull.getMessage());

        // Передан объект не Number
        IllegalArgumentException exStr = assertThrows(IllegalArgumentException.class, () -> func.floorIndexOfX("string"));
        assertEquals("NEED NUMBER!!!", exStr.getMessage());
    }

    @Test
    void testExtrapolateLeft_singleElement() {
        double[] xValSingle = {1};
        double[] yValSingle = {10};
        func = new LinkedListTabulatedFunction(xValSingle, yValSingle);

        // При count == 1 возвращается y головы вне зависимости от x
        assertEquals(10, func.extrapolateLeft(0.5));
        assertEquals(10, func.extrapolateLeft(1.0));
    }

    @Test
    void testExtrapolateLeft_multipleElements() {
        func = new LinkedListTabulatedFunction(xVal, yVal);

        // extrapolateLeft для x меньше минимального x, проверяем вызов interpolate с индексом 0
        // Предполагаемая реализация interpolate: для теста можно проверить, что значение не равно head.y, а рассчитано корректно.
        double result = func.extrapolateLeft(0);
        // Можно сравнить результат с вызовом interpolate вручную, если он доступен, либо просто проверить что результат не равен head.y
        assertNotEquals(yVal[0], result);
    }

    @Test
    void testExtrapolateRight_singleElement() {
        double[] xValSingle = {1};
        double[] yValSingle = {10};
        func = new LinkedListTabulatedFunction(xValSingle, yValSingle);

        // При count == 1 возвращается y головы
        assertEquals(10, func.extrapolateRight(2.0));
        assertEquals(10, func.extrapolateRight(1.0));
    }

    @Test
    void testExtrapolateRight_multipleElements() {
        func = new LinkedListTabulatedFunction(xVal, yVal);

        // extrapolateRight для x больше максимального x, проверяем вызов interpolate с индексом count - 2
        double result = func.extrapolateRight(6);
        assertNotEquals(yVal[yVal.length - 1], result);
    }

    @Test
    void interpolate() {

    }

    @Test
    void getCount() {
        double[] xVal = {1, 2, 3};
        double[] yVal = {4, 5, 6};
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xVal, yVal);

        assertEquals(3, func.getCount());
    }
    @Test
    public void testGetXY() {
        double[] xVal = {10, 20, 30};
        double[] yVal = {100, 200, 300};
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xVal, yVal);
        for (int i = 0; i < func.getCount(); i++) {
            assertEquals(xVal[i], func.getX(i), 1e-9);
            assertEquals(yVal[i], func.getY(i), 1e-9);
        }
    }
    @Test
    void testSetY_validAndInvalid() {
        func = new LinkedListTabulatedFunction(xVal, yVal);

        // Устанавливаем новое значение y по индексу 2
        func.setY(2, 100.5);
        assertEquals(100.5, func.getNode(2).y);

        // Устанавливаем значение y типом Integer (Number)
        func.setY(1, 50);
        assertEquals(50.0, func.getNode(1).y);

        // Передаем не число - должно выбросить исключение
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> func.setY(0, "not a number"));
        assertEquals("Y - не число", ex.getMessage());
    }
    @Test
    public void testLeftBound() {
        double[] xVal = {1.5, 2.5};
        double[] yVal = {4.0, 5.0};
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xVal, yVal);
        assertEquals(1.5, func.leftBound());
    }

    @Test
    void rightBound() {
        double[] xVal = {1.5, 2.5};
        double[] yVal = {4.0, 5.0};
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xVal, yVal);
        assertEquals(2.5, func.rightBound());

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

        // Передача объекта, не являющегося числом
        assertEquals(-1, func.indexOfY("строка"));
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

        // Передача значения не числа
        assertEquals(-1, func.indexOfX("строка"));
    }

    @Test
    void testFloorNodeOfX() {
                                                             // x меньше головы - возвращается голова
        func = new LinkedListTabulatedFunction(
                new double[]{1, 3, 5, 7, 9},
                new double[]{10, 30, 50, 70, 90});
        Node head = func.getHead();
        assertEquals(head, func.floorNodeOfX(0.5));

                                                              // x больше последнего узла - возвращается последний узел
        Node last = head.prev;                                // предположим, что prev последнего - это последний узел
        assertEquals(last, func.floorNodeOfX(10));

                                                                // x находится между узлами, тестируем возвращаемый узел
        assertEquals(head.next, func.floorNodeOfX(3.5));    // 3.5 между 3 и 5, ожидается узел с x=3
        assertEquals(head.next.next, func.floorNodeOfX(5)); // x = 5, между 5 и 7, возвращается узел 5

                                                               // x равен одному из узлов
        Node node7 = head.next.next.next;
        assertEquals(node7, func.floorNodeOfX(7));
    }
    @Test
    void testApply() {
        func = new LinkedListTabulatedFunction(
                new double[]{1, 2, 3, 4, 5},
                new double[]{10, 20, 30, 40, 50}
        );
        // Значение X меньше левой границы - вызывается extrapolateLeft
        double xLeft = 0.5;
        double expectedLeft = func.extrapolateLeft(xLeft);
        assertEquals(expectedLeft, func.apply(xLeft));

        // Значение X больше правой границы - вызывается extrapolateRight
        double xRight = 6.0;
        double expectedRight = func.extrapolateRight(xRight);
        assertEquals(expectedRight, func.apply(xRight));

        // Значение X внутри границ, ровно равное одному из узлов - возвращается y этого узла
        double exactX = 3.0;
        double expectedExact = 30.0;
        assertEquals(expectedExact, func.apply(exactX), 1e-10);

        // Передача объекта не Number - возвращается NaN
        assertTrue(Double.isNaN(func.apply("not a number")));
    }

    @Test
    void sort() {
    }
}