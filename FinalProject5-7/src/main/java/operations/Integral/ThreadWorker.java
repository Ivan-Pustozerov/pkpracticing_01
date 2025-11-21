package operations.Integral;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Рабочий поток с конвейером для выполнения задач типа Runnable
 * Основан на блокирующей очереди
 * @see Runnable
 * @see LinkedBlockingQueue
 */
public class ThreadWorker extends Thread{
///==================================Служебное=============================
    private final LinkedBlockingQueue<Runnable> Tasks; // Очередь задач
    private volatile boolean running = true;          // Активность потока
///========================================================================

///--------------------------------Конструкторы---------------------------------------
    /**
     * Конструктор с параметром
     * @param Tasks Подготовленный список задач
     */
    public ThreadWorker(LinkedBlockingQueue<Runnable> Tasks){
        this.Tasks = Tasks;
    }

    /**
     * Конструктор по умолчанию
     */
    public ThreadWorker(){
        this.Tasks = new LinkedBlockingQueue<>();
    }

///------------------------------------Функционал--------------------------------------
    /**
     * Основной цикл работы
     * По возможности выполняет следующую задачу из очереди
     * Ошибки прерывания игнорирует, только если активен
     */
    @Override
    public void run(){
        while(running){
            try{
                Runnable task = Tasks.take();
                task.run();
            }
            catch(InterruptedException e) {
                if (!running) break;
                e.printStackTrace();
            }
        }

    }

    /**
     * Добавляет задачу в очередь потока
     * @param task Задача типа Runnable
     * @throws InterruptedException в случае неудачи
     */
    public void addTask(Runnable task) throws InterruptedException{
        Tasks.put(task);
    }

    /**
     * Завершение работы потока.
     * Вызывается interrupt()
     */
    public void shutdown(){
        running = false;
        this.interrupt();
    }

}
