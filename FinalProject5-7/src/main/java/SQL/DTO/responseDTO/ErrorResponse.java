package SQL.DTO.responseDTO;

import java.time.LocalDateTime;

public record ErrorResponse
        (LocalDateTime timestamp, String status, String error, String path)
        {}
