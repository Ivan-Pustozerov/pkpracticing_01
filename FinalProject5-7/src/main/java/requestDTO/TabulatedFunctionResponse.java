package requestDTO;

public record TabulatedFunctionResponse(Long id, String type, String name, Long ownerId, double[] xvals, double[] yvals) {
    public TabulatedFunctionResponse {
        if (type == null) type = "tabulated";
    }
    
    public TabulatedFunctionResponse(Long id, String name, Long ownerId, double[] xvals, double[] yvals) {
        this(id, "tabulated", name, ownerId, xvals, yvals);
    }
}