package SQL.repositories;

import SQL.DTO.responseDTO.ErrorsResponse;
import SQL.repositories.tools.Repository;
import SQL.repositories.tools.SQLRepositoryException;

import java.sql.Connection;
import java.util.ArrayList;

public class ErrorsRepository extends Repository {
    private final String dir = "scripts/";
    private final String ErrorsInit;
    private final String ErrorsInsert;
    private final String ErrorsReadTime;

    {
        ErrorsInit = readCommand(dir + "BD_INIT/BD_INIT_Errors.sql");
        ErrorsInsert = readCommand(dir + "BD_INSERT/BD_INSERT_Errors.sql");
        ErrorsReadTime = readCommand(dir + "BD_READ/BD_READ_Errors_time.sql");
    }

    public int initTable(Connection connect)
            throws SQLRepositoryException {
        return initTable(connect,ErrorsInit);
    }

    public int insertError(Connection connect, int code, String type)
            throws SQLRepositoryException {
        return executeUpdate(connect, ErrorsInsert, ps -> {
            ps.setInt(1, code);
            ps.setString(2, type);
        });
    }

    public ArrayList<ErrorsResponse> readErrorByTime(Connection connect, java.time.LocalDateTime time)
            throws SQLRepositoryException {
        return executeQuery(connect, ErrorsReadTime, ps -> {
                    ps.setTimestamp(1, java.sql.Timestamp.valueOf(time));
                },
                set -> {
                    return new ErrorsResponse(
                            set.getTimestamp("time").toLocalDateTime(),
                            set.getInt("code"),
                            set.getString("type"));
                });
    }

    public ArrayList<ErrorsResponse> readErrorByCode(Connection connect, int code)
            throws SQLRepositoryException {
        return executeQuery(connect, ErrorsReadTime, ps -> {
                    ps.setInt(1, code);
                },
                set -> {
                    return new ErrorsResponse(
                            set.getTimestamp("time").toLocalDateTime(),
                            set.getInt("code"),
                            set.getString("type"));
                });
    }
}
