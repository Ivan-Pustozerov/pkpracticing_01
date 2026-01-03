package SQL.DTO.ToClient;

import SQL.DTO.DTO;

import java.time.LocalDateTime;

public record ErrorsDTO
        (LocalDateTime time, int code, String type)
        implements DTO {}
