package operations.Integral;

import functions.interfaces.MathFunction;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.DoubleAdder;


/**
 * Метод вычисления Определенного интеграла методом трапеций.
 * Вычисляет интеграл на промежутке A-B с заданной точностью N.
 * Основан на использовании стека для обеспечения потокабезопасности
 * Результат возвращает через DoubleAdder
 * Объект не может быть создан
 * @see MathFunction
 * @see DoubleAdder
 */
@ThreadSafe
public class TrapezoidDefineIntegralXY{
    /**
     * Пустой приватный конструктор
     */
    private TrapezoidDefineIntegralXY(){}

    /**
     * Возвращает результат опеределенного интеграла по методу трапеций
     * @param ThPool Пул потоков
     * @param func Математическая функция
     * @param N Количество интервалов интегрирования
     * @param A Нижний предел интегрирования
     * @param B Верхний предел интегрирования
     * @return численное значение определенного интеграла
     * @param <T> Тип математической функции, наследуемый от MathFunction.
     * @throws InterruptedException
     */
    public static <T extends MathFunction> double apply(ThreadWorker[] ThPool, T func, int N, double A, double B) throws InterruptedException {
        if(N <0)throw new IllegalArgumentException("N should be positive");

        DoubleAdder result = new DoubleAdder();
        CountDownLatch latch = new CountDownLatch(ThPool.length);

        int segmentsPerThread = N / ThPool.length;
        int segmentsRemain = N % ThPool.length;
        double DX = (B-A)/(ThPool.length);
        double X1 = A;
        double X2 = A+DX;

        for (ThreadWorker threadWorker : ThPool) {
            int Segment = segmentsPerThread + ((segmentsRemain > 0)? 1 : 0);
            threadWorker.addTask(new TrapezoidDefineIntegralTaskXY<T>(func, result, Segment, X1, X2, latch));
            X1 = X2;
            X2 += DX;
            --segmentsRemain;
        }
        try{
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Calculation interrupted");
        }
        return result.doubleValue();
    }
}
