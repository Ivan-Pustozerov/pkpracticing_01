package SQL.repositories;

import SQL.DTO.DTO;
import SQL.DTO.IdDTO;
import SQL.DTO.MathFunctionDTO;
import SQL.SQLRepositoryException;
import SQL.repositories.tools.Repository;
import SQL.repositories.tools.SQLArray;

import java.sql.Connection;
import java.util.ArrayList;

public class MathFunctionsRepository extends Repository {
 ///===========================================СЛУЖЕБНЫЕ=СОСТОЯНИЯ=================================================
    private final String dir = "scripts/";
    private final String MFuncInit;
    private final String MFuncInsert;
    private final String MFuncReadInfoAsc;
    private final String MFuncReadInfoDesc;
    private final String MFuncRemove;
    private final String MFuncUpdate;

    {
        MFuncInit = readCommand(dir + "BD_INIT/BD_INIT_MathFunctions_table.sql");
        MFuncInsert = readCommand(dir + "BD_INSERT/BD_INSERT_MathFunction.sql");
        MFuncReadInfoAsc = readCommand(dir + "BD_READ/BD_READ_MathFunction_infoASC.sql");
        MFuncReadInfoDesc = readCommand(dir + "BD_READ/BD_READ_MathFunction_infoDESC.sql");
        MFuncRemove = readCommand(dir + "BD_REMOVE/BD_REMOVE_MathFunction.sql");
        MFuncUpdate = readCommand(dir + "BD_UPDATE/BD_UPDATE_MathFunction.sql");
    }
///=================================================================================================================

///---------------------------------------------------UPDATER-------------------------------------------------------
    public int initTable(Connection connect)
            throws SQLRepositoryException {
        return super.initTable(connect, MFuncInit);
    }

    public ArrayList<IdDTO> insertMFunc(Connection connect, String type, String name, long owner_id)
            throws SQLRepositoryException{
        return executeQuery(connect, MFuncInsert, ps -> { ps.setString(1, type);
                                                                  ps.setString(2, name);
                                                                  ps.setLong(3, owner_id);},
                set -> {return new IdDTO(set.getLong("id"));});
    }

    public int removeMFunc(Connection connect, long owner_id, long id)
            throws SQLRepositoryException{
        return executeUpdate(connect, MFuncRemove, ps -> {ps.setLong(1, owner_id);
                                                                          ps.setLong(2, id);}
        );
    }

    public int updateMFunc(Connection connect,long owner_id, long id, String new_name)
            throws SQLRepositoryException{
        return executeUpdate(connect, MFuncUpdate, ps ->{
                                                                    ps.setString(1, new_name);
                                                                    ps.setLong(2, id);
                                                                    ps.setLong(3, owner_id);
        });
    }

///-------------------------------------------------READER---------------------------------------------------------
    public ArrayList<MathFunctionDTO> readMFuncInfo(Connection connect, long[] id, String  sortField, String sortOrder)
            throws SQLRepositoryException{
        try(SQLArray idArray = toLongSQLArray(connect, id)) {

            return executeQuery(connect,sortOrder.equals("desc")? MFuncReadInfoDesc : MFuncReadInfoAsc,
                    ps -> {
                                ps.setArray(1, idArray.innerArray());
                                ps.setString(2, sortField);
                    },
                    set -> {
                        return new MathFunctionDTO(set.getLong("id"),
                                set.getString("type"),
                                set.getString("name"),
                                set.getLong("owner_id"));
                    });
        }
    }
}
