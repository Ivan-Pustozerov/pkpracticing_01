package SQL.repositories;

import SQL.DTO.TabulatedFunctionToServerDTO;
import SQL.repositories.tools.Repository;
import SQL.repositories.tools.SQLArray;

import java.sql.SQLException;
import java.util.ArrayList;

public class TabulatedFunctionsRepository extends Repository {
    ///===========================================СЛУЖЕБНЫЕ=СОСТОЯНИЯ================================================
    private final String dir = "FinalProject5-7/src/main/resources/scripts/";
    private final String TabulatedInit;
    private final String TabulatedInsert;
    private final String TabulatedReadInfo;
    private final String TabulatedUpdateIndex;
    private final String TabulatedUpdateFull;

    {
        TabulatedInit = readCommand(dir + "BD_INIT/BD_INIT_Tabulated_Functions_table.sql");
        TabulatedInsert = readCommand(dir + "BD_INSERT/BD_INSERT_Tabulated_Function.sql");
        TabulatedReadInfo = readCommand(dir + "BD_READ/BD_READ_Tabulated_Function.sql");
        TabulatedUpdateIndex = readCommand(dir + "BD_UPDATE/BD_UPDATE_Tabulated_Function_index.sql");
        TabulatedUpdateFull = readCommand(dir + "BD_UPDATE/BD_UPDATE_Tabulated_Function_rewriteArrays.sql");
    }
    public TabulatedFunctionsRepository(String url, String username, String password){
        super(url,username,password);
    }
///================================================================================================================

///---------------------------------------------------UPDATER------------------------------------------------------
    public int initTable(){
        return initTable(TabulatedInit);
    }

    /// МОЖЕТ ОЧЕНЬ СИЛЬНО РАЗОЧАРОВАТЬ - ПРОБЛЕМЫ НЕ МОИ - СОЗДАТЕЛЯ java.sql.Array
    public int insertTabulatedFunction(long func_id, double[] xVals, double[] yVals) throws SQLException {

        try(SQLArray sqlXvals = new SQLArray(toDoubleSQLArray(xVals));
            SQLArray sqlYvals = new SQLArray(toDoubleSQLArray(yVals))){

            return executeUpdate(TabulatedInsert, ps -> {
                                                                    ps.setLong(1, func_id);
                                             if(sqlXvals.isAlive()) ps.setArray(2, sqlXvals.innerArray());
                                             if(sqlYvals.isAlive()) ps.setArray(3, sqlYvals.innerArray());});
        }
    }
    public int updateTabulatedFunctionIndex(long id, int x_index, double x_val, int y_index, double y_val){
        return executeUpdate(TabulatedUpdateIndex, ps -> { ps.setLong(1, id);
                                                                            ps.setInt(2, x_index);
                                                                            ps.setDouble(3, x_val);
                                                                            ps.setInt(4, y_index);
                                                                            ps.setDouble(5, y_val);});
    }
    /// МОЖЕТ ОЧЕНЬ СИЛЬНО РАЗОЧАРОВАТЬ - ПРОБЛЕМЫ НЕ МОИ - СОЗДАТЕЛЯ java.sql.Array
    public int updateTabulatedFunctionFull(long id, double[] xVals, double[] yVals) throws SQLException {

        try(SQLArray sqlXvals = new SQLArray(toDoubleSQLArray(xVals));
            SQLArray sqlYvals = new SQLArray(toDoubleSQLArray(yVals))){

            return executeUpdate(TabulatedUpdateFull, ps -> {ps.setLong(1, id);
                                                if(sqlXvals.isAlive())ps.setArray(2, sqlXvals.innerArray());
                                                if(sqlYvals.isAlive())ps.setArray(3, sqlYvals.innerArray());
            });
        }

    }

///-------------------------------------------------READER---------------------------------------------------------
    public ArrayList<TabulatedFunctionToServerDTO> readTabulatedFunctionInfo(long id){
        return executeQuery(TabulatedReadInfo, ps -> ps.setLong(1, id),
                                                    set ->{ return new TabulatedFunctionToServerDTO(
                                                                    set.getLong("id"),
                                                                    toDoubleBaseArray(set.getArray("xVals")),
                                                                    toDoubleBaseArray(set.getArray("yVals")));});
    }
}

