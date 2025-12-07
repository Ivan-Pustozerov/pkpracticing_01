package SQL.repositories.tools;

import java.sql.SQLException;

public class ConnectPoolException extends SQLException {
    public ConnectPoolException(String message) {
        super(message);
    }
}
