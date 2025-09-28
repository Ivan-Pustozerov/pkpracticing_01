package functions.classes;
import functions.interfaces.MathFunction;
import functions.interfaces.MathFunction2args;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvFileSource;
import LazyTester.LazyTester;

class RungeKuttaMethodTest extends LazyTester {
    protected static MathFunction2args dfunc = (x,y)->3*new SqrFunction().apply(x);
    protected static RungeKuttaMethod func;
    protected static LazyHolder h;
    protected static final String TYPE_OUT="DOUBLE";
    protected static final String Name="RungeKutta_3x^2";
    protected static final String FILE="/data1-1.csv";
    protected static final double delta=1e-3;
    protected static final double x0=0.0;
    protected static final double y0=0.0;
    protected static final double step=0.00001;


    /// Lamda objects Mathfunction temp =x->x^2;
    ///                     +
    /// Наследование под-тестов от RungeKuttaMethodTest с изменением только имени
    @Test
    public void constructorTest(){

        func=new RungeKuttaMethod(dfunc,x0,y0,step);
        Assertions.assertNotNull(func,"Object not Created!");
    }

    @ParameterizedTest
    @CsvFileSource(resources = FILE,numLinesToSkip = 0)
    public void testApply(ArgumentsAccessor args) {
        h=this.LazyTest(args,TYPE_OUT,Name);
        assertionTest(h,func.apply(h.nextIn()),TYPE_OUT,delta);
    }
    /*@ParameterizedTest
    @CsvFileSource(resources = FILE,numLinesToSkip = 0)
    public void testGetConst(ArgumentsAccessor args) {
        h=this.LazyTest(args,TYPE_OUT,Name);
        func=new ConstantFunction(h.nextIn());
        assertionTest(h,func.getConst(),TYPE_OUT);
    }*/
}