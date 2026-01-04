package SQL.DTO.ToClient;

import SQL.DTO.DTO;
import SQL.DTO.FunctionData;
import functions.interfaces.TabulatedFunction;

public record MathFunctionToClientUserDTO
        (Long id, String type, String name, FunctionData data)
        implements DTO {}
