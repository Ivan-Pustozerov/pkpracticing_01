package SQL;
import SQL.DTO.*;
import SQL.repositories.*;
import SQL.repositories.tools.Repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Server {
    private final String url;
    private final String username;
    private final String password;
    public final UsersRepository Users;
    public final MathFunctionsRepository MathFunctions;
    public final AnalyticFunctionsRepository AnalyticFunctions;
    public final TabulatedFunctionsRepository TabulatedFunctions;

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

    public Server(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
        Users = new UsersRepository(url, username, password);
        MathFunctions = new MathFunctionsRepository(url, username, password);
        AnalyticFunctions = new AnalyticFunctionsRepository(url, username, password);
        TabulatedFunctions = new TabulatedFunctionsRepository(url, username, password);
        logger("SERVER CREATED");
    }

    public void initDataBase()
            throws SQLRepositoryException{
        Users.initTable();
        MathFunctions.initTable();
        AnalyticFunctions.initTable();
        TabulatedFunctions.initTable();
        logger("TABLE INIT");
    }

    public int addUser(boolean isAdmin, String name, String password)
            throws SQLRepositoryException {
        byte[] pswd = passwordHash(password);
        logger("USER ADDED");
        return Users.insertUser(isAdmin, name, pswd);
    }

    public int addAnalyticMFunction(String function_expression, String name, long owner_id)
            throws SQLRepositoryException {
        long mfId = MathFunctions.insertMFunc("analytic", name, owner_id).get(0).id();
        logger("ANALYTIC FUNCTION ADDED");
        return AnalyticFunctions.insertAnalyticFunction(mfId,function_expression);
    }
    public int addAnalyticMFunction(String function_expression, String name, String owner_name)
            throws SQLRepositoryException {
        long userId = Users.readUserId(owner_name).get(0).id();
        return addAnalyticMFunction(function_expression, name, userId);
    }

    public int addTabulatedMFunction(double[] xVals, double[] yVals, String name, long owner_id)
            throws SQLRepositoryException {
        long mfId = MathFunctions.insertMFunc("tabulated", name, owner_id).get(0).id();
        logger("TABULATED FUNCTION ADDED");
        return TabulatedFunctions.insertTabulatedFunction(mfId, xVals, yVals);
    }
    public int addTabulatedMFunction(double[] xVals, double[] yVals, String name, String owner_name)
            throws SQLRepositoryException {
        long userId = Users.readUserId(owner_name).get(0).id();
        return addTabulatedMFunction(xVals, yVals, name, userId);
    }

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/lab";
        String username = "postgres";
        String password = "lkroot";

        Server server = new Server(url, username, password);
        try{
            server.initDataBase();
            server.addUser(true,"Zaharov","08Lab");
            server.addAnalyticMFunction("z^2","Zaharov^2","Zaharov");
            server.addTabulatedMFunction(new double[]{1,2,3}, new double[]{-2,2.3,3.1},"Zaharov Tabulated", "Zaharov");

            //server.MathFunctions.removeMFunc(8);
            System.out.println(server.Users.readUserInfo(42));
            System.out.println(server.Users.readUserFunctions(42));

            //server.Users.removeUser(42);
        } catch (SQLRepositoryException e) {
            logger("ERROR: - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
