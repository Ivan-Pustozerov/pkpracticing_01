package SQL.Server.exception;

import java.io.IOException;

public class BadRequest400 extends IOException {
    public BadRequest400() {
        super("Неверные параметры запроса");
    }
}
