package SQL.Server.exception;

import SQL.DTO.responseDTO.ErrorResponse;

import java.io.IOException;
import java.time.LocalDateTime;

public class InternalServerError500 extends ServerError{
    protected int code = 500;
    public InternalServerError500() {
        super("Ошибка сервера");
    }
    @Override
    public ErrorResponse getErrorResponse(String path) {
        return new ErrorResponse(LocalDateTime.now(),500,"Ошибка сервера",path);
    }
}
