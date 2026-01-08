package SQL.Server.exception;

import SQL.DTO.responseDTO.ErrorResponse;

import java.io.IOException;

public abstract class ServerError extends IOException{
    protected int code;
    ErrorResponse getErrorResponse(String path) {
        return null;
    }
    public ServerError(String message) {
        super(message);
    }
    public int code(){
        return code;
    }
}
