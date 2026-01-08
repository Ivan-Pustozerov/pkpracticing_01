package SQL.Server.exception;

import SQL.DTO.responseDTO.ErrorResponse;

import java.io.IOException;
import java.time.LocalDateTime;

public class NotFound404 extends ServerError{
    protected int code = 404;
    public NotFound404() {
        super("Запрашиваемый ресурс не найден");
    }

    @Override
    public ErrorResponse getErrorResponse(String path) {
        return new ErrorResponse(LocalDateTime.now(),404,"Запрашиваемый ресурс не найден",path);
    }
}
