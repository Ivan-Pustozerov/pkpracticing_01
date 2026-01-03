package SQL.repositories.tools;

import java.sql.SQLException;

public class SQLRepositoryException extends SQLException {
    public SQLRepositoryException(String message) {
        super(message);
    }
}
