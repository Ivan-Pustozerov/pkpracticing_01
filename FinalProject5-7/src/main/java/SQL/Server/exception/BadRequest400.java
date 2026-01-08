package SQL.Server.exception;

import SQL.DTO.responseDTO.ErrorResponse;

import java.io.IOException;
import java.time.LocalDateTime;

public class BadRequest400 extends ServerError{
    protected int code = 400;
    public BadRequest400() {
        super("Неверные параметры запроса");
    }

    @Override
    public ErrorResponse getErrorResponse(String path) {
        return new ErrorResponse(LocalDateTime.now(),400,"Неверные параметры запроса",path);
    }
}
