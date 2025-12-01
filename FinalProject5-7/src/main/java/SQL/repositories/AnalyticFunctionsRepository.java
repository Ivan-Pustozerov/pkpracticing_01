package SQL.repositories;

import SQL.DTO.AnalyticFunctionDTO;
import SQL.repositories.tools.Repository;

import java.util.ArrayList;

public class AnalyticFunctionsRepository extends Repository {
    private final String dir = "FinalProject5-7/src/main/resources/scripts/";
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
    public AnalyticFunctionsRepository(String url, String username, String password){
        super(url,username,password);
    }
///================================================================================================================

///---------------------------------------------------UPDATER------------------------------------------------------
    public int initTable(){
        return initTable(AnalyticFunctionInit);
    }
    public int insertAnalyticFunction(long func_id, String function_expression){
        return executeUpdate(AnalyticFunctionInsert, ps -> { ps.setLong(1, func_id);
                                                                  ps.setString(2, function_expression);});
    }
    public int updateAnalyticFunction(long id, String function_expression){
        return executeUpdate(AnalyticFunctionUpdate, ps -> { ps.setLong(1, id);
                                                                              ps.setString(2, function_expression);});
    }

///-------------------------------------------------READER---------------------------------------------------------
    public ArrayList<AnalyticFunctionDTO> readAnalyticFunctionInfo(long id){
        return executeQuery(AnalyticFunctionReadInfo, ps -> ps.setLong(1, id),
                                                        set ->{ return new AnalyticFunctionDTO(
                                                                 set.getLong("id"),
                                                                 set.getString("function_expression"));});
    }
}
