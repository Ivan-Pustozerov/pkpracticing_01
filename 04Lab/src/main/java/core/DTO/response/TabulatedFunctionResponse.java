package core.DTO.response;

import lombok.Data;
@Data
public class TabulatedFunctionResponse extends MathFunctionResponse{
    private Double[] xVals;

    private Double[] yVals;
}
