package SQL.repositories;

import SQL.SQLRepositoryException;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MathFunctionsRepositoryTest {
    private MathFunctionsRepository mf = new MathFunctionsRepository();
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
        url = "jdbc:postgresql://localhost:5432/MfuncTest";
        username = "postgres";
        password = "lkroot";
        connection = DriverManager.getConnection(url,username,password);
        users.initTable(connection);
        users.insertUser(connection,true,"FuncTester",null,new byte[]{12});
        testerId = users.readUserId(connection,new String[]{"FuncTester"},"-").get(0).id();
    }

    @Test
    @Order(1)
    void initTable() {
        assertDoesNotThrow(() -> mf.initTable(connection));
    }

    @Test
    @Order(2)
    void insertMFunc() throws SQLRepositoryException {
        for(int i =0; i<COUNT; ++i){
            String type = (i<COUNT/2)? "analytic" : "tabulated";
            String name = "testFunc" + i;
            assertDoesNotThrow(() -> mf.insertMFunc(connection,type, name, testerId));
        }
        for(int i =0; i<COUNT; ++i){
            fbuffer[i] = users.readUserFunctions(connection,null,new String[]{"FuncTester"}).get(i).id();
        }
    }

    @Test
    @Order(3)
    void updateMFunc() {
        for(int i =0; i<COUNT; ++i){
            long id = fbuffer[i];
            String newname = "newtestFunc" + i;
            assertDoesNotThrow(() -> mf.updateMFunc(connection, testerId, id, newname));
        }
    }

    @Test
    @Order(4)
    void readMFuncInfo() {
        assertDoesNotThrow(() -> mf.readMFuncInfo(connection, fbuffer, "name_asc","asc"));
        assertDoesNotThrow(() -> mf.readMFuncInfo(connection, fbuffer, "type_asc","asc"));
        assertDoesNotThrow(() -> mf.readMFuncInfo(connection, fbuffer, "-","asc"));

        assertDoesNotThrow(() -> mf.readMFuncInfo(connection, fbuffer, "name_desc","desc"));
        assertDoesNotThrow(() -> mf.readMFuncInfo(connection, fbuffer, "type_desc","desc"));
        assertDoesNotThrow(() -> mf.readMFuncInfo(connection, fbuffer, "-","desc"));
    }

    @Test
    @Order(5)
    void removeMFunc() {
        assertDoesNotThrow(() -> users.removeUser(connection,null,"FuncTester"));
    }
}