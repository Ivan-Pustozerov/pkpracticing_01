package SQL.DTO.FromBD;

import SQL.DTO.DTO;

public record TabulatedFunctionFromBdDTO(long func_id, double[] xVals, double[] yVals)
        implements DTO {}
