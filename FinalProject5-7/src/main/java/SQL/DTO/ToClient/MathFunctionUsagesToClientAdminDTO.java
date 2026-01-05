package SQL.DTO.ToClient;

import SQL.DTO.DTO;

public record MathFunctionUsagesToClientAdminDTO
        (long owner_id, long func_id, int usages)
        implements DTO {}
