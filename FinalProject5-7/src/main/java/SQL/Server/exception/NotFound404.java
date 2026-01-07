package SQL.Server.exception;

import java.io.IOException;

public class NotFound404 extends IOException {
    public NotFound404() {
        super("Запрашиваемый ресурс не найден");
    }
}
