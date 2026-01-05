package requestDTO;

public record AnalyticFunctionResponse(Long id, String type, String name, Long ownerId, String functionExpression) {
    public AnalyticFunctionResponse {
        if (type == null) type = "analytic";
    }
    
    public AnalyticFunctionResponse(Long id, String name, Long ownerId, String functionExpression) {
        this(id, "analytic", name, ownerId, functionExpression);
    }
}