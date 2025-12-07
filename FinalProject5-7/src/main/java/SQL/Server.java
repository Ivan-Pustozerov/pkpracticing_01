/*
package SQL;
import SQL.DTO.*;
import SQL.repositories.*;
import SQL.repositories.tools.ConnectPool;
import SQL.repositories.tools.ConnectPoolException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class Server {
    private final String url;
    private final String username;
    private final String password;

    private ConnectPool connections;
    private final int UPoolIndex = 0;
    private final int MPoolIndex = 1;
    private final int APoolIndex = 2;
    private final int TPoolIndex = 3;

    protected final UsersRepository Users = new UsersRepository();
    protected final MathFunctionsRepository MathFunctions = new MathFunctionsRepository();
    protected final AnalyticFunctionsRepository AnalyticFunctions = new AnalyticFunctionsRepository();
    protected final TabulatedFunctionsRepository TabulatedFunctions = new TabulatedFunctionsRepository();



    private static void logger(String log){
        System.out.println(log);
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

    public Server(String url, String username, String password) throws ConnectPoolException {
        this.url = url;
        this.username = username;
        this.password = password;
        connections = new ConnectPool(url, username, password, 4);
        logger("SERVER CREATED");
    }
///==============================================================================
    public void initDataBase()
            throws SQLRepositoryException, ConnectPoolException {

        Users.initTable(connections.getConnection(UPoolIndex));
        MathFunctions.initTable(connections.getConnection(MPoolIndex));
        AnalyticFunctions.initTable(connections.getConnection(APoolIndex));
        TabulatedFunctions.initTable(connections.getConnection(TPoolIndex));

        connections.free();
        if(connections.isEmpty()) logger("TABLE INIT");
    }

///------------------------------------------------------------------------------
    public int addUser(boolean isAdmin, String name, String email, String password)
            throws SQLRepositoryException, ConnectPoolException {
        byte[] pswd = passwordHash(password);
        logger("USER ADDED");
        try{
            return Users.insertUser(connections.getConnection(UPoolIndex),isAdmin, name, email, pswd);
        }
        finally{
            connections.free();
        }
    }


    public int addAnalyticMFunction(String function_expression, String name, long owner_id)
            throws SQLRepositoryException, ConnectPoolException {
        try {
            long mfId = MathFunctions.insertMFunc(connections.getConnection(MPoolIndex), "analytic", name, owner_id).get(0).id();
            logger("ANALYTIC FUNCTION ADDED");
            return AnalyticFunctions.insertAnalyticFunction(connections.getConnection(APoolIndex), mfId, function_expression);
        }
        finally{
            connections.free();
        }
    }
    public int addAnalyticMFunction(String function_expression, String name, String owner_name)
            throws SQLRepositoryException, ConnectPoolException {
        try {

            long userId = Users.readUserId(connections.getConnection(UPoolIndex), new String[]{owner_name}).get(0).id();
            return addAnalyticMFunction(function_expression, name, userId);
        }
        finally{
            connections.free();
        }
    }


    public int addTabulatedMFunction(double[] xVals, double[] yVals, String name, long owner_id)
            throws SQLRepositoryException, ConnectPoolException {
        try {
            long mfId = MathFunctions.insertMFunc(connections.getConnection(MPoolIndex), "tabulated", name, owner_id).get(0).id();
            logger("TABULATED FUNCTION ADDED");
            return TabulatedFunctions.insertTabulatedFunction(connections.getConnection(TPoolIndex), mfId, xVals, yVals);
        }
        finally{
            connections.free();
        }
    }
    public int addTabulatedMFunction(double[] xVals, double[] yVals, String name, String owner_name)
            throws SQLRepositoryException, ConnectPoolException {
        try {
            long userId = Users.readUserId(connections.getConnection(UPoolIndex), new String[]{owner_name}).get(0).id();
            return addTabulatedMFunction(xVals, yVals, name, userId);
        }
        finally{
            connections.free();
        }
    }

///------------------------------------------------------------------------------
    public int removeMFunction(long owner_id, long id)
            throws ConnectPoolException, SQLRepositoryException {
        try {
            return MathFunctions.removeMFunc(connections.getConnection(MPoolIndex), owner_id, id);
        }
        finally{
            connections.free();
        }
    }
    public int removeMFunction(String owner_name, long id)
            throws ConnectPoolException, SQLRepositoryException {
        try {
            long userId = Users.readUserId(connections.getConnection(UPoolIndex), new String[]{owner_name}).get(0).id();
            return removeMFunction(userId, id);
        }
        finally{
            connections.free();
        }
    }


    public int removeUser(long id)
            throws ConnectPoolException, SQLRepositoryException {
        try {
            return Users.removeUser(connections.getConnection(UPoolIndex), id, null);
        }
        finally{
            connections.free();
        }
    }
    public int removeUser(String name)
            throws
            ConnectPoolException, SQLRepositoryException {
        try {
            return Users.removeUser(connections.getConnection(UPoolIndex), -1, name);
        }
        finally{
            connections.free();
        }
    }

///------------------------------------------------------------------------------
    public ArrayList<UserToServerDTO> readUsersInfo(long[] id)
            throws ConnectPoolException, SQLRepositoryException {
        try {
            return Users.readUserInfo(connections.getConnection(UPoolIndex), id, null);
        }
        finally{
            connections.free();
        }

    }
    public ArrayList<UserToServerDTO> readUsersInfo(String[] name)
            throws ConnectPoolException, SQLRepositoryException {
        try {
            return Users.readUserInfo(connections.getConnection(UPoolIndex), new long[]{-1}, name);
        }
        finally{
            connections.free();
        }
    }


    public ArrayList<MathFunctionDTO> readUsersFunctions(long[] id)
            throws ConnectPoolException, SQLRepositoryException {
        try {
            return Users.readUserFunctions(connections.getConnection(UPoolIndex), id, null);
        }
        finally{
            connections.free();
        }

    }
    public ArrayList<MathFunctionDTO> readUsersFunctions(String[] name)
            throws ConnectPoolException, SQLRepositoryException {
        try {
            return Users.readUserFunctions(connections.getConnection(UPoolIndex), new long[]{-1}, name);
        }
        finally{
            connections.free();
        }

    }


    public ArrayList<IdDTO> readUsersID(String[] name)
            throws ConnectPoolException, SQLRepositoryException {
        try {
            return Users.readUserId(connections.getConnection(UPoolIndex), name);
        }
        finally{
            connections.free();
        }
    }

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/lab";
        String username = "postgres";
        String password = "lkroot";


        try{
            Server server = new Server(url, username, password);
            server.initDataBase();
            server.addUser(true, "Zaharov" ,null, "08Lab");
            server.addAnalyticMFunction("z^2","Zaharov^2","Zaharov");
            server.addTabulatedMFunction(new double[]{1,2,3}, new double[]{-2,2.3,3.1},"Zaharov Tabulated", "Zaharov");

            System.out.println(server.readUsersInfo(new String[]{"Zaharov"}));
            System.out.println(server.readUsersFunctions(new String[]{"Zaharov"}));
            System.out.println(server.readUsersID(new String[]{"Zaharov"}));

            //server.removeMFunction("Zaharov",)
            //server.Users.removeUser(42);
        } catch (SQLRepositoryException e) {
            logger("ERROR: - " + e.getMessage());
            e.printStackTrace();
        } catch (ConnectPoolException e) {
            System.out.println(e.getMessage());
        }
    }
}*/
