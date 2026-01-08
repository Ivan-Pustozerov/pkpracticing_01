package SQL.Server.exception;

import SQL.DTO.responseDTO.ErrorResponse;

import java.io.IOException;
import java.time.LocalDateTime;

public class Conflict409 extends ServerError{
    protected int code = 409;
    public Conflict409() {
        super("Пользователь с таким именем или email уже существует");
    }
    @Override
    public ErrorResponse getErrorResponse(String path) {
        return new ErrorResponse(LocalDateTime.now(),409,"Пользователь с таким именем или email уже существует",path);
    }
}
