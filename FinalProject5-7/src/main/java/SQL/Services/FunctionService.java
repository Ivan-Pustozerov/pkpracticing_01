package SQL.Services;

import SQL.DTO.ToClient.MathFunctionToClientAdminDTO;
import SQL.Mappers.MFunctionMapper;
import SQL.repositories.AnalyticFunctionsRepository;
import SQL.repositories.MathFunctionsRepository;
import SQL.repositories.TabulatedFunctionsRepository;
import SQL.repositories.UsersRepository;
import SQL.repositories.tools.SQLRepositoryException;
import SQL.repositories.tools.SmartConnection;
import SQL.repositories.tools.SmartConnectionException;

import java.util.ArrayList;


public class FunctionService {

    private final SmartConnection connection;
    private final MathFunctionsRepository MathRepo = new MathFunctionsRepository();
    private final AnalyticFunctionsRepository AnalyticRepo = new AnalyticFunctionsRepository();
    private final TabulatedFunctionsRepository TabulatedRepo = new TabulatedFunctionsRepository();
    private final UsersRepository UserRepo = new UsersRepository();


    public FunctionService(String url, String username, String password)
            throws SmartConnectionException {
        connection = new SmartConnection(url,username,password);
    }

/// ========================================CREATE=============================================

    public void initDataBase()
            throws SmartConnectionException, SQLRepositoryException {
        MathRepo.initTable(connection.getConnection());
        AnalyticRepo.initTable(connection.getConnection());
        TabulatedRepo.initTable(connection.getConnection());
    }

    public int addAnalyticMFunction(String function_expression, String name, long owner_id)
            throws SQLRepositoryException, SmartConnectionException {

        long mfId = MathRepo.insertMFunc(connection.getConnection(), "analytic", name, owner_id).get(0).id();
        return AnalyticRepo.insertAnalyticFunction(connection.getConnection(), mfId, function_expression);
    }
    public int addAnalyticMFunction(String function_expression, String name, String owner_name)
            throws SQLRepositoryException, SmartConnectionException {

        long userId = UserRepo.readUserId(connection.getConnection(), new String[]{owner_name},"-").get(0).id();
        return addAnalyticMFunction(function_expression, name, userId);
    }


    public int addTabulatedMFunction(double[] xVals, double[] yVals, String name, long owner_id)
            throws SQLRepositoryException, SmartConnectionException {

        long mfId = MathRepo.insertMFunc(connection.getConnection(), "tabulated", name, owner_id).get(0).id();
        return TabulatedRepo.insertTabulatedFunction(connection.getConnection(), mfId, xVals, yVals);
    }
    public int addTabulatedMFunction(double[] xVals, double[] yVals, String name, String owner_name)
            throws SQLRepositoryException, SmartConnectionException {

        long userId = UserRepo.readUserId(connection.getConnection(), new String[]{owner_name}, "-").get(0).id();
        return addTabulatedMFunction(xVals, yVals, name, userId);
    }


    public ArrayList<MathFunctionToClientAdminDTO> readInfoAll(String orderField, String sortOrder)
            throws SmartConnectionException, SQLRepositoryException {
        var BDdto = MathRepo.readMFuncInfoAll(connection.getConnection(), orderField, sortOrder);
        return MFunctionMapper.translateToClientDTO(BDdto,connection, AnalyticRepo,TabulatedRepo);
    }


/// ==========================================DELETE============================================
    public int removeMFunction(long owner_id, long id)
            throws SmartConnectionException, SQLRepositoryException {

        return MathRepo.removeMFunc(connection.getConnection(), owner_id, id);
    }
    public int removeMFunction(String owner_name, long id)
            throws SmartConnectionException, SQLRepositoryException {

        long userId = UserRepo.readUserId(connection.getConnection(), new String[]{owner_name},"-").get(0).id();
        return removeMFunction(userId, id);
    }

}



