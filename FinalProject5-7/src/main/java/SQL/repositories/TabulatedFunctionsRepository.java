package SQL.repositories;

import SQL.DTO.TabulatedFunctionToServerDTO;
import SQL.SQLRepositoryException;
import SQL.repositories.tools.Repository;
import SQL.repositories.tools.SQLArray;

import java.sql.Connection;
import java.util.ArrayList;

public class TabulatedFunctionsRepository extends Repository {
    ///===========================================СЛУЖЕБНЫЕ=СОСТОЯНИЯ================================================
    private final String dir = "scripts/";
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
///================================================================================================================

///---------------------------------------------------UPDATER------------------------------------------------------
    public int initTable(Connection connect)
            throws SQLRepositoryException {
        return initTable(connect, TabulatedInit);
    }

    /// МОЖЕТ ОЧЕНЬ СИЛЬНО РАЗОЧАРОВАТЬ - ПРОБЛЕМЫ НЕ МОИ - СОЗДАТЕЛЯ java.sql.Array
    public int insertTabulatedFunction(Connection connect, long func_id, double[] xVals, double[] yVals)
            throws SQLRepositoryException {

        try(SQLArray sqlXvals = toDoubleSQLArray(connect,xVals);
            SQLArray sqlYvals = toDoubleSQLArray(connect,yVals)){

            return executeUpdate(connect, TabulatedInsert, ps -> {
                                                                    ps.setLong(1, func_id);
                                             if(sqlXvals.isAlive()) ps.setArray(2, sqlXvals.innerArray());
                                             if(sqlYvals.isAlive()) ps.setArray(3, sqlYvals.innerArray());});
        }
    }

    public int updateTabulatedFunctionIndex(Connection connect, long func_id, int index, double y_val)
            throws SQLRepositoryException{
        return executeUpdate(connect, TabulatedUpdateIndex, ps -> {
                                                                ps.setInt(1, index);
                                                                ps.setDouble(2, y_val);
                                                                ps.setInt(3, index);
                                                                ps.setLong(4, func_id);
        });
    }

    /// МОЖЕТ ОЧЕНЬ СИЛЬНО РАЗОЧАРОВАТЬ - ПРОБЛЕМЫ НЕ МОИ - СОЗДАТЕЛЯ java.sql.Array
    public int updateTabulatedFunctionFull(Connection connect, long id, double[] xVals, double[] yVals)
            throws SQLRepositoryException {

        try(SQLArray sqlXvals = toDoubleSQLArray(connect, xVals);
            SQLArray sqlYvals = toDoubleSQLArray(connect, yVals)){

            return executeUpdate(connect, TabulatedUpdateFull, ps -> {
                                                if(sqlXvals.isAlive()){ps.setArray(1, sqlXvals.innerArray());}
                                                if(sqlYvals.isAlive()){ps.setArray(2, sqlYvals.innerArray());}
                                                                       ps.setLong(3, id);
            });
        }

    }

///-------------------------------------------------READER---------------------------------------------------------
    public ArrayList<TabulatedFunctionToServerDTO> readTabulatedFunctionInfo(Connection connect, long[] func_id)
            throws SQLRepositoryException{
        try( SQLArray func_idArray = toLongSQLArray(connect, func_id)) {

            return executeQuery(connect, TabulatedReadInfo,
                    ps -> ps.setArray(1, func_idArray.innerArray()),
                    set -> {
                        return new TabulatedFunctionToServerDTO(
                                set.getLong("func_id"),
                                toDoubleBaseArray(set.getArray("xVals")),
                                toDoubleBaseArray(set.getArray("yVals")));
                    }
            );
        }
    }
}

