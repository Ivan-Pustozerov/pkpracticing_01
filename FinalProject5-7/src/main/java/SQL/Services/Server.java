/*
package SQL.Services;
import SQL.DTO.*;
import SQL.DTO.ToServer.UserToServerDTO;
import SQL.repositories.SQLRepositoryException;
import SQL.repositories.*;
import SQL.repositories.tools.SmartConnection;
import SQL.repositories.tools.SmartConnectionException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class Server {
    private final String url;
    private final String username;
    private final String password;

    private SmartConnection connection;

    protected final UsersRepository Users = new UsersRepository();
    protected final MathFunctionsRepository MathFunctions = new MathFunctionsRepository();
    protected final AnalyticFunctionsRepository AnalyticFunctions = new AnalyticFunctionsRepository();
    protected final TabulatedFunctionsRepository TabulatedFunctions = new TabulatedFunctionsRepository();



    private static void logger(String log){
        System.out.println(log);
    }

///==============================================================================
    public void initDataBase()
            throws SQLRepositoryException, SmartConnectionException {

        Users.initTable(connections.getConnection(UPoolIndex));
        MathFunctions.initTable(connections.getConnection(MPoolIndex));
        AnalyticFunctions.initTable(connections.getConnection(APoolIndex));
        TabulatedFunctions.initTable(connections.getConnection(TPoolIndex));

        connections.free();
        if(connections.isEmpty()) logger("TABLE INIT");
    }

///------------------------------------------------------------------------------



    public int addAnalyticMFunction(String function_expression, String name, long owner_id)
            throws SQLRepositoryException, SmartConnectionException {
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
            throws SQLRepositoryException, SmartConnectionException {
        try {

            long userId = Users.readUserId(connections.getConnection(UPoolIndex), new String[]{owner_name}).get(0).id();
            return addAnalyticMFunction(function_expression, name, userId);
        }
        finally{
            connections.free();
        }
    }


    public int addTabulatedMFunction(double[] xVals, double[] yVals, String name, long owner_id)
            throws SQLRepositoryException, SmartConnectionException {
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
            throws SQLRepositoryException, SmartConnectionException {
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
            throws SmartConnectionException, SQLRepositoryException {
        try {
            return MathFunctions.removeMFunc(connections.getConnection(MPoolIndex), owner_id, id);
        }
        finally{
            connections.free();
        }
    }
    public int removeMFunction(String owner_name, long id)
            throws SmartConnectionException, SQLRepositoryException {
        try {
            long userId = Users.readUserId(connections.getConnection(UPoolIndex), new String[]{owner_name}).get(0).id();
            return removeMFunction(userId, id);
        }
        finally{
            connections.free();
        }
    }


///------------------------------------------------------------------------------




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
        } catch (SmartConnectionException e) {
            System.out.println(e.getMessage());
        }
    }
}*/
