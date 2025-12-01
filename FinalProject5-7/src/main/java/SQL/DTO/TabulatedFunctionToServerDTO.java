package SQL.DTO;

public record TabulatedFunctionToServerDTO(long func_id, double[] xVals, double[] yVals)
        implements DTO {}
