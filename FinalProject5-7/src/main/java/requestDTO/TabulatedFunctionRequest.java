package requestDTO;

public record TabulatedFunctionRequest(Long userId, String type, String name, double[] xvals, double[] yvals) {
    public TabulatedFunctionRequest {
        if (type == null) type = "tabulated";
    }
    
    public TabulatedFunctionRequest(Long userId, String name, double[] xvals, double[] yvals) {
        this(userId, "tabulated", name, xvals, yvals);
    }
}