package concurrent;

import functions.classes.ConstantFunction;
import functions.classes.LinkedListTabulatedFunction;

public class ReadWriteTaskExecutor {
    public static void main(String[] args){

        ConstantFunction cons = new ConstantFunction(-1);
        LinkedListTabulatedFunction list = new LinkedListTabulatedFunction(cons,1,1000,1000);

        Thread Readthread = new Thread(new ReadTask(list));
        Thread Writethread = new Thread(new WriteTask(list,0.5));

        Writethread.start();
        Readthread.start();

    }
}
