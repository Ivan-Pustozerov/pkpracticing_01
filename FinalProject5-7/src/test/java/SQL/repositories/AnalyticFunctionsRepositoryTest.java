package SQL.repositories;

import SQL.SQLRepositoryException;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnalyticFunctionsRepositoryTest {

    private AnalyticFunctionsRepository af = new AnalyticFunctionsRepository();
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
        url = "jdbc:postgresql://localhost:5432/AfuncTest";
        username = "postgres";
        password = "lkroot";
        connection = DriverManager.getConnection(url,username,password);
        users.initTable(connection);
        users.insertUser(connection,true,"FuncTester",null,new byte[]{12});
        testerId = users.readUserId(connection,new String[]{"FuncTester"},"-").get(0).id();
        mf.initTable(connection);
        for(int i = 0; i<COUNT;++i) {mf.insertMFunc(connection,"analytic", "analyticTest", testerId);}
        for(int i =0; i<COUNT; ++i){
            fbuffer[i] = users.readUserFunctions(connection,null,new String[]{"FuncTester"}).get(i).id();
        }
    }


    @Test
    @Order(1)
    void initTable() {
        assertDoesNotThrow(() -> af.initTable(connection));
    }

    @Test
    @Order(2)
    void insertAnalyticFunction() {
        for(int i =0; i<COUNT; ++i){
            long id = fbuffer[i];
            String func_expr = "x^2 + " + i;
            assertDoesNotThrow(() -> af.insertAnalyticFunction(connection,id, func_expr));
        }
    }

    @Test
    @Order(3)
    void updateAnalyticFunction() {
        for(int i =0; i<COUNT; ++i){
            long id = fbuffer[i];
            String func_expr = "x^2 - " + i;
            assertDoesNotThrow(() -> af.updateAnalyticFunction(connection,id, func_expr));
        }
    }

    @Test
    @Order(4)
    void readAnalyticFunctionInfo() {
        assertDoesNotThrow(() -> af.readAnalyticFunctionInfo(connection,fbuffer));
    }

    @AfterAll
    static void clean() throws SQLRepositoryException {
        users.removeUser(connection,null,"FuncTester");
    }
}