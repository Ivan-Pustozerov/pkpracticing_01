package SQL.DTO.ToClient;

import SQL.DTO.DTO;
import SQL.DTO.FunctionData;
import functions.interfaces.TabulatedFunction;

public record MathFunctionToClientAdminDTO
        (Long id, String type, String name, FunctionData data, Long owner_id)
        implements DTO {}
