package functions.classes;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvFileSource;
import LazyTester.LazyTester;

class ConstantFunctionTest extends LazyTester {
    protected static ConstantFunction func;
    protected static LazyHolder h;
    protected static final String TYPE_OUT="DOUBLE";
    protected static final String Name="ConstFunctionTest";
    protected static final String FILE="/data1-1.csv";

    @ParameterizedTest
    @CsvFileSource(resources = FILE,numLinesToSkip = 0)
    public void testApply(ArgumentsAccessor args) {
        h=this.LazyTest(args,TYPE_OUT,Name);
        func=new ConstantFunction(h.nextIn());
        assertionTest(h,func.apply(h.reset().nextIn()),TYPE_OUT);
    }
    @ParameterizedTest
    @CsvFileSource(resources = FILE,numLinesToSkip = 0)
    public void testGetConst(ArgumentsAccessor args) {
        h=this.LazyTest(args,TYPE_OUT,Name);
        func=new ConstantFunction(h.nextIn());
        assertionTest(h,func.getConst(),TYPE_OUT);
    }
}
