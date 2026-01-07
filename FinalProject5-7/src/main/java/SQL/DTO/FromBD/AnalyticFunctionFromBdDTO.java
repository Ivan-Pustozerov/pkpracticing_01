package SQL.DTO.FromBD;

public record AnalyticFunctionFromBdDTO(long func_id, String function_expression)
        implements DTO {}