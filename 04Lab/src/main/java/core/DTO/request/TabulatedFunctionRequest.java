package core.DTO.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class TabulatedFunctionRequest extends MathFunctionRequest{
    @JsonProperty("xvals")
    private Double[] xVals;
    @JsonProperty("yvals")
    private Double[] yVals;

}
