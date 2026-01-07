package SQL.DTO.responseDTO;

import SQL.DTO.FromBD.DTO;

import java.time.LocalDateTime;

public record ErrorsResponse
        (LocalDateTime time, int code, String type)
        implements DTO {}
