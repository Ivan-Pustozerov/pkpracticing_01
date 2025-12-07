package SQL.repositories;

import SQL.DTO.AnalyticFunctionDTO;
import SQL.SQLRepositoryException;
import SQL.repositories.tools.Repository;
import SQL.repositories.tools.SQLArray;

import java.sql.Connection;
import java.util.ArrayList;

public class AnalyticFunctionsRepository extends Repository {
    private final String dir = "scripts/";
    private final String AnalyticFunctionInit;
    private final String AnalyticFunctionInsert;
    private final String AnalyticFunctionReadInfo;
    private final String AnalyticFunctionUpdate;

    {
        AnalyticFunctionInit = readCommand(dir + "BD_INIT/BD_INIT_Analytic_Functions_table.sql");
        AnalyticFunctionInsert = readCommand(dir + "BD_INSERT/BD_INSERT_Analytic_Function.sql");
        AnalyticFunctionReadInfo = readCommand(dir + "BD_READ/BD_READ_Analytic_Function.sql");
        AnalyticFunctionUpdate = readCommand(dir + "BD_UPDATE/BD_UPDATE_Analytic_Function.sql");
    }
///================================================================================================================

///---------------------------------------------------UPDATER------------------------------------------------------
    public int initTable(Connection connect)
            throws SQLRepositoryException {
        return initTable(connect, AnalyticFunctionInit);
    }

    public int insertAnalyticFunction(Connection connect, long func_id, String function_expression)
            throws SQLRepositoryException{
        return executeUpdate(connect, AnalyticFunctionInsert, ps -> { ps.setLong(1, func_id);
                                                                  ps.setString(2, function_expression);});
    }

    public int updateAnalyticFunction(Connection connect, long func_id, String function_expression)
            throws SQLRepositoryException{
        return executeUpdate(connect, AnalyticFunctionUpdate, ps -> {
                                                                    ps.setString(1, function_expression);
                                                                    ps.setLong(2, func_id);
        });
    }

///-------------------------------------------------READER---------------------------------------------------------
    public ArrayList<AnalyticFunctionDTO> readAnalyticFunctionInfo(Connection connect, long[] func_id)
            throws SQLRepositoryException{
        try(SQLArray func_idArray = toLongSQLArray(connect, func_id)) {
            return executeQuery(connect, AnalyticFunctionReadInfo,
                    ps -> ps.setArray(1, func_idArray.innerArray()),
                    set -> {
                        return new AnalyticFunctionDTO(
                                set.getLong("func_id"),
                                set.getString("function_expression"));
                    });
        }
    }
}
