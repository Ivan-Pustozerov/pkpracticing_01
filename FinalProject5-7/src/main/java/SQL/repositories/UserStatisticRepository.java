package SQL.repositories;

import SQL.DTO.ToClient.StatisticToClientDTO;
import SQL.repositories.tools.Repository;

import java.sql.Connection;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;

public class UserStatisticRepository extends Repository {
    private final String dir = "scripts/";
    private final String StatInit;
    private final String StatInsert;
    private final String StatReadId;
    private final String StatReadName;
    private final String StatUpdateTimeID;
    private final String StatUpdateTimeName;
    private final String StatUpdateFuncs;

    {
        StatInit = readCommand(dir + "BD_INIT/BD_INIT_UserStatistic.sql");
        StatInsert = readCommand(dir + "BD_INSERT/BD_INSERT_UserStatistic.sql");
        StatReadId = readCommand(dir + "BD_READ/BD_READ_UserStatistic_id.sql");
        StatReadName = readCommand(dir + "BD_READ/BD_READ_UserStatistic_name.sql");
        StatUpdateTimeID = readCommand(dir + "BD_UPDATE/BD_UPDATE_UserStatistic_avrg_time_day_ID.sql");
        StatUpdateTimeName = readCommand(dir + "BD_UPDATE/BD_UPDATE_UserStatistic_avrg_time_day_Name.sql");
        StatUpdateFuncs = readCommand(dir + "BD_UPDATE/BD_UPDATE_UserStatistic_func_count.sql");
    }

    public int initTable(Connection connect)
            throws SQLRepositoryException {
        return initTable(connect,StatInit);
    }

    public int insertStat(Connection connect, String username)
            throws SQLRepositoryException {
        return executeUpdate(connect, StatInsert, ps -> ps.setString(1,username));
    }

    public ArrayList<StatisticToClientDTO> readStatById(Connection connect, long id)
            throws SQLRepositoryException {
        return executeQuery(connect, StatReadId, ps -> {ps.setLong(1,id);},
                set -> {return new StatisticToClientDTO(
                        set.getLong("id"),
                        set.getTimestamp("reg_time").toLocalDateTime(),
                        set.getTime("avrg_time_day").toLocalTime(),
                        set.getInt("func_count"));
        });
    }
    public ArrayList<StatisticToClientDTO> readStatByName(Connection connect, String name)
            throws SQLRepositoryException {
        return executeQuery(connect, StatReadName, ps -> {
                    ps.setString(1, name);
                },
                set -> {
                    return new StatisticToClientDTO(
                            set.getLong("id"),
                            set.getTimestamp("reg_time").toLocalDateTime(),
                            set.getTime("avrg_time_day").toLocalTime(),
                            set.getInt("func_count"));
                });
    }

    public int updateStatTimeById(Connection connect, long id, LocalTime avrg_time_day)
            throws SQLRepositoryException {
        return executeUpdate(connect, StatUpdateTimeID, ps -> {
            ps.setTime(1, Time.valueOf(avrg_time_day));
            ps.setLong(2,id);});
    }

    public int updateStatTimeByName(Connection connect, String username, LocalTime avrg_time_day)
            throws SQLRepositoryException {
        return executeUpdate(connect, StatUpdateTimeName, ps -> {
            ps.setTime(1, Time.valueOf(avrg_time_day));
            ps.setString(2,username);});
    }

    public int updateStatFunc(Connection connect, String name)
            throws SQLRepositoryException {
        return executeUpdate(connect, StatUpdateFuncs, ps -> {
            ps.setString(1, name);
            ps.setString(2, name);
        });
    }
}
