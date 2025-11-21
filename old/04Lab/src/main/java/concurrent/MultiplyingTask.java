package concurrent;
import functions.interfaces.TabulatedFunction;
public class MultiplyingTask implements Runnable {
    private TabulatedFunction tfunc;

    public MultiplyingTask(TabulatedFunction function) {
        this.tfunc = function;
    }

    @Override
    public void run() {
        for (int i = 0; i < tfunc.getCount(); i++) {
            synchronized (tfunc) {
                tfunc.setY(i, tfunc.getY(i) * 2);
            }
        }
        System.out.println("Thread " + Thread.currentThread().getName() + " finished MultiplyingTask");
    }
}
