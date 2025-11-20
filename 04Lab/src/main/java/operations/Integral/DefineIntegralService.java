package operations.Integral;

import exceptions.DifferentLengthOfArraysException;
import functions.interfaces.MathFunction;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Сервис для вычисления определенного интеграла
 * Для заданного списка функций, с использованием многопоточности.
 * Реализован с использованием шаблона Singleton.
 * Класс и методы <></><b>потокобезопасны</b>.
 * Может быть использован в сервисных приложениях.
 *
 * Используется Умный поток:
 * @see ThreadWorker
 *
 * Сервис поддерживает расширения - пример:
 * @see TrapezoidDefineIntegralTaskXY
 * @see TrapezoidDefineIntegralXY
 */
@ThreadSafe
public class DefineIntegralService {
///=================================Служебные===========================================================
    private final ThreadWorker[] ThreadPool;                         // Пул потоков
    private final List<MathFunction> FuncList;                      // список функций сервиса
    private volatile static DefineIntegralService instance = null; // реализация sigleton
///=====================================================================================================

///-----------------------------------Методы------------------------------------------------------------
    /**
     * Вычисляет определенный интеграл методом трапеций для заданной по индексу функции.
     * @param FuncIndex Индекс функции в списке функций
     * @param Ndot Количество интервалов интегрирования
     * @param from Нижний предел интегрирования
     * @param to Верхний предел интегрирования
     * @return значение определенного интеграла
     */
    public double TrapezoidMethod(int FuncIndex, int Ndot, double from, double to){
        if(FuncIndex <0 || FuncIndex >= FuncList.size()) throw new IllegalArgumentException("Index Out Of Range");
        try{
            return TrapezoidDefineIntegralXY.apply(ThreadPool,FuncList.get(FuncIndex),Ndot,from,to);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Вычисляет определенные интегралы методом трапеций для всех функций.
     * @param Ndot Массив количеств интервалов интергрирования
     * @param from Массив нижних пределов интергрирования
     * @param to Массив верхних пределов интегрирования
     * @return массив значений определенных интегралов по индексам в списке функций
     */
    public double[] TrapezoidMethodToALL(int[] Ndot, double[] from, double[] to){
        int funcLen =FuncList.size();
        if(Ndot.length != funcLen
                || from.length != funcLen
                || to.length != funcLen)
            throw new DifferentLengthOfArraysException();

        double[] result = new double[funcLen];
        for(int i =0; i<funcLen;++i){
            result[i] = TrapezoidDefineIntegralXY.apply(ThreadPool,FuncList.get(i),Ndot[i],from[i],to[i]);
        }
        return result;
    }

///-------------------------------------Функционал------------------------------------------------------
    /**
     * Добавляет Математическую функцию в память Сервиса.
     * @param func Математическая функция
     */
    public void appendFunc(MathFunction func){
        FuncList.add(func);
    }

    /**
     * Удаляет Математическую функцию по индексу из памяти Сервиса.
     * @param index Индекс функции в памяти Сервиса
     * @return Удаленная Математическая функция
     */
    public MathFunction deleteFunc(int index){
        return FuncList.remove(index);
    }

    /**
     * Останавливает работу сервиса.
     * Очищает пул потопов.
     */
    public void shutdown(){
        for(ThreadWorker thread : ThreadPool){
            if(thread != null)
                thread.shutdown();
        }
    }

///----------------------------------Конструкторы--------------------------------------------------------
    /**
     * Создает Сервис или возвращает ссылку на него через конструктор с аргументами.
     * @param Funcs выбранный набор функций
     * @param ThreadNum выбранное количество потоков
     * @return ссылка на объект сервиса
     */
    public static DefineIntegralService getInstance(LinkedList<MathFunction> Funcs, int ThreadNum) {
        if (instance == null){
            synchronized (DefineIntegralService.class) {
                if (instance == null) instance = new DefineIntegralService(Funcs, ThreadNum);
            }
        }
        return instance;
    }

    /**
     * Создает Сервис или возвращает ссылку на него через конструктор по умолчанию.
     * @return ссылка на объект сервиса
     */
    public static DefineIntegralService getInstance() {
        if( instance == null){
            synchronized (DefineIntegralService.class){
                if( instance == null) instance = new DefineIntegralService();
            }
        }
        return instance;
    }

    /**
     * Приватный конструктор с параметрами
     * @param Funcs выбранный набор функций
     * @param ThreadNum выбранное количество потоков
     */
    private DefineIntegralService(LinkedList<MathFunction> Funcs, int ThreadNum){
        this.FuncList = Collections.synchronizedList(Funcs);
        this.ThreadPool = new ThreadWorker[ThreadNum];
        for(int i=0; i<ThreadPool.length;++i){
            ThreadPool[i] = new ThreadWorker();
            ThreadPool[i].start();
        }
    }

    /**
     *  Приватный конструктор по умолчанию.
     *  Создает кол-во потоков = максимальному количеству доступных ядер.
     */
    private DefineIntegralService(){
        int ThreadNum = Runtime.getRuntime().availableProcessors();
        this.FuncList = Collections.synchronizedList(new LinkedList<>());
        this.ThreadPool = new ThreadWorker[ThreadNum];
        for(int i=0; i<ThreadPool.length;++i){
            ThreadPool[i] = new ThreadWorker();
            ThreadPool[i].start();
        }
    }
}

