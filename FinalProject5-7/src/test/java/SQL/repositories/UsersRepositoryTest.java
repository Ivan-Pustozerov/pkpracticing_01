package SQL.repositories;

import SQL.DTO.IdDTO;
import SQL.DTO.FromBD.MathFunctionFromBdDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
            ubuffer[i] = "user" + (i+1);
            ubufferdelete[i] = "test" + "user" +(i+1);
        }
    }

    @Test
    @Order(1)
    void initTable() {
        assertDoesNotThrow(() -> user.initTable(connection));
    }

    @Test
    @Order(2)
    void insertUser() throws SQLRepositoryException {
        for(int i = 0; i < COUNT; ++i){
            boolean is_admin = i < COUNT/2;
            String name = ubuffer[i];
            String email = name + "@example.com";
            byte[] pswd = new byte[]{12};
            assertDoesNotThrow(() -> user.insertUser(connection,is_admin,name,email,pswd));

        }
        user.insertUser(connection,true,"e",null,new byte[]{12});
    }

    @Test
    @Order(3)
    void readUserInfo() throws SQLRepositoryException {
        assertDoesNotThrow(() -> user.readUserInfo(connection,null,ubuffer,"-","-"));
        /*long id = 1;
        ArrayList<UserToServerDTO> array = user.readUserInfo(connection,null,ubuffer,"id_desc","desc");
        for(var elem : array){
            assertArrayEquals(elem.passwordHash(),new byte[]{12});
            System.out.println(elem + "/n");
        }*/

        assertDoesNotThrow(() -> user.readUserInfo(connection,null,ubuffer,"name_asc","asc"));
        assertDoesNotThrow(() -> user.readUserInfo(connection,null,ubuffer,"name_desc","desc"));

        assertDoesNotThrow(() -> user.readUserInfo(connection,null,ubuffer,"id_asc","asc"));
        assertDoesNotThrow(() -> user.readUserInfo(connection,null,ubuffer,"id_desc","desc"));

        assertDoesNotThrow(() -> user.readUserInfo(connection,null,ubuffer,"is_admin_asc","asc"));
        assertDoesNotThrow(() -> user.readUserInfo(connection,null,ubuffer,"is_admin_desc","desc"));

        long[] idbuffer = getIdbuffer(ubuffer);

        /*ArrayList<UserToServerDTO> array = user.readUserInfo(connection,idbuffer,null,"is_admin_desc","desc");
        for(var elem : array){
            assertArrayEquals(elem.passwordHash(),new byte[]{12});
            System.out.println(elem + "/n");
        }*/

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
        long[] idbuffer = getIdbuffer(ubufferdelete);

        ArrayList<MathFunctionFromBdDTO> array = user.readUserFunctions(connection,null,new String[]{"testuser1","e"});
        System.out.println(array);
        for(var elem : array){
            System.out.println(elem);
        }

        assertDoesNotThrow(() -> user.readUserFunctions(connection,idbuffer,null));
        assertDoesNotThrow(() -> user.readUserFunctions(connection,null,ubufferdelete));
    }

    @Test
    @Order(6)
    void readUserByRole() throws SQLRepositoryException {

        /*var array = user.readUserByRole(connection,false);
        for(var elem : array){
            System.out.println(elem + "/n");
        }*/

        assertDoesNotThrow(() -> user.readUserByRole(connection,true));
        assertDoesNotThrow(() -> user.readUserByRole(connection,false));
    }

    @Test
    @Order(7)
    void readUserId() {
        assertDoesNotThrow(() -> user.readUserId(connection,ubufferdelete,"-"));
        assertDoesNotThrow(() -> user.readUserId(connection,ubufferdelete,"asc"));
        assertDoesNotThrow(() -> user.readUserId(connection,ubufferdelete,"dsc"));
    }

    @Test
    @Order(8)
    void readUserIdByLikeName() throws SQLRepositoryException {
        /*var array = user.readUserIdByLikeName(connection,"test%");
        for(var elem : array){
            System.out.println(elem + "/n");
        }*/

        assertDoesNotThrow(() -> user.readUserIdByLikeName(connection,"test%"));
    }

    @Test
    @Order(9)
    void readAllUser() throws SQLRepositoryException {
        /*var array = user.readAllUsers(connection,"id_asc","asc");
        for(var elem : array){
            System.out.println(elem + "/n");
        }*/
        assertDoesNotThrow(() -> user.readAllUsers(connection,"id_asc","asc"));
        assertDoesNotThrow(() -> user.readAllUsers(connection,"is_admin_asc","asc"));
        assertDoesNotThrow(() -> user.readAllUsers(connection,"-","asc"));

        assertDoesNotThrow(() -> user.readAllUsers(connection,"id_desc","desc"));
        assertDoesNotThrow(() -> user.readAllUsers(connection,"is_admin_desc","desc"));
        assertDoesNotThrow(() -> user.readAllUsers(connection,"-","desc"));
    }

    @Test
    @Order(10)
    void checkExists() throws SQLRepositoryException {
        long id =1;
        long badid = COUNT+2;

        String name = ubufferdelete[0];
        String badname = "BadName";

        assertTrue(user.exists(connection,id,null));
        assertFalse(user.exists(connection,badid,null));

        assertTrue(user.exists(connection,null,name));
        assertFalse(user.exists(connection,null,badname));
    }

    @Test
    @Order(10)
    void checkIsAdmin() throws SQLRepositoryException {
        long id_admin =1;
        long id_user = COUNT/2+2;

        String name_admin = ubufferdelete[0];
        String name_user = ubuffer[COUNT/2+2];

        assertTrue(user.isAdmin(connection,id_admin,null));
        assertFalse(user.isAdmin(connection,id_user,null));

        assertTrue(user.isAdmin(connection,null,name_admin));
        assertFalse(user.isAdmin(connection,null,name_user));
    }

    @Test
    @Order(12)
    void removeUser() {
        for(String username : ubufferdelete){
            assertDoesNotThrow(() -> user.removeUser(connection,null,username));
        }
    }

    @AfterAll
    static void cleanUp() throws SQLException {
        String drop = "DROP TABLE Users CASCADE; DROP TABLE MathFunctions CASCADE;";
        Statement st = connection.createStatement();
        st.execute(drop);
    }
}