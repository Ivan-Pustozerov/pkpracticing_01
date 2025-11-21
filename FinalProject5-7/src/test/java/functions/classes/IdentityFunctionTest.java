package functions.classes;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IdentityFunctionTest{
    protected static IdentityFunction func = new IdentityFunction();

    @Test
    void applyTest(){
        assertEquals(1.0,func.apply(1.0));
    }
}