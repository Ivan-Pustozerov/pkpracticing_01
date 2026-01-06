package SQL.Services;

import SQL.repositories.tools.SQLRepositoryException;
import SQL.repositories.tools.SmartConnectionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService = new UserService("jdbc:postgresql://localhost:5432/Final",
            "postgres", "lkroot");

    UserServiceTest() throws SmartConnectionException, SQLRepositoryException {}

    @Test
    void initDataBase() throws SmartConnectionException, SQLRepositoryException {
        //userService.initDataBase();
    }
}