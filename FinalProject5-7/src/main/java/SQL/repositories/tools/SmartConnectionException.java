package SQL.repositories.tools;

import java.sql.SQLException;

public class SmartConnectionException extends SQLException {
    public SmartConnectionException(String message) {
        super(message);
    }
}
