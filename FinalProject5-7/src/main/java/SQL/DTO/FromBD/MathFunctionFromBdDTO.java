package SQL.DTO.FromBD;

public record MathFunctionFromBdDTO(long id, String type, String name, long owner_id)
        implements DTO {}
