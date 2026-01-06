package SQL.Services;

import SQL.DTO.ToClient.MathFunctionToClientAdminDTO;
import SQL.Mappers.MFunctionMapper;
import SQL.repositories.*;
import SQL.repositories.tools.SQLRepositoryException;
import SQL.repositories.tools.SmartConnection;
import SQL.repositories.tools.SmartConnectionException;
import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;

import java.util.ArrayList;


public class FunctionService {

    private final SmartConnection connection;
    private final MathFunctionsRepository MathRepo = new MathFunctionsRepository();
    private final AnalyticFunctionsRepository AnalyticRepo = new AnalyticFunctionsRepository();
    private final TabulatedFunctionsRepository TabulatedRepo = new TabulatedFunctionsRepository();
    private final UserStatisticRepository StatRepo = new UserStatisticRepository();
    private final UsersRepository UserRepo = new UsersRepository();

    private TabulatedFunctionFactory factory;


    public FunctionService(String url, String username, String password)
            throws SmartConnectionException, SQLRepositoryException {
        connection = new SmartConnection(url,username,password);
        factory = new ArrayTabulatedFunctionFactory();
        initDataBase();
    }

    public void setArrayFactory(){
        factory = new ArrayTabulatedFunctionFactory();
    }
    public void setLinkedFactory(){
        factory = new LinkedListTabulatedFunctionFactory();
    }

    /// ========================================CREATE=============================================

    public void initDataBase()
            throws SmartConnectionException, SQLRepositoryException {
        MathRepo.initTable(connection.getConnection());
        AnalyticRepo.initTable(connection.getConnection());
        TabulatedRepo.initTable(connection.getConnection());
        StatRepo.initTable(connection.getConnection());
        UserRepo.initTable(connection.getConnection());
    }

    public int addAnalyticMFunction(String function_expression, String name, long owner_id)
            throws SQLRepositoryException, SmartConnectionException {

        if(!UserRepo.exists(connection.getConnection(), owner_id, null))
            throw new ServiceArgumentsException("Owner with id " + owner_id + " does not exist");

        long mfId = MathRepo.insertMFunc(connection.getConnection(), "analytic", name, owner_id).get(0).id();

        StatRepo.updateStatFuncID(connection.getConnection(),owner_id);
        return AnalyticRepo.insertAnalyticFunction(connection.getConnection(), mfId, function_expression);
    }

    public int addAnalyticMFunction(String function_expression, String name, String owner_name)
            throws SQLRepositoryException, SmartConnectionException {

        if(!UserRepo.exists(connection.getConnection(), null, owner_name))
            throw new ServiceArgumentsException("Owner with name " + owner_name + " does not exist");

        long userId = UserRepo.readUserId(connection.getConnection(), new String[]{owner_name},"-").get(0).id();

        StatRepo.updateStatFunc(connection.getConnection(),owner_name);
        return addAnalyticMFunction(function_expression, name, userId);
    }


    public int addTabulatedMFunction(double[] xVals, double[] yVals, String name, long owner_id)
            throws SQLRepositoryException, SmartConnectionException {

        if(!UserRepo.exists(connection.getConnection(), owner_id, null))
            throw new ServiceArgumentsException("Owner with id " + owner_id + " does not exist");

        try{
            factory.create(xVals,yVals);
        }
        catch(ArrayIsNotSortedException | DifferentLengthOfArraysException Err){
            throw new ServiceArgumentsException(Err.getMessage());
        }

        long mfId = MathRepo.insertMFunc(connection.getConnection(), "tabulated", name, owner_id).get(0).id();

        StatRepo.updateStatFuncID(connection.getConnection(),owner_id);
        return TabulatedRepo.insertTabulatedFunction(connection.getConnection(), mfId, xVals, yVals);
    }

