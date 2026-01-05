package requestDTO;

public record AnalyticFunctionRequest(Long userId, String type, String name, String functionExpression) {
}