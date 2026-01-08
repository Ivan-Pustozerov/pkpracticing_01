package SQL.Server.exception;

import SQL.DTO.responseDTO.ErrorResponse;

import java.io.IOException;
import java.time.LocalDateTime;

public class Forbidden403 extends ServerError{
    protected int code = 403;
    public Forbidden403() {
        super("Недостаточно прав");
    }
    @Override
    public ErrorResponse getErrorResponse(String path) {
        return new ErrorResponse(LocalDateTime.now(),403,"Недостаточно прав",path);
    }
}
