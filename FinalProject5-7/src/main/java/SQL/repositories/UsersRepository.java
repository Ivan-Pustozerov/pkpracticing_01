package SQL.repositories;

import java.sql.Connection;
import java.util.ArrayList;

import SQL.DTO.IdDTO;
import SQL.DTO.UserToServerDTO;
import SQL.DTO.MathFunctionDTO;
import SQL.SQLRepositoryException;
import SQL.repositories.tools.Repository;
import SQL.repositories.tools.SQLArray;


public class UsersRepository extends Repository {
///===========================================СЛУЖЕБНЫЕ=СОСТОЯНИЯ================================================
    private final String dir = "scripts/";
    private final String UserInit;
    private final String UserInsert;
    private final String UserReadInfoAsc;
    private final String UserReadInfoDesc;
    private final String UserReadFuncs;
    private final String UserReadIDAsc;
    private final String UserReadIDDesc;
    private final String UserReadByRole;
    private final String UserRemove;
    private final String UserUpdate;

    {
        UserInit = readCommand(dir + "BD_INIT/BD_INIT_Users_table.sql");
        UserInsert = readCommand(dir + "BD_INSERT/BD_INSERT_User.sql");
        UserReadInfoAsc = readCommand(dir + "BD_READ/BD_READ_User_infoASC.sql");
        UserReadInfoDesc = readCommand(dir + "BD_READ/BD_READ_User_infoDESC.sql");
        UserReadFuncs = readCommand(dir + "BD_READ/BD_READ_User_Functions.sql");
        UserReadIDAsc = readCommand(dir + "BD_READ/BD_READ_User_IDASC.sql");
        UserReadIDDesc = readCommand(dir + "BD_READ/BD_READ_User_IDDESC.sql");
        UserReadByRole = readCommand(dir + "BD_READ/BD_READ_User_By_Role.sql");
        UserRemove = readCommand(dir + "BD_REMOVE/BD_REMOVE_User.sql");
        UserUpdate = readCommand(dir + "BD_UPDATE/BD_UPDATE_User.sql");
    }
///================================================================================================================

///---------------------------------------------------UPDATER------------------------------------------------------
    public int initTable(Connection connect)
            throws SQLRepositoryException {
        return initTable(connect, UserInit);
    }

    public int insertUser(Connection connect, boolean isAdmin, String name, String email, byte[] passwordHash)
            throws SQLRepositoryException{
        return executeUpdate(connect, UserInsert, ps -> { ps.setBoolean(1, isAdmin);
                                                                 ps.setString(2, name);
                                                                 ps.setString(3, email);
                                                                 ps.setBytes(4, passwordHash);});
    }

    public int removeUser(Connection connect, Long id, String name)
            throws SQLRepositoryException{
        return executeUpdate(connect, UserRemove, ps -> {
            if(id == null) ps.setNull(1, -5);              else{ps.setLong(1, id);}
                                                                ps.setString(2,name);});
    }

    public int updateUser(Connection connect, Long id, String old_name, Boolean new_role, String new_name, String new_email, byte[] new_password)
            throws SQLRepositoryException {
        return executeUpdate(connect, UserUpdate, ps -> {
                if(new_role == null) ps.setNull(1, 16);     else{ps.setBoolean(1, new_role);}
                                                                 ps.setString(2, new_name);
                                                                 ps.setString(3, new_email);
                                                                 ps.setBytes(4, new_password);
                if(id == null) ps.setNull(5, -5);           else{ps.setLong(5, id);}
                                                                 ps.setString(6, old_name);
        });
    }

///-------------------------------------------------READER---------------------------------------------------------
    public ArrayList<UserToServerDTO> readUserInfo(Connection connect, long[] id, String[] names, String sortField, String sortOrder)
            throws SQLRepositoryException{

        try(SQLArray idArray = toLongSQLArray(connect, id);
            SQLArray nameArray = toStringSQLArray(connect, names)) {

            return executeQuery(connect, sortOrder.equals("desc")? UserReadInfoDesc : UserReadInfoAsc,
                    ps -> {
                        ps.setArray(1, idArray.innerArray());
                        ps.setArray(2, nameArray.innerArray());
                        ps.setString(3, sortField);
                    },
                    set -> {
                        return new UserToServerDTO(
                                set.getLong("id"),
                                set.getBoolean("is_admin"),
                                set.getString("name"),
                                set.getString("email"),
                                set.getBytes("password"));
                    });
        }
    }

    public ArrayList<MathFunctionDTO> readUserFunctions(Connection connect, long[] id, String[] names)
            throws SQLRepositoryException{

        try(SQLArray idArray = toLongSQLArray(connect, id);
            SQLArray nameArray = toStringSQLArray(connect, names)) {
            return executeQuery(connect, UserReadFuncs, ps -> {
                        ps.setArray(1, idArray.innerArray());
                        ps.setArray(2, nameArray.innerArray());
                    },
                    set -> {
                        return new MathFunctionDTO(
                                set.getLong("id"),
                                set.getString("type"),
                                set.getString("name"),
                                set.getLong("owner_id"));
                    });
        }
    }

    public ArrayList<UserToServerDTO> readUserByRole(Connection connect, boolean is_admin)
            throws SQLRepositoryException {
        return executeQuery(connect, UserReadByRole, ps -> ps.setBoolean(1,is_admin),
                set -> {
                    return new UserToServerDTO(
                            set.getLong("id"),
                            set.getBoolean("is_admin"),
                            set.getString("name"),
                            set.getString("email"),
                            set.getBytes("password"));
                });
    }

    public ArrayList<IdDTO> readUserId(Connection connect, String[] names, String sortOrder)
            throws SQLRepositoryException {
        try(SQLArray nameArray = toStringSQLArray(connect, names)) {
            return executeQuery(connect, (sortOrder.equals("desc"))? UserReadIDDesc:UserReadIDAsc,
                    ps -> {
                                    ps.setArray(1, nameArray.innerArray());
                    },
                    set -> {
                        return new IdDTO(set.getLong("id"));
                    }
            );
        }
    }

}