    public int addTabulatedMFunction(double[] xVals, double[] yVals, String name, String owner_name)
            throws SQLRepositoryException, SmartConnectionException {

        if(!UserRepo.exists(connection.getConnection(), null, owner_name))
            throw new ServiceArgumentsException("Owner with name " + owner_name + " does not exist");

        try{
            factory.create(xVals,yVals);
        }
        catch(ArrayIsNotSortedException | DifferentLengthOfArraysException Err){
            throw new ServiceArgumentsException(Err.getMessage());
        }

        long userId = UserRepo.readUserId(connection.getConnection(), new String[]{owner_name}, "-").get(0).id();

        StatRepo.updateStatFunc(connection.getConnection(),owner_name);
        return addTabulatedMFunction(xVals, yVals, name, userId);
    }

    /// ========================================READ=============================================

    public ArrayList<MathFunctionToClientAdminDTO> readMFunctionInfo(long[] id, String sortField, String sortOrder)
            throws SmartConnectionException, SQLRepositoryException {

        if(!checkMFunctionsExist(id))
            throw new ServiceArgumentsException("Not Every Function Is Available");

        var BDdto = MathRepo.readMFuncInfo(connection.getConnection(), id, sortField, sortOrder);
        for(var DTO : BDdto){
            long owner_id = DTO.owner_id();
            long func_id = DTO.id();
            MathRepo.updateMFuncUsages(connection.getConnection(), owner_id, func_id);
        }
        return MFunctionMapper.translateToClientDTO(BDdto, connection, AnalyticRepo, TabulatedRepo, factory);
    }

    public ArrayList<MathFunctionToClientAdminDTO> readMFunctionInfoByOwnerId(long owner_id, String sortField, String sortOrder)
            throws SmartConnectionException, SQLRepositoryException {

        if(!UserRepo.exists(connection.getConnection(), owner_id, null))
            throw new ServiceArgumentsException("Owner with id " + owner_id + " does not exist");

        var BDdto = MathRepo.readMFuncInfoByOwnerId(connection.getConnection(), owner_id, sortField, sortOrder);
        return MFunctionMapper.translateToClientDTO(BDdto, connection, AnalyticRepo, TabulatedRepo, factory);
    }

    public ArrayList<MathFunctionToClientAdminDTO> readMFunctionInfoByOwnerName(String owner_name, String sortField, String sortOrder)
            throws SmartConnectionException, SQLRepositoryException {

        if(!UserRepo.exists(connection.getConnection(), null, owner_name))
            throw new ServiceArgumentsException("Owner with name " + owner_name + " does not exist");

        long userId = UserRepo.readUserId(connection.getConnection(), new String[]{owner_name}, "-").get(0).id();
        var BDdto = MathRepo.readMFuncInfoByOwnerId(connection.getConnection(), userId, sortField, sortOrder);
        return MFunctionMapper.translateToClientDTO(BDdto, connection, AnalyticRepo, TabulatedRepo, factory);
    }

    public ArrayList<MathFunctionToClientAdminDTO> readInfoAll(String sortField, String sortOrder)
            throws SmartConnectionException, SQLRepositoryException {
        var BDdto = MathRepo.readMFuncInfoAll(connection.getConnection(), sortField, sortOrder);
        return MFunctionMapper.translateToClientDTO(BDdto, connection, AnalyticRepo, TabulatedRepo, factory);
    }

    /// ========================================UPDATE=============================================

    public int updateMFunctionName(long owner_id, long id, String new_name)
            throws SmartConnectionException, SQLRepositoryException {

        if(!checkMFunctionBelongs(owner_id, id))
            throw new ServiceArgumentsException("Function with id " + id + " does not belong to user with id " + owner_id);

        return MathRepo.updateMFunc(connection.getConnection(), owner_id, id, new_name);
    }

