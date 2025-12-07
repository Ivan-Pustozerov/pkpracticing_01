package SQL.repositories;

import SQL.DTO.IdDTO;
import SQL.SQLRepositoryException;
import org.junit.jupiter.api.*;

import java.net.IDN;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsersRepositoryTest {
    private UsersRepository user = new UsersRepository();
    private static Connection connection;
    private static String url;
    private static String username;
    private static String password;
    private static final int COUNT = 10;
    private static String[] ubuffer = new String[COUNT];
    private static String[] ubufferdelete = new String[COUNT];

    private long[] getIdbuffer(String[] ubuffer) throws SQLRepositoryException {
        ArrayList<IdDTO> idlist = user.readUserId(connection,ubuffer,"-");
        long[] idbuffer = new long[idlist.size()];
        for(int i = 0; i<idlist.size();++i){idbuffer[i] = idlist.get(i).id();}
        return idbuffer;
    }

    @BeforeAll
    static void setup() throws SQLException {
        url = "jdbc:postgresql://localhost:5432/UserTest";
        username = "postgres";
        password = "lkroot";
        connection = DriverManager.getConnection(url,username,password);
        for(int i =0; i<COUNT; ++i){
            ubuffer[i] = "user" +i;
            ubufferdelete[i] = "test" + "user" +i;
        }
    }

    @Test
    @Order(1)
    void initTable() {
        assertDoesNotThrow(() -> user.initTable(connection));
    }

    @Test
    @Order(2)
    void insertUser() {
        for(int i = 0; i < COUNT; ++i){
            boolean is_admin = i < COUNT/2;
            String name = ubuffer[i];
            String email = name + "@example.com";
            byte[] pswd = new byte[]{12};
            assertDoesNotThrow(() -> user.insertUser(connection,is_admin,name,email,pswd));

        }
    }

    @Test
    @Order(3)
    void readUserInfo() throws SQLRepositoryException {
        assertDoesNotThrow(() -> user.readUserInfo(connection,null,ubuffer,"-","-"));

        assertDoesNotThrow(() -> user.readUserInfo(connection,null,ubuffer,"name_asc","asc"));
        assertDoesNotThrow(() -> user.readUserInfo(connection,null,ubuffer,"name_desc","desc"));

        assertDoesNotThrow(() -> user.readUserInfo(connection,null,ubuffer,"id_asc","asc"));
        assertDoesNotThrow(() -> user.readUserInfo(connection,null,ubuffer,"id_desc","desc"));

        assertDoesNotThrow(() -> user.readUserInfo(connection,null,ubuffer,"is_admin_asc","asc"));
        assertDoesNotThrow(() -> user.readUserInfo(connection,null,ubuffer,"is_admin_desc","desc"));

        long[] idbuffer = getIdbuffer(ubuffer);

        assertDoesNotThrow(() -> user.readUserInfo(connection,idbuffer,null,"name_asc","asc"));
        assertDoesNotThrow(() -> user.readUserInfo(connection,idbuffer,null,"name_desc","desc"));

        assertDoesNotThrow(() -> user.readUserInfo(connection,idbuffer,null,"id_asc","asc"));
        assertDoesNotThrow(() -> user.readUserInfo(connection,idbuffer,null,"id_desc","desc"));

        assertDoesNotThrow(() -> user.readUserInfo(connection,idbuffer,null,"is_admin_asc","asc"));
        assertDoesNotThrow(() -> user.readUserInfo(connection,idbuffer,null,"is_admin_desc","desc"));

    }


    @Test
    @Order(4)
    void updateUserAndSetFunctions() throws SQLRepositoryException {
        long[] idbuffer = getIdbuffer(ubuffer);
        MathFunctionsRepository mrep = new MathFunctionsRepository();
        mrep.initTable(connection);
        for(long id : idbuffer){mrep.insertMFunc(connection,"analytic","testname",id);}

        for(int i = 0;i<ubuffer.length;++i){
            String oldname = ubuffer[i];
            String newname = ubufferdelete[i];
            assertDoesNotThrow(() -> user.updateUser(connection,null,oldname,null,newname,null,null));
        }

    }

    @Test
    @Order(5)
    void readUserFunctions() throws SQLRepositoryException {
        long[] idbuffer = getIdbuffer(ubuffer);
        assertDoesNotThrow(() -> user.readUserFunctions(connection,idbuffer,null));
        assertDoesNotThrow(() -> user.readUserFunctions(connection,null,ubuffer));
    }

    @Test
    @Order(6)
    void readUserByRole() {
        assertDoesNotThrow(() -> user.readUserId(connection,ubuffer,"asc"));
        assertDoesNotThrow(() -> user.readUserId(connection,ubuffer,"desc"));
    }

    @Test
    @Order(7)
    void readUserId() {
        assertDoesNotThrow(() -> user.readUserId(connection,ubuffer,"-"));
        assertDoesNotThrow(() -> user.readUserId(connection,ubuffer,"asc"));
        assertDoesNotThrow(() -> user.readUserId(connection,ubuffer,"dsc"));
    }

    @Test
    @Order(8)
    void removeUser() {
        for(String username : ubufferdelete){
            System.out.println(username);
            assertDoesNotThrow(() -> user.removeUser(connection,null,username));
        }
    }
}