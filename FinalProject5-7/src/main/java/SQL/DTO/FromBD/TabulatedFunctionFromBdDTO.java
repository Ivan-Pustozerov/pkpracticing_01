package SQL.DTO.FromBD;

public record TabulatedFunctionFromBdDTO(long func_id, double[] xVals, double[] yVals)
        implements DTO {}
