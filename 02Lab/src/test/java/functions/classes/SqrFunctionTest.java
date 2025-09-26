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
    protected static final String type_out="DOUBLE";
    protected static final String File="/data1-1.csv";

    //в параметризированном тесте мы создаем НОВЫЙ объект sqrFuncTest НА КАЖДОЙ ИТЕРАЦИИ!!!
    @ParameterizedTest
    @CsvFileSource(resources = File,numLinesToSkip = 0)
    public void testApply(ArgumentsAccessor args) {
        h=this.LazyTest(args,type_out);
        assertionTest(h,func.apply(h.nextIn()),type_out);
    }
}