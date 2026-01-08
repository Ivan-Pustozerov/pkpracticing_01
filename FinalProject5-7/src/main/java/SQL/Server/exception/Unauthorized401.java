package SQL.Server.exception;

import SQL.DTO.responseDTO.ErrorResponse;

import java.io.IOException;
import java.time.LocalDateTime;

public class Unauthorized401 extends ServerError{
    protected int code = 401;
    public Unauthorized401() {
        super("Пользователь не авторизован");
    }
    @Override
    public ErrorResponse getErrorResponse(String path) {
        return new ErrorResponse(LocalDateTime.now(),404,"Пользователь не авторизован",path);
    }
}
