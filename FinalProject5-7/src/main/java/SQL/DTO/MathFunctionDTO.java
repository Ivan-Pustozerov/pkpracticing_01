package SQL.DTO;

public record MathFunctionDTO(long id, String type, String name, long owner_id)
        implements DTO{}
