package SQL.Server.exception;

import java.io.IOException;

public class Unauthorized401 extends IOException {
    public Unauthorized401() {
        super("Пользователь не авторизован");
    }
}
