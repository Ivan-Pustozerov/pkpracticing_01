package SQL.repositories;

import SQL.repositories.tools.SQLRepositoryException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionsRepositoryTest {
    private TabulatedFunctionsRepository tf = new TabulatedFunctionsRepository();
    private static MathFunctionsRepository mf = new MathFunctionsRepository();
    private static UsersRepository users = new UsersRepository();
    private static Connection connection;
    private static String url;
    private static String username;
    private static String password;
    private static final int COUNT = 10;
    private static long testerId;
    private static long[] fbuffer = new long[COUNT];
    @BeforeAll
    static void setup() throws SQLException {
        url = "jdbc:postgresql://localhost:5432/TfuncTest";
        username = "postgres";
        password = "lkroot";
        connection = DriverManager.getConnection(url,username,password);
        users.initTable(connection);
        users.insertUser(connection,true,"FuncTester",null,new byte[]{12});
        testerId = users.readUserId(connection,new String[]{"FuncTester"},"-").get(0).id();
        mf.initTable(connection);
        for(int i = 0; i<COUNT;++i) {mf.insertMFunc(connection,"tabulated", "tabulatedTest", testerId);}
        for(int i =0; i<COUNT; ++i){
            fbuffer[i] = users.readUserFunctions(connection,null,new String[]{"FuncTester"}).get(i).id();
        }
    }


    @Test
    @Order(1)
    void initTable() {
        assertDoesNotThrow(() -> tf.initTable(connection));
    }

    @Test
    @Order(2)
    void insertTabulatedFunction() {
        for(int i =0; i<COUNT; ++i){
            long id = fbuffer[i];
            double[] xVals= new double[]{1,2,3};
            double[] yVals= new double[]{0,0,0};
            assertDoesNotThrow(() -> tf.insertTabulatedFunction(connection,id, xVals, yVals));
        }
    }

    @Test
    @Order(3)
    void updateTabulatedFunction() {
        for(int i =0; i<COUNT; ++i){
            long id = fbuffer[i];
            double[] xVals= new double[]{1,2,3};
            assertDoesNotThrow(() -> tf.updateTabulatedFunctionFull(connection,id, xVals, xVals));
        }
        for(int i =0; i<COUNT; ++i){
            long id = fbuffer[i];
            int yind = 1;
            double yval = -5;
            assertDoesNotThrow(() -> tf.updateTabulatedFunctionIndex(connection,id, yind, yval));
            assertDoesNotThrow(() -> tf.updateTabulatedFunctionIndex(connection,id, yind, Double.NaN));
        }

    }

    @Test
    @Order(4)
    void readTabulatedFunctionInfo() {
        assertDoesNotThrow(() -> tf.readTabulatedFunctionInfo(connection,fbuffer));
    }

    @AfterAll
    static void clean() throws SQLRepositoryException {
        users.removeUser(connection,null,"FuncTester");
    }
}