package SQL.DTO.responseDTO;

import java.time.LocalDateTime;

public record ErrorResponse
        (LocalDateTime timestamp, int status, String error, String path)
        {}
