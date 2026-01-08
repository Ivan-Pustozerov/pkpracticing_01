package SQL.Server.exception;

import SQL.DTO.responseDTO.ErrorResponse;

import java.io.IOException;

public abstract class ServerError extends IOException{
    protected int code;
    abstract public ErrorResponse getErrorResponse(String path);
    public ServerError(String message) {
        super(message);
    }
    public int code(){
        return code;
    }
}
