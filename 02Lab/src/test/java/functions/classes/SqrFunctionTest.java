package functions.classes;
import functions.interfaces.MathFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvFileSource;

class SqrFunctionTest extends LazyTester{
    protected static SqrFunction func=new SqrFunction();
    protected static LazyHolder h;
    protected static final String TYPE_OUT="DOUBLE";
    protected static final String Name="SqrFuncTest";
    protected static final String FILE="/data1-1.csv";

    //в параметризированном тесте мы создаем НОВЫЙ объект sqrFuncTest НА КАЖДОЙ ИТЕРАЦИИ!!!
    @ParameterizedTest
    @CsvFileSource(resources = FILE,numLinesToSkip = 0)
    public void testApply(ArgumentsAccessor args) {
        h=this.LazyTest(args,TYPE_OUT,Name);
        assertionTest(h,func.apply(h.nextIn()),TYPE_OUT);
    }
}