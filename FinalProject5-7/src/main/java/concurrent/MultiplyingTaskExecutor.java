package concurrent;

import functions.classes.LinkedListTabulatedFunction;
import functions.classes.UnitFunction;

import java.util.*;

public class MultiplyingTaskExecutor {
    public static void main(String[] args) throws InterruptedException {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(new UnitFunction(), 1, 1000, 1000);

        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            MultiplyingTask task = new MultiplyingTask(func);
            Thread thread = new Thread(task);
            threads.add(thread);
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        System.out.println("Function is now: " + func);
    }
}
