package functions.classes;
import functions.interfaces.MathFunction2args;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class RungeKuttaMethodTest{
    protected static MathFunction2args dfunc = new MathFunction2args() {
        @Override
        public <T extends Number> double apply(T x, T y) {
            return 3 * new SqrFunction().apply(x);
        }
    };

    protected static RungeKuttaMethod func;
    protected static final double delta=1e-3;
    protected static final double x0=0.0;
    protected static final double y0=0.0;
    protected static final double step=0.00001;

    @Test
    public void constructorTest(){
        func=new RungeKuttaMethod(dfunc,x0,y0,step);
        Assertions.assertNotNull(func,"Object not Created!");
    }

    @Test
    public void applyTest(){
        double[][] data= {
        {1.0, 1.0},
        {2.0, 8.0},
        {3.0, 27.0},
        {3.4, 39.304},
        {2.8, 21.952},
        {0.0, 0.0},
        {0.01, 1e-6},
        {-1.0, -1.0},
        {-2.0, -8.0},
        {-2.1, -9.261}
        };
        for(double[] p : data){
            assertEquals(p[1],func.apply(p[0]));
        }
    }
}