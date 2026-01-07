package SQL.repositories;

import SQL.DTO.IdDTO;
import SQL.DTO.FromBD.MathFunctionFromBdDTO;
import SQL.DTO.responseDTO.MathFunctionUsagesResponse;
import SQL.repositories.tools.Repository;
import SQL.repositories.tools.SQLArray;
import SQL.repositories.tools.SQLRepositoryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MathFunctionsRepository extends Repository {
 ///===========================================СЛУЖЕБНЫЕ=СОСТОЯНИЯ=================================================
    private final String dir = "scripts/";
    private final String MFuncInit;
    private final String MFuncInsert;
    private final String MFuncReadInfoAsc;
    private final String MFuncReadInfoDesc;
    private final String MFuncReadInfoByOwnerAsc;
    private final String MFuncReadInfoByOwnerDesc;
    private final String MFuncReadInfoAllAsc;
    private final String MFuncReadInfoAllDesc;
    private final String MFuncReadUsagesAll;
    private final String MFuncReadUsagesID;
    private final String MFuncRemove;
    private final String MFuncUpdate;
    private final String MFuncUpdateUsages;
    private final String MFuncBelongs;

    {
        MFuncInit = readCommand(dir + "BD_INIT/BD_INIT_MathFunctions_table.sql");
        MFuncInsert = readCommand(dir + "BD_INSERT/BD_INSERT_MathFunction.sql");
        MFuncReadInfoAsc = readCommand(dir + "BD_READ/BD_READ_MathFunction_infoASC.sql");
        MFuncReadInfoDesc = readCommand(dir + "BD_READ/BD_READ_MathFunction_infoDESC.sql");
        MFuncReadInfoByOwnerAsc = readCommand(dir + "BD_READ/BD_READ_MathFunction_By_OwnerIDASC.sql");
        MFuncReadInfoByOwnerDesc = readCommand(dir + "BD_READ/BD_READ_MathFunction_By_OwnerIDDESC.sql");
        MFuncReadInfoAllAsc = readCommand(dir + "BD_READ/BD_READ_MathFunction_infoAllASC.sql");
        MFuncReadInfoAllDesc = readCommand(dir + "BD_READ/BD_READ_MathFunction_infoAllDESC.sql");
        MFuncReadUsagesAll = readCommand(dir + "BD_READ/BD_READ_MathFunction_usages_All_ID.sql");
        MFuncReadUsagesID = readCommand(dir + "BD_READ/BD_READ_MathFunction_usages_func_ID.sql");
        MFuncRemove = readCommand(dir + "BD_REMOVE/BD_REMOVE_MathFunction.sql");
        MFuncUpdate = readCommand(dir + "BD_UPDATE/BD_UPDATE_MathFunction.sql");
        MFuncUpdateUsages = readCommand(dir + "BD_UPDATE/BD_UPDATE_MathFunction_usages.sql");
        MFuncBelongs = readCommand(dir + "BD_CHECK/BD_CHECK_MathFunction_Belongs.sql");
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
            throws SQLRepositoryException {
        return executeUpdate(connect, MFuncUpdate, ps ->{
                                                                    ps.setString(1, new_name);
                                                                    ps.setLong(2, id);
                                                                    ps.setLong(3, owner_id);
        });
    }

    public int updateMFuncUsages(Connection connect, long owner_id, long id)
            throws SQLRepositoryException {
        return executeUpdate(connect, MFuncUpdateUsages, ps ->{
            ps.setLong(1, id);
            ps.setLong(2, owner_id);
        });
    }


///-------------------------------------------------READER---------------------------------------------------------
    public ArrayList<MathFunctionFromBdDTO> readMFuncInfo(Connection connect, long[] id, String  sortField, String sortOrder)
            throws SQLRepositoryException{
        try(SQLArray idArray = toLongSQLArray(connect, id)) {

            return executeQuery(connect,sortOrder.equals("desc")? MFuncReadInfoDesc : MFuncReadInfoAsc,
                    ps -> {
                                ps.setArray(1, idArray.innerArray());
                                ps.setString(2, sortField);
                    },
                    set -> {
                        return new MathFunctionFromBdDTO(
                                set.getLong("id"),
                                set.getString("type"),
                                set.getString("name"),
                                set.getLong("owner_id"));
                    });
        }
    }

    public ArrayList<MathFunctionFromBdDTO> readMFuncInfoAll(Connection connect, String  sortField, String sortOrder)
            throws SQLRepositoryException {
        return executeQuery(connect,sortOrder.equals("desc")? MFuncReadInfoAllDesc : MFuncReadInfoAllAsc,
                ps ->{ ps.setString(1, sortField);},
                set -> {
                    return new MathFunctionFromBdDTO(
                            set.getLong("id"),
                            set.getString("type"),
                            set.getString("name"),
                            set.getLong("owner_id"));
                    });
    }

    public ArrayList<MathFunctionFromBdDTO> readMFuncInfoByOwnerId(Connection connect, long owner_id, String sortField, String sortOrder)
            throws SQLRepositoryException {
        return executeQuery(connect, sortOrder.equals("desc")? MFuncReadInfoByOwnerDesc : MFuncReadInfoByOwnerAsc,
                ps ->{
                                ps.setLong(1, owner_id);
                                ps.setString(2, sortField);},
                set -> {
                    return new MathFunctionFromBdDTO(
                            set.getLong("id"),
                            set.getString("type"),
                            set.getString("name"),
                            set.getLong("owner_id"));
                });
    }

    public ArrayList<MathFunctionUsagesResponse> readMFuncUsagesAllByOwnerId(Connection connect, long owner_id)
            throws SQLRepositoryException {
        return executeQuery(connect, MFuncReadUsagesAll,
                ps ->{
                    ps.setLong(1, owner_id);},
                set -> {
                    return new MathFunctionUsagesResponse(
                            owner_id,
                            set.getLong("id"),
                            set.getInt("usages"));
                });

    }

    public ArrayList<MathFunctionUsagesResponse> readMFuncUsagesByOwnerId(Connection connect, long owner_id, long func_id)
            throws SQLRepositoryException {
        return executeQuery(connect, MFuncReadUsagesID,
                ps ->{
                    ps.setLong(1, func_id);
                    ps.setLong(2, owner_id);},
                set -> {
                    return new MathFunctionUsagesResponse(
                            owner_id,
                            set.getLong("id"),
                            set.getInt("usages"));
                });

    }


///-------------------------------------------------CHECK----------------------------------------------------------
    public boolean BelongsByOwnerId(Connection connect, Long id, Long owner_id )
            throws SQLRepositoryException {
        try(PreparedStatement ps = connect.prepareStatement(MFuncBelongs)) {
            ps.setLong(1, id);
            ps.setLong(2, owner_id);

            try (ResultSet set = ps.executeQuery()) {
                return set.next();
            }

        }catch (SQLException e){
            throw new SQLRepositoryException("Belongs exist error");
        }
    }
}
