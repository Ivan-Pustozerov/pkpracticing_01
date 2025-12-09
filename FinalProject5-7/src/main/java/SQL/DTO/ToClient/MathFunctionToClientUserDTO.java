package SQL.DTO.ToClient;

import SQL.DTO.DTO;
import SQL.DTO.FunctionData;

public record MathFunctionToClientUserDTO
        (String type, String name, FunctionData data)
        implements DTO {}
