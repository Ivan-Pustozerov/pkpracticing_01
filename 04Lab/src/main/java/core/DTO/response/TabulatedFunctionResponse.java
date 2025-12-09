package core.DTO.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class TabulatedFunctionResponse extends MathFunctionResponse{

    @JsonProperty("xvals")
    private Double[] xVals;
    @JsonProperty("yvals")
    private Double[] yVals;
}
