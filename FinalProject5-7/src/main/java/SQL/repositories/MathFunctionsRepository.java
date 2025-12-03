package SQL.repositories;

import SQL.DTO.DTO;
import SQL.DTO.IdDTO;
import SQL.DTO.MathFunctionDTO;
import SQL.SQLRepositoryException;
import SQL.repositories.tools.Repository;

import java.util.ArrayList;

public class MathFunctionsRepository extends Repository {
 ///===========================================СЛУЖЕБНЫЕ=СОСТОЯНИЯ=================================================
    private final String dir = "FinalProject5-7/src/main/resources/scripts/";
    private final String MFuncInit;
    private final String MFuncInsert;
    private final String MFuncReadInfo;
    private final String MFuncRemove;
    private final String MFuncUpdate;

    {
        MFuncInit = readCommand(dir + "BD_INIT/BD_INIT_MathFunctions_table.sql");
        MFuncInsert = readCommand(dir + "BD_INSERT/BD_INSERT_MathFunction.sql");
        MFuncReadInfo = readCommand(dir + "BD_READ/BD_READ_MathFunction_info.sql");
        MFuncRemove = readCommand(dir + "BD_REMOVE/BD_REMOVE_MathFunction.sql");
        MFuncUpdate = readCommand(dir + "BD_UPDATE/BD_UPDATE_MathFunction.sql");
    }

    public MathFunctionsRepository(String url, String username, String password){
        super(url, username, password);
    }
///=================================================================================================================

///---------------------------------------------------UPDATER-------------------------------------------------------
    public int initTable()
            throws SQLRepositoryException {
        return super.initTable(MFuncInit);
    }

    public ArrayList<IdDTO> insertMFunc(String type, String name, long owner_id)
            throws SQLRepositoryException{
        return executeQuery(MFuncInsert, ps -> { ps.setString(1, type);
                                                                  ps.setString(2,name);
                                                                  ps.setLong(3, owner_id);},
                set -> {return new IdDTO(set.getLong("id"));});
    }

    public int removeMFunc(long id)
            throws SQLRepositoryException{
        return executeUpdate(MFuncRemove, ps -> ps.setLong(1, id));
    }

    public int updateMFunc(long id, String new_name)
            throws SQLRepositoryException{
        return executeUpdate(MFuncUpdate, ps ->{ ps.setLong(1, id);
                                                                 ps.setString(2, new_name);});
    }

///-------------------------------------------------READER---------------------------------------------------------
    public ArrayList<MathFunctionDTO> readMFuncInfo(long id)
            throws SQLRepositoryException{
        return executeQuery(MFuncReadInfo, ps -> ps.setLong(1, id),
                set ->{ return new MathFunctionDTO( set.getLong("id"),
                        set.getString("type"),
                        set.getString("name"),
                        set.getLong("owner_id"));});
    }
}
