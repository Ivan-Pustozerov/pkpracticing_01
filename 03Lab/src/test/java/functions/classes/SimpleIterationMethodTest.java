package functions.classes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleIterationMethodTest {
    @Test
    void Test1() {
        double epsilon = 1e-6;
        int maxIter = 100;
        double[][] matA = {{4.0, 1.0}, {2.0, 3.0}};
        double[] vecB = {1.0, 2.0};
        double[] approx = {0.0, 0.0};
        SimpleIterationMethod method = new SimpleIterationMethod(epsilon, maxIter, matA, vecB, approx);
    }
    @Test
    void apply1() {
        double epsilon = 1e-6;
        int maxIter = 10;
        double[][] matA = {{1.0}};              //Да,крутая система:D
        double[] vecB = {1.0};
        double[] approx = {0.0};
        SimpleIterationMethod method = new SimpleIterationMethod(epsilon, maxIter, matA, vecB, approx);

        double result = method.apply(0);
        assertEquals(1.0, result, 1e-8, "Apply должен вернуть значение приближения по индексу 0");
    }
    @Test
    void apply2() {
        double epsilon = 1e-6;
        int maxIter = 100;
        double[][] matA = {{1.0,3.0},{2.0,1.0}};        //не лин-дом.
        double[] vecB = {5.0,5.0};
        double[] approx = {0.0,0.0};
        SimpleIterationMethod method = new SimpleIterationMethod(epsilon, maxIter, matA, vecB, approx);

        double result1 = method.apply(0);
        double result2 = method.apply(1);
        assertEquals(2.0, result1, epsilon, "Apply должен вернуть значение приближения по индексу 0");
        assertEquals(1.0, result2, epsilon, "Apply должен вернуть значение приближения по индексу 0");
    }
}