package functions.classes;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvFileSource;
import LazyTester.LazyTester;

class IdentityFunctionTest extends LazyTester {
    protected static IdentityFunctionTest func = new IdentityFunctionTest();
    protected static LazyHolder h;
    protected static final String TYPE_OUT="DOUBLE";
    protected static final String Name="IdenFuncTest";
    protected static final String FILE="/data1-1.csv";


    @ParameterizedTest
    @CsvFileSource(resources = FILE,numLinesToSkip = 0)
    public void testApply(ArgumentsAccessor args) {
        h=this.LazyTest(args,TYPE_OUT,Name);
        assertionTest(h,func.apply(h.nextIn()),TYPE_OUT);
    }
}