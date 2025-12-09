package SQL.repositories.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SmartConnection {
    private final String url;
    private final String username;
    private final String password;
    private Connection connection;

    public SmartConnection(String url, String username, String password)
            throws SmartConnectionException {
        this.url = url;
        this.username = username;
        this.password = password;
        try {
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(true);

        } catch (SQLException e) {
            try {
                connection.close();
            } catch (SQLException ex) {
                System.out.println("Connection Close Error");
            }
            throw new SmartConnectionException("Connection Error");
        }
    }

    public Connection getConnection()
            throws SmartConnectionException {
        try {
            if(!connection.isValid(2)){
                connection = DriverManager.getConnection(url,username,password);
            }
            return connection;

        } catch (SQLException e) {
            throw new SmartConnectionException("Connection Get Error");
        }
    }
}