    public int updateMFunctionName(String owner_name, long id, String new_name)
            throws SmartConnectionException, SQLRepositoryException {

        if(!UserRepo.exists(connection.getConnection(), null, owner_name))
            throw new ServiceArgumentsException("Owner with name " + owner_name + " does not exist");

        long userId = UserRepo.readUserId(connection.getConnection(), new String[]{owner_name}, "-").get(0).id();

        if(!checkMFunctionBelongs(userId, id))
            throw new ServiceArgumentsException("Function with id " + id + " does not belong to user with name " + owner_name);

        return MathRepo.updateMFunc(connection.getConnection(), userId, id, new_name);
    }

    public int updateAnalyticFunction(long owner_id, long id, String function_expression)
            throws SmartConnectionException, SQLRepositoryException {

        if(!checkMFunctionBelongs(owner_id, id))
            throw new ServiceArgumentsException("Function with id " + id + " does not belong to user with id " + owner_id);

        String funcType = getFunctionType(id);
        if(!funcType.equals("analytic"))
            throw new ServiceArgumentsException("Function with id " + id + " is not an analytic function");

        /// чеккер на корректность парсера

        return AnalyticRepo.updateAnalyticFunction(connection.getConnection(), id, function_expression);
    }

    public int updateAnalyticFunction(String owner_name, long id, String function_expression)
            throws SmartConnectionException, SQLRepositoryException {

        if(!UserRepo.exists(connection.getConnection(), null, owner_name))
            throw new ServiceArgumentsException("Owner with name " + owner_name + " does not exist");

        long userId = UserRepo.readUserId(connection.getConnection(), new String[]{owner_name}, "-").get(0).id();

        if(!checkMFunctionBelongs(userId, id))
            throw new ServiceArgumentsException("Function with id " + id + " does not belong to user with name " + owner_name);

        String funcType = getFunctionType(id);
        if(!funcType.equals("analytic"))
            throw new ServiceArgumentsException("Function with id " + id + " is not an analytic function");

        /// чеккер на корректность парсера

        return AnalyticRepo.updateAnalyticFunction(connection.getConnection(), id, function_expression);
    }

    public int updateTabulatedFunctionIndex(long owner_id, long id, int index, double y_val)
            throws SmartConnectionException, SQLRepositoryException {

        if(!checkMFunctionBelongs(owner_id, id))
            throw new ServiceArgumentsException("Function with id " + id + " does not belong to user with id " + owner_id);

        String funcType = getFunctionType(id);
        if(!funcType.equals("tabulated"))
            throw new ServiceArgumentsException("Function with id " + id + " is not a tabulated function");

        return TabulatedRepo.updateTabulatedFunctionIndex(connection.getConnection(), id, index, y_val);
    }

    public int updateTabulatedFunctionFull(long owner_id, long id, double[] xVals, double[] yVals)
            throws SmartConnectionException, SQLRepositoryException {

        if(!checkMFunctionBelongs(owner_id, id))
            throw new ServiceArgumentsException("Function with id " + id + " does not belong to user with id " + owner_id);

        String funcType = getFunctionType(id);
        if(!funcType.equals("tabulated"))
            throw new ServiceArgumentsException("Function with id " + id + " is not a tabulated function");

        try{
            factory.create(xVals,yVals);
        }
        catch(ArrayIsNotSortedException | DifferentLengthOfArraysException Err){
            throw new ServiceArgumentsException(Err.getMessage());
        }

        return TabulatedRepo.updateTabulatedFunctionFull(connection.getConnection(), id, xVals, yVals);
    }

    public int updateTabulatedFunctionIndex(String owner_name, long id, int index, double y_val)
            throws SmartConnectionException, SQLRepositoryException {

        if(!UserRepo.exists(connection.getConnection(), null, owner_name))
            throw new ServiceArgumentsException("Owner with name " + owner_name + " does not exist");

        long userId = UserRepo.readUserId(connection.getConnection(), new String[]{owner_name}, "-").get(0).id();

        if(!checkMFunctionBelongs(userId, id))
            throw new ServiceArgumentsException("Function with id " + id + " does not belong to user with name " + owner_name);

        String funcType = getFunctionType(id);
        if(!funcType.equals("tabulated"))
            throw new ServiceArgumentsException("Function with id " + id + " is not a tabulated function");

        return TabulatedRepo.updateTabulatedFunctionIndex(connection.getConnection(), id, index, y_val);
    }

