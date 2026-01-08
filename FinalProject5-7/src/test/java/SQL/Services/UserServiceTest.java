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

    @Test
    void authenticateUser() throws SmartConnectionException, SQLRepositoryException {
        String token = userService.authenticateUser(2,"qwerty");
        System.out.println(token);
        //eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIyIiwiaWF0IjoxNzY3ODY5MTcxLCJleHAiOjE3Njc5NTU1NzF9.n_IxfG4dPaLJKBHTtAaKAn4038bYC6fBIU4Q5m9b8isao1FOnffvxHpvf0fuHS_i
        //eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIyIiwiaWF0IjoxNzY3ODY5MjA2LCJleHAiOjE3Njc5NTU2MDZ9.bQFdS7CKFthUSTJ5am14GECFmnPg_b3lNtg35B4Q55dgejBfQTMp3__LQ67v96Ye
    }
}