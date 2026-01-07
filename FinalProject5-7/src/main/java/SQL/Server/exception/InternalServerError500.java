package SQL.Server.exception;

import java.io.IOException;

public class InternalServerError500 extends IOException {
    public InternalServerError500() {
        super("Ошибка сервера");
    }
}
