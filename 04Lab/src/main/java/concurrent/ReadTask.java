package concurrent;

import functions.interfaces.TabulatedFunction;

public class ReadTask implements Runnable{
    private TabulatedFunction tfunc;

    public ReadTask(TabulatedFunction tfunc) {
        this.tfunc = tfunc;
    }

    @Override
    public void run(){
        for (int i=0;tfunc.getCount()>i;i++) {
            synchronized (tfunc) {
                System.out.printf("After read: i = %d, x = %f, y = %f\n", i, tfunc.getX(i), tfunc.getY(i));
            }
        }
    }
}
