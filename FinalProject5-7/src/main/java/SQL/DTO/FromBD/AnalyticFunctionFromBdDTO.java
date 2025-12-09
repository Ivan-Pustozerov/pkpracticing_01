package SQL.DTO.FromBD;

import SQL.DTO.DTO;

public record AnalyticFunctionFromBdDTO(long func_id, String function_expression)
        implements DTO {}