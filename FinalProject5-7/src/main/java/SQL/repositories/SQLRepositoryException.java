package SQL.repositories;

import java.sql.SQLException;

public class SQLRepositoryException extends SQLException {
    public SQLRepositoryException(String message) {
        super(message);
    }
}
