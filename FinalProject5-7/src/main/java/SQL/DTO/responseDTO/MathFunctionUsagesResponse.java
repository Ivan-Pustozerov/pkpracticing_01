package SQL.DTO.responseDTO;

import SQL.DTO.FromBD.DTO;

public record MathFunctionUsagesResponse
        (long owner_id, long func_id, int usages)
        implements DTO {}
