package SQL.DTO.responseDTO;

import SQL.DTO.FromBD.DTO;

public record MathFunctionUsagesResponse
        (Long owner_id, Long func_id, Integer usages)
        implements DTO {}
