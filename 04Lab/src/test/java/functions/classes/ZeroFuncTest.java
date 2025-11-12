package functions.classes;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ZeroFuncTest{
    protected static ZeroFunction func;

    @Test
    void ConstructorTest(){
        func =new ZeroFunction();
        assertNotNull(func);
    }
    @Test
    void getConstTest(){
        func = new ZeroFunction();
        assertEquals(0.0,func.getConst());
    }
    @Test
    void applyTest(){
        func =new ZeroFunction();
        assertEquals(0.0,func.apply(2));
    }


}