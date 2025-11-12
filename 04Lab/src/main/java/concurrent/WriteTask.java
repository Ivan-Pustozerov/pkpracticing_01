package concurrent;

import functions.interfaces.TabulatedFunction;

public class WriteTask implements Runnable {
    private TabulatedFunction tfunc;
    private double value;

    public WriteTask(TabulatedFunction tfunc, double value) {
        this.tfunc = tfunc;
        this.value = value;
    }

    @Override
    public void run() {
        for (int i = 0; i < tfunc.getCount(); i++) {
            synchronized (tfunc) {
                tfunc.setY(i, value);
                System.out.printf("Writing for index %d complete\n", i);
            }
        }
    }
}
