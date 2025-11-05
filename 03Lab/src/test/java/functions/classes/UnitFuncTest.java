package functions.classes;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UnitFuncTest{
    protected static UnitFunction func;

    @Test
    void ConstructorTest(){
        func =new UnitFunction();
        assertNotNull(func);
    }
    @Test
    void getConstTest(){
        func = new UnitFunction();
        assertEquals(1.0,func.getConst());
    }
    @Test
    void applyTest(){
        func =new UnitFunction();
        assertEquals(1.0,func.apply(2));
    }


}