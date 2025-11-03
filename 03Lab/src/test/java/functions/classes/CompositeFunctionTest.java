package functions.classes;
import functions.interfaces.MathFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompositeFunctionTest {
    private final static double delta = 1e-4;

    @Test
    void testDoubleIdentityComposition() {
        MathFunction identity = new IdentityFunction();
        CompositeFunction composite = new CompositeFunction(identity, identity);

        assertEquals(5.0, composite.apply(5.0), delta, "identity(identity(5)) = 5, GOOD");
        assertEquals(-3.0, composite.apply(-3.0), delta, "identity(identity(-3)) = -3, GOOD");
        assertEquals(0.0, composite.apply(0.0), delta, "identity(identity(0)) = 0, GOOD");
    }
    @Test
    void testSqrAndIdentityComposition() {
        MathFunction sqr = new SqrFunction();
        MathFunction identity = new IdentityFunction();
        CompositeFunction sqrThenIdentity = new CompositeFunction(sqr, identity);
        CompositeFunction identityThenSqr = new CompositeFunction(identity, sqr);

        assertEquals(25.0, sqrThenIdentity.apply(5.0), delta, "identity(sqr(5)) = 25, GOOD");
        assertEquals(25.0, identityThenSqr.apply(5.0), delta, "sqr(identity(5)) = 25, GOOD");
        assertEquals(4.0, sqrThenIdentity.apply(-2.0), delta, "identity(sqr(-2)) = 4, GOOD");
        assertEquals(4.0, identityThenSqr.apply(-2.0), delta, "sqr(identity(-2)) = 4,GOOD");
    }
    @Test
    void testDoubleSqrComposition() {
        MathFunction sqr = new SqrFunction();
        CompositeFunction sqrSqr = new CompositeFunction(sqr, sqr);

        assertEquals(625.0, sqrSqr.apply(5.0), delta, "sqr(sqr(5)) = 625, GOOD");
        assertEquals(16.0, sqrSqr.apply(2.0), delta, "sqr(sqr(2)) = 16, GOOD");
        assertEquals(16.0, sqrSqr.apply(-2.0), delta, "sqr(sqr(-2)) = 16, GOOD");
    }
    @Test
    void testTripleComposition() {
        MathFunction sqr = new SqrFunction();
        MathFunction identity = new IdentityFunction();

        CompositeFunction firstLevel = new CompositeFunction(sqr, identity);
        CompositeFunction secondLevel = new CompositeFunction(firstLevel, sqr);
        CompositeFunction thirdLevel = new CompositeFunction(secondLevel, identity);

        assertEquals(625.0, secondLevel.apply(5.0), delta, "sqr(identity(sqr(5))) = 625, GOOD");
        assertEquals(625.0, thirdLevel.apply(5.0), delta, "identity(sqr(identity(sqr(5)))) = 625, GOOD");
        assertEquals(256.0, secondLevel.apply(4.0), delta, "sqr(identity(sqr(4))) = 256, GOOD");
        assertEquals(256.0, thirdLevel.apply(4.0), delta, "identity(sqr(identity(sqr(4)))) = 256, GOOD");
        assertEquals(16.0, secondLevel.apply(2.0), delta, "sqr(identity(sqr(2))) = 16, GOOD");
        assertEquals(16.0, thirdLevel.apply(2.0), delta, "identity(sqr(identity(sqr(2)))) = 16, GOOD");
        assertEquals(0.0, secondLevel.apply(0.0), delta, "sqr(identity(sqr(0))) = 0, GOOD");

    }
    @Test
    void testComplexNestedComposition() {
        MathFunction sqr = new SqrFunction();
        MathFunction identity = new IdentityFunction();

        CompositeFunction level1 = new CompositeFunction(sqr, sqr);
        CompositeFunction level2 = new CompositeFunction(level1, identity);
        CompositeFunction level3 = new CompositeFunction(identity, level2);
        CompositeFunction level4 = new CompositeFunction(level3, sqr);

        assertEquals(625.0, level1.apply(5.0), delta, "sqr(sqr(5)) = 625, GOOD");
        assertEquals(625.0, level2.apply(5.0), delta, "identity(sqr(sqr(5))) = 625, GOOD");
        assertEquals(625.0, level3.apply(5.0), delta, "identity(sqr(sqr(5))) через level3 = 625, GOOD");
        assertEquals(390625.0, level4.apply(5.0), 1.0, "sqr(identity(sqr(sqr(5)))) = 390625, GOOD");
    }

    @Test
    void testArrayWithArrayAndThen(){
        //f(x) = 4x - 5
        double[] x1 = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] y1 = {-5.0, -1.0, 3.0, 7.0, 11.0};
        ArrayTabulatedFunction f = new ArrayTabulatedFunction(x1, y1);

        //g(x) = x + 10
        double[] x2 = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] y2 = {10.0, 11.0, 12.0, 13.0, 14.0};
        ArrayTabulatedFunction g = new ArrayTabulatedFunction(x2, y2);

        MathFunction composition = f.andThen(g);
        // f(g(x)) = 4(x + 10) - 5 = 4x + 35
        assertEquals(35.0, composition.apply(0.0), 1e-8, "f(g(0)) = f(10) = 35, GOOD");
        assertEquals(39.0, composition.apply(1.0), 1e-8, "f(g(1)) = f(11) = 39, GOOD");
        assertEquals(43.0, composition.apply(2.0), 1e-8, "f(g(2)) = f(12) = 43, GOOD");
        assertEquals(47.0, composition.apply(3.0), 1e-8, "f(g(3)) = f(13) = 47, GOOD");
        assertEquals(51.0, composition.apply(4.0), 1e-8, "f(g(3)) = f(14) = 51, GOOD");
    }
    @Test
    void testArrayWithLinkedListAndThen(){
        //f(x) = 4x - 5
        double[] x1 = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] y1 = {-5.0, -1.0, 3.0, 7.0, 11.0};
        ArrayTabulatedFunction f = new ArrayTabulatedFunction(x1, y1);

        //g(x) = x + 10
        double[] x2 = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] y2 = {10.0, 11.0, 12.0, 13.0, 14.0};
        LinkedListTabulatedFunction g = new LinkedListTabulatedFunction(x2, y2);

        MathFunction composition = f.andThen(g);
        // f(g(x)) = 4(x + 10) - 5 = 4x + 35
        assertEquals(35.0, composition.apply(0.0), 1e-8, "f(g(0)) = f(10) = 35, GOOD");
        assertEquals(39.0, composition.apply(1.0), 1e-8, "f(g(1)) = f(11) = 39, GOOD");
        assertEquals(43.0, composition.apply(2.0), 1e-8, "f(g(2)) = f(12) = 43, GOOD");
        assertEquals(47.0, composition.apply(3.0), 1e-8, "f(g(3)) = f(13) = 47, GOOD");
        assertEquals(51.0, composition.apply(4.0), 1e-8, "f(g(3)) = f(14) = 51, GOOD");
    }

    @Test
    void testLinkedListWithLinkedListAndThen(){
        //f(x) = 4x - 5
        double[] x1 = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] y1 = {-5.0, -1.0, 3.0, 7.0, 11.0};
        LinkedListTabulatedFunction f = new LinkedListTabulatedFunction(x1, y1);

        //g(x) = x + 10
        double[] x2 = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] y2 = {10.0, 11.0, 12.0, 13.0, 14.0};
        LinkedListTabulatedFunction g = new LinkedListTabulatedFunction(x2, y2);

        MathFunction composition = f.andThen(g);
        // f(g(x)) = 4(x + 10) - 5 = 4x + 35
        assertEquals(35.0, composition.apply(0.0), 1e-8, "f(g(0)) = f(10) = 35, GOOD");
        assertEquals(39.0, composition.apply(1.0), 1e-8, "f(g(1)) = f(11) = 39, GOOD");
        assertEquals(43.0, composition.apply(2.0), 1e-8, "f(g(2)) = f(12) = 43, GOOD");
        assertEquals(47.0, composition.apply(3.0), 1e-8, "f(g(3)) = f(13) = 47, GOOD");
        assertEquals(51.0, composition.apply(4.0), 1e-8, "f(g(3)) = f(14) = 51, GOOD");
    }

    @Test
    void testLinkedListWithArrayAndThen(){
        //f(x) = 4x - 5
        double[] x1 = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] y1 = {-5.0, -1.0, 3.0, 7.0, 11.0};
        LinkedListTabulatedFunction f = new LinkedListTabulatedFunction(x1, y1);

        //g(x) = x + 10
        double[] x2 = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] y2 = {10.0, 11.0, 12.0, 13.0, 14.0};
        ArrayTabulatedFunction g = new ArrayTabulatedFunction(x2, y2);

        MathFunction composition = f.andThen(g);
        // f(g(x)) = 4(x + 10) - 5 = 4x + 35
        assertEquals(35.0, composition.apply(0.0), 1e-8, "f(g(0)) = f(10) = 35, GOOD");
        assertEquals(39.0, composition.apply(1.0), 1e-8, "f(g(1)) = f(11) = 39, GOOD");
        assertEquals(43.0, composition.apply(2.0), 1e-8, "f(g(2)) = f(12) = 43, GOOD");
        assertEquals(47.0, composition.apply(3.0), 1e-8, "f(g(3)) = f(13) = 47, GOOD");
        assertEquals(51.0, composition.apply(4.0), 1e-8, "f(g(3)) = f(14) = 51, GOOD");
    }
}