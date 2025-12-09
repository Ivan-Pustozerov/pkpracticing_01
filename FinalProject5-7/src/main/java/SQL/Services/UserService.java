package SQL.Services;

import SQL.DTO.IdDTO;
import SQL.DTO.ToClient.MathFunctionToClientAdminDTO;
import SQL.DTO.ToClient.UserToClientAdminDTO;
import SQL.DTO.FromBD.UserFromBdDTO;
import SQL.Mappers.MathFunctionMapperDTO;
import SQL.repositories.AnalyticFunctionsRepository;
import SQL.repositories.SQLRepositoryException;
import SQL.repositories.TabulatedFunctionsRepository;
import SQL.repositories.UsersRepository;
import SQL.repositories.tools.SmartConnection;
import SQL.repositories.tools.SmartConnectionException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class UserService {

    private final SmartConnection connection;
    private final UsersRepository Users = new UsersRepository();
    private final AnalyticFunctionsRepository AnalyticRepo = new AnalyticFunctionsRepository();
    private final TabulatedFunctionsRepository TabulatedRepo = new TabulatedFunctionsRepository();


    public UserService(String url, String username, String password)
            throws SmartConnectionException {
        connection = new SmartConnection(url, username, password);
    }

    public static ArrayList<UserToClientAdminDTO> translateToClientDTO(ArrayList<UserFromBdDTO> BDdto){
        ArrayList<UserToClientAdminDTO> result = new ArrayList<>();
        for (UserFromBdDTO userDTO : BDdto) {
            boolean is_admin = userDTO.is_admin();
            String name = userDTO.name();
            String email = userDTO.email();
            result.add(new UserToClientAdminDTO(is_admin, name, email));
        }
        return result;
    }
    private static byte[] passwordHash(String password){
        //одностороннее хеширование
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] input = password.getBytes(StandardCharsets.UTF_8);
            return md.digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

///=========================================CREATE======================================
    public void initDataBase()
            throws SQLRepositoryException, SmartConnectionException {
        Users.initTable(connection.getConnection());
    }

    public int addUser(boolean isAdmin, String name, String email, String password)
            throws SmartConnectionException, SQLRepositoryException {

        if(checkUsersExist(new String[]{name})) throw new ServiceArgumentsException("User: " + name + " already exists");

        byte[] pswd = passwordHash(password);
        return Users.insertUser(connection.getConnection(), isAdmin, name, email, pswd);

    }

///===========================================READ=======================================

    public ArrayList<UserToClientAdminDTO> readUserInfo(long[] id, String sortField, String sortOrder)
            throws SmartConnectionException, SQLRepositoryException {

        if(!checkUsersExist(id)) throw new ServiceArgumentsException("Not Every User Is Available");

        var BDdto = Users.readUserInfo(connection.getConnection(), id, null, sortField, sortOrder);
        return translateToClientDTO(BDdto);
    }
    public ArrayList<UserToClientAdminDTO> readUserInfo(String[] name, String sortField, String sortOrder)
            throws SmartConnectionException, SQLRepositoryException {

        if(!checkUsersExist(name)) throw new ServiceArgumentsException("Not Every User Is Available");

        var BDdto = Users.readUserInfo(connection.getConnection(), null, name, sortField, sortOrder);
        return translateToClientDTO(BDdto);
    }


    public ArrayList<IdDTO> readUsersID(String[] name, String sortOrder)
            throws SmartConnectionException, SQLRepositoryException {

        if(!checkUsersExist(name)) throw new ServiceArgumentsException("Not Every User Is Available");

        return Users.readUserId(connection.getConnection(), name, sortOrder);
    }


    public ArrayList<MathFunctionToClientAdminDTO> readUsersFunctions(long[] id)
            throws SmartConnectionException, SQLRepositoryException {

        var BDdto = Users.readUserFunctions(connection.getConnection(), id, null);
        var result = MathFunctionMapperDTO.translateToClientDTO(BDdto,connection, AnalyticRepo, TabulatedRepo);
        return MathFunctionMapperDTO.whoIsMissing(id, result);
    }

    public ArrayList<MathFunctionToClientAdminDTO> readUsersFunctions(String[] name)
            throws SmartConnectionException, SQLRepositoryException {

        var BDdto = Users.readUserFunctions(connection.getConnection(), null, name);
        return FunctionService.translateToClientDTO(BDdto,connection,
                new AnalyticFunctionsRepository(),new TabulatedFunctionsRepository());
    }

///=======================================UPDATE=========================================

    public int updateUserInfo(long id, String new_name, String new_email, String new_password)
            throws SmartConnectionException, SQLRepositoryException {

        byte[] pswrd = passwordHash(new_password);
        return Users.updateUser(connection.getConnection(),id,null,null,new_name,new_email,pswrd);
    }

    public int updateUserInfo(String old_name, String new_name, String new_email, String new_password)
            throws SmartConnectionException, SQLRepositoryException {

        byte[] pswrd = passwordHash(new_password);
        return Users.updateUser(connection.getConnection(),null,old_name,null,new_name,new_email,pswrd);
    }


    public int updateUserRole(long id, boolean new_role)
            throws SmartConnectionException, SQLRepositoryException {
        return Users.updateUser(connection.getConnection(),id,null,new_role,null,null,null);
    }

///=======================================DELETE==========================================
    public int removeUser(long id)
            throws SmartConnectionException, SQLRepositoryException {
        return Users.removeUser(connection.getConnection(), id, null);

    }
    public int removeUser(String name)
            throws SmartConnectionException, SQLRepositoryException {
        return Users.removeUser(connection.getConnection(), null, name);
    }

/// ======================================CHECK============================================

    public boolean checkUsersExist(long[] id)
            throws SmartConnectionException, SQLRepositoryException {

        for(int i=0;i<id.length;++i){
            if(!Users.exists(connection.getConnection(), id[i], null)){
                return false;
            }
        }
        return true;
    }
    public boolean checkUsersExist(String[] name)
            throws SmartConnectionException, SQLRepositoryException {

        for(int i=0;i<name.length;++i){
            if(!Users.exists(connection.getConnection(), null, name[i])){
                return false;
            }
        }
        return true;
    }


    public boolean checkIsAdmin(long id)
            throws SmartConnectionException, SQLRepositoryException {

        return Users.isAdmin(connection.getConnection(), id, null);
    }
    public boolean checkIsAdmin(String name)
            throws SmartConnectionException, SQLRepositoryException {

        return Users.isAdmin(connection.getConnection(), null, name);
    }


    public void UserAuth(long id, String password)
            throws SmartConnectionException, SQLRepositoryException {

        if(!checkUsersExist(new long[]{id}))
            throw new ServiceArgumentsException("Incorrect Auth");

        byte[] pswrd = passwordHash(password);
        var UserDto = Users.readUserInfo(connection.getConnection(), new long[]{id},null,"-","-").get(0);

        if(!MessageDigest.isEqual(UserDto.passwordHash(),pswrd))
            throw new ServiceArgumentsException("Incorrect Auth");
    }
    public void UserAuth(String name, String password)
            throws SmartConnectionException, SQLRepositoryException {

        if(!checkUsersExist(new String[]{name}))
            throw new ServiceException("Incorrect Auth");

        byte[] pswrd = passwordHash(password);
        var UserDto = Users.readUserInfo(connection.getConnection(), null, new String[]{name},"-","-").get(0);

        if(!MessageDigest.isEqual(UserDto.passwordHash(),pswrd))
            throw new ServiceArgumentsException("Incorrect Auth");
    }
}
