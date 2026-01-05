package requestDTO;

public record AnalyticFunctionRequest(Long userId, String type, String name, String functionExpression) {
    public AnalyticFunctionRequest {
        if (type == null) type = "analytic";
    }
    
    public AnalyticFunctionRequest(Long userId, String name, String functionExpression) {
        this(userId, "analytic", name, functionExpression);
    }
}