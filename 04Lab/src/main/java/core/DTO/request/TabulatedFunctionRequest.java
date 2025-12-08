package core.DTO.request;

import lombok.Data;
@Data
public class TabulatedFunctionRequest extends MathFunctionRequest{

    private Double[] xVals;
    private Double[] yVals;

}