    public int updateTabulatedFunctionFull(String owner_name, long id, double[] xVals, double[] yVals)
            throws SmartConnectionException, SQLRepositoryException {

        if(!UserRepo.exists(connection.getConnection(), null, owner_name))
            throw new ServiceArgumentsException("Owner with name " + owner_name + " does not exist");

        long userId = UserRepo.readUserId(connection.getConnection(), new String[]{owner_name}, "-").get(0).id();

        if(!checkMFunctionBelongs(userId, id))
            throw new ServiceArgumentsException("Function with id " + id + " does not belong to user with name " + owner_name);

        String funcType = getFunctionType(id);
        if(!funcType.equals("tabulated"))
            throw new ServiceArgumentsException("Function with id " + id + " is not a tabulated function");

        try{
            factory.create(xVals,yVals);
        }
        catch(ArrayIsNotSortedException | DifferentLengthOfArraysException Err){
            throw new ServiceArgumentsException(Err.getMessage());
        }

        return TabulatedRepo.updateTabulatedFunctionFull(connection.getConnection(), id, xVals, yVals);
    }

    /// ========================================DELETE============================================
    public int removeMFunction(long owner_id, long id)
            throws SmartConnectionException, SQLRepositoryException {

        if(!checkMFunctionBelongs(owner_id, id))
            throw new ServiceArgumentsException("Function with id " + id + " does not belong to user with id " + owner_id);


        int res = MathRepo.removeMFunc(connection.getConnection(), owner_id, id);
        StatRepo.updateStatFuncID(connection.getConnection(),owner_id);
        return res;
    }

    public int removeMFunction(String owner_name, long id)
            throws SmartConnectionException, SQLRepositoryException {

        if(!UserRepo.exists(connection.getConnection(), null, owner_name))
            throw new ServiceArgumentsException("Owner with name " + owner_name + " does not exist");

        long userId = UserRepo.readUserId(connection.getConnection(), new String[]{owner_name},"-").get(0).id();

        if(!checkMFunctionBelongs(userId, id))
            throw new ServiceArgumentsException("Function with id " + id + " does not belong to user with name " + owner_name);

        int res = removeMFunction(userId, id);
        StatRepo.updateStatFunc(connection.getConnection(),owner_name);
        return res;
    }

    /// ========================================CHECK============================================

    public boolean checkMFunctionsExist(long[] id)
            throws SmartConnectionException, SQLRepositoryException {

        for(int i = 0; i < id.length; ++i) {
            var result = MathRepo.readMFuncInfo(connection.getConnection(), new long[]{id[i]}, "-", "-");
            if(result.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean checkMFunctionBelongs(long owner_id, long id)
            throws SmartConnectionException, SQLRepositoryException {

        return MathRepo.BelongsByOwnerId(connection.getConnection(), id, owner_id);
    }

    public boolean checkMFunctionBelongs(String owner_name, long id)
            throws SmartConnectionException, SQLRepositoryException {

        if(!UserRepo.exists(connection.getConnection(), null, owner_name))
            throw new ServiceArgumentsException("Owner with name " + owner_name + " does not exist");

        long userId = UserRepo.readUserId(connection.getConnection(), new String[]{owner_name}, "-").get(0).id();
        return MathRepo.BelongsByOwnerId(connection.getConnection(), id, userId);
    }

    private String getFunctionType(long id)
            throws SmartConnectionException, SQLRepositoryException {

        var result = MathRepo.readMFuncInfo(connection.getConnection(), new long[]{id}, "-", "-");
        if(result.isEmpty()) {
            throw new ServiceArgumentsException("Function with id " + id + " does not exist");
        }
        return result.get(0).type();
    }

}