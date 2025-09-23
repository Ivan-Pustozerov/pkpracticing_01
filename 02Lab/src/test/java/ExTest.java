import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

public class ExTest {

    private Ex.A obj;
    @ParameterizedTest
    @CsvSource({"4,5","7,7"})
    public void getATest(int a, int result){
        obj = new Ex.A(a);
        Assertions.assertEquals(result,obj.getA());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data.csv",numLinesToSkip = 0)
    void getDoubleATest(int a, int result) {
        obj = new Ex.A(a);
        Assertions.assertEquals(result,obj.getDoubleA());
    }
}