package SQL.repositories;

import SQL.DTO.ToClient.StatisticToClientDTO;
import SQL.repositories.tools.Repository;
import SQL.repositories.tools.SQLRepositoryException;

import java.sql.Connection;
import java.sql.Time;
import java.time.Duration;
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
    private final String StatUpdateAllTimeName;
    private final String StatUpdateAllTimeID;
    private final String StatUpdateFuncs;
    private final String StatUpdateFuncsID;

    {
        StatInit = readCommand(dir + "BD_INIT/BD_INIT_UserStatistic.sql");
        StatInsert = readCommand(dir + "BD_INSERT/BD_INSERT_UserStatistic.sql");
        StatReadId = readCommand(dir + "BD_READ/BD_READ_UserStatistic_id.sql");
        StatReadName = readCommand(dir + "BD_READ/BD_READ_UserStatistic_name.sql");
        StatUpdateTimeID = readCommand(dir + "BD_UPDATE/BD_UPDATE_UserStatistic_avrg_time_day_ID.sql");
        StatUpdateTimeName = readCommand(dir + "BD_UPDATE/BD_UPDATE_UserStatistic_avrg_time_day_Name.sql");
        StatUpdateAllTimeID = readCommand(dir + "BD_UPDATE/BD_UPDATE_UserStatistic_all_time_ID.sql");
        StatUpdateAllTimeName = readCommand(dir + "BD_UPDATE/BD_UPDATE_UserStatistic_all_time_Name.sql");
        StatUpdateFuncs = readCommand(dir + "BD_UPDATE/BD_UPDATE_UserStatistic_func_count.sql");
        StatUpdateFuncsID = readCommand(dir + "BD_UPDATE/BD_UPDATE_UserStatistic_func_count_ID.sql");
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
                        Duration.parse(set.getString("all_time")),
                        Duration.parse(set.getString("avrg_time_day")),
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
                            Duration.parse(set.getString("all_time")),
                            Duration.parse(set.getString("avrg_time_day")),
                            set.getInt("func_count"));
                });
    }

    public int updateStatAvrgTimeById(Connection connect, long id, Duration avrg_time_day)
            throws SQLRepositoryException {
        return executeUpdate(connect, StatUpdateTimeID, ps -> {
            ps.setString(1, avrg_time_day.toString());
            ps.setLong(2,id);});
    }

    public int updateStatAvrgTimeByName(Connection connect, String username, Duration avrg_time_day)
            throws SQLRepositoryException {
        return executeUpdate(connect, StatUpdateTimeName, ps -> {
            ps.setString(1, avrg_time_day.toString());
            ps.setString(2,username);});
    }

    public int updateStatAllTimeById(Connection connect, long id, Duration add_time)
            throws SQLRepositoryException {
        return executeUpdate(connect, StatUpdateAllTimeID, ps -> {
            ps.setString(1, add_time.toString());
            ps.setLong(2,id);});
    }

    public int updateStatAllTimeByName(Connection connect, String name, Duration add_time)
            throws SQLRepositoryException {
        return executeUpdate(connect, StatUpdateAllTimeName, ps -> {
            ps.setString(1, add_time.toString());
            ps.setString(2,name);});
    }

    public int updateStatFunc(Connection connect, String name)
            throws SQLRepositoryException {
        return executeUpdate(connect, StatUpdateFuncs, ps -> {
            ps.setString(1, name);
            ps.setString(2, name);
        });
    }

    public int updateStatFuncID(Connection connect, long id)
            throws SQLRepositoryException {
        return executeUpdate(connect, StatUpdateFuncsID, ps -> {
            ps.setLong(1, id);
            ps.setLong(2, id);
        });
    }
}
