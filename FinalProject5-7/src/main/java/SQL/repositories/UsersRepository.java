package SQL.repositories;

import java.util.ArrayList;

import SQL.DTO.IdDTO;
import SQL.DTO.UserToServerDTO;
import SQL.DTO.MathFunctionDTO;
import SQL.SQLRepositoryException;
import SQL.repositories.tools.Repository;


public class UsersRepository extends Repository {
///===========================================СЛУЖЕБНЫЕ=СОСТОЯНИЯ================================================
    private final String dir = "FinalProject5-7/src/main/resources/scripts/";
    private final String UserInit;
    private final String UserInsert;
    private final String UserReadInfo;
    private final String UserReadFuncs;
    private final String UserReadID;
    private final String UserRemove;
    private final String UserUpdate;

    {
        UserInit = readCommand(dir + "BD_INIT/BD_INIT_Users_table.sql");
        UserInsert = readCommand(dir + "BD_INSERT/BD_INSERT_User.sql");
        UserReadInfo = readCommand(dir + "BD_READ/BD_READ_User_info.sql");
        UserReadFuncs = readCommand(dir + "BD_READ/BD_READ_User_Functions.sql");
        UserReadID = readCommand(dir + "BD_READ/BD_READ_User_ID.sql");
        UserRemove = readCommand(dir + "BD_REMOVE/BD_REMOVE_User.sql");
        UserUpdate = readCommand(dir + "BD_UPDATE/BD_UPDATE_User.sql");
    }
    public UsersRepository(String url, String username, String password){
        super(url,username,password);
    }
///================================================================================================================

///---------------------------------------------------UPDATER------------------------------------------------------
    public int initTable()
            throws SQLRepositoryException {
        return initTable(UserInit);
    }

    public int insertUser(boolean isAdmin, String name, byte[] passwordHash)
            throws SQLRepositoryException{
        return executeUpdate(UserInsert, ps -> { ps.setBoolean(1, isAdmin);
                                                                 ps.setString(2, name);
                                                                 ps.setBytes(3, passwordHash);});
    }

    public int removeUser(long id)
            throws SQLRepositoryException{
        return executeUpdate(UserRemove, ps -> ps.setLong(1,id));
    }

    public int updateUser(long id, boolean new_role, String new_name, String new_email, byte[] new_password)
            throws SQLRepositoryException {
        return executeUpdate(UserUpdate, ps -> { ps.setLong(1, id);
                                                                 ps.setBoolean(2, new_role);
                                                                 ps.setString(3, new_name);
                                                                 ps.setString(4, new_email);
                                                                 ps.setBytes(5, new_password);});
    }

///-------------------------------------------------READER---------------------------------------------------------
    public ArrayList<UserToServerDTO> readUserInfo(long id)
            throws SQLRepositoryException{
        return executeQuery(UserReadInfo, ps -> ps.setLong(1, id),
                set ->{ return new UserToServerDTO(
                        set.getLong("id"),
                        set.getBoolean("is_admin"),
                        set.getString("name"),
                        set.getString("email"),
                        set.getBytes("password"));});
    }

    public ArrayList<MathFunctionDTO> readUserFunctions(long id)
            throws SQLRepositoryException{
        return executeQuery(UserReadFuncs, ps -> ps.setLong(1, id),
                set ->{ return new MathFunctionDTO(
                        set.getLong("id"),
                        set.getString("type"),
                        set.getString("name"),
                        set.getLong("owner_id"));});
    }
    public ArrayList<IdDTO> readUserId(String name)
            throws SQLRepositoryException {
        return executeQuery(UserReadID, ps -> ps.setString(1, name),
                set -> {return new IdDTO(set.getLong("id"));}
                );
    }

}

