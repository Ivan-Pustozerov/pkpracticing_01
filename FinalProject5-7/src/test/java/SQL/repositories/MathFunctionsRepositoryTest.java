package SQL.repositories;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
    void readMFuncInfo() throws SQLRepositoryException {

        /*var array =mf.readMFuncInfo(connection, fbuffer, "type_desc","desc");
        for(var elem : array){
            System.out.println(elem+"/n");
        }*/

        assertDoesNotThrow(() -> mf.readMFuncInfo(connection, fbuffer, "name_asc","asc"));
        assertDoesNotThrow(() -> mf.readMFuncInfo(connection, fbuffer, "type_asc","asc"));
        assertDoesNotThrow(() -> mf.readMFuncInfo(connection, fbuffer, "-","asc"));

        assertDoesNotThrow(() -> mf.readMFuncInfo(connection, fbuffer, "name_desc","desc"));
        assertDoesNotThrow(() -> mf.readMFuncInfo(connection, fbuffer, "type_desc","desc"));
        assertDoesNotThrow(() -> mf.readMFuncInfo(connection, fbuffer, "-","desc"));
    }

    @Test
    @Order(5)
    void readMFuncInfoByOwnerId() throws SQLRepositoryException {
        /*var array =mf.readMFuncInfoByOwnerId(connection,testerId,"-","-");
        for(var elem : array){
            System.out.println(elem+"/n");
        }*/
        assertDoesNotThrow(()-> mf.readMFuncInfoByOwnerId(connection,testerId,"-","-"));
    }

    @Test
    @Order(6)
    void readMFuncInfoAll() throws SQLRepositoryException {
        /*var array =mf.readMFuncInfoAll(connection,"-","-");
        for(var elem : array){
            System.out.println(elem+"/n");
        }*/
        assertDoesNotThrow(()-> mf.readMFuncInfoAll(connection,"-","-"));
    }

    @Test
    @Order(7)
    void CheckBelongs() throws SQLRepositoryException {
        long badfuncid = COUNT+1;
        long badUserId = testerId +1;

        assertTrue(mf.BelongsByOwnerId(connection,fbuffer[0],testerId));
        assertFalse(mf.BelongsByOwnerId(connection,badfuncid,testerId));
        assertFalse(mf.BelongsByOwnerId(connection,fbuffer[0],badUserId));
    }

    @Test
    @Order(8)
    void removeMFunc() {
        for(int i=0; i<COUNT;++i){
            long owner_id = testerId;
            long func_id = fbuffer[i];
            assertDoesNotThrow(() -> mf.removeMFunc(connection,owner_id,func_id));
        }
    }

    @AfterAll
    static void cleanUp() throws SQLException {
        String drop = "DROP TABLE Users CASCADE; DROP TABLE MathFunctions CASCADE; ";
        Statement st = connection.createStatement();
        st.execute(drop);
    }
}