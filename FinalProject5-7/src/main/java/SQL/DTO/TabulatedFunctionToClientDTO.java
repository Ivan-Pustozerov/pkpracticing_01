package SQL.DTO;

import java.util.List;

public class TabulatedFunctionToClientDTO implements DTO{
    private final long func_id;
    private final List<Double> xVals;
    private final List<Double> yVals;

    public TabulatedFunctionToClientDTO(long func_id, Double[] xVals, Double[] yVals){
        this.func_id = func_id;
        this.xVals = (xVals != null)? List.<Double>of(xVals) : List.of();
        this.yVals = (yVals != null)? List.<Double>of(yVals) : List.of();
    }

    public long func_id() {
        return func_id;
    }
    public List<Double> xVals() {
        return xVals;
    }
    public List<Double> yVals() {
        return yVals;
    }
}
