package SQL.Server.exception;

import java.io.IOException;

public class Forbidden403 extends IOException {
    public Forbidden403() {
        super("Недостаточно прав");
    }
}
