package operations.Integral;

import functions.interfaces.MathFunction;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * Задача для вычисления Определенного интеграла методом трапеции для малого промежутка
 * @param <T> тип математической функции
 */
@ThreadSafe
public class TrapezoidDefineIntegralTaskXY<T extends MathFunction> implements Runnable {
///=============================Служебное===============================================
    private final DoubleAdder result;    // результат на промежутке
    private final CountDownLatch Latch; // Защелка для синхронизации задач
    private final T func;              // Функция
    private final int N;              // Количество точек на интервале
    private final double A;          // Нижний предел интегрирования
    private final double B;         // Верхний предел интегрирования
///=====================================================================================

    /**
     * Конструктор с параметрами
     * @param function Функция
     * @param res Будущий результат - глобальная переменная
     * @param Ndot Количество интервалов
     * @param XStart Нижний предел интегрирования
     * @param XEnd Верхний предел интегрирования
     * @param latch Защелка - нужна для синхронизации на уровне вызова
     */
    public TrapezoidDefineIntegralTaskXY(T function, DoubleAdder res, int Ndot, double XStart, double XEnd, CountDownLatch latch){
        result=res;
        func = function;
        N = Ndot;
        A = XStart;
        B = XEnd;
        Latch = latch;
    }

    /**
     * Основной цикл работы
     */
    @Override
    public void run(){
        double dx = (B-A)/N;
        double x1 = A;
        double x2 = A+dx;
        double buffer = 0.0;
        for(int i=0; i<N; ++i){
            double f1 = func.apply(x1);
            double f2 = func.apply(x2);
            buffer += ( (f1 + f2) * dx )/ 2;
            x1=x2; x2+=dx;
        }
        result.add(buffer);
        Latch.countDown();
    }
}
