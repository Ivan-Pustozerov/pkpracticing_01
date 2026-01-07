package SQL.DTO.requestDTO;

public record AnalyticFunctionRequest(Long userId, String type, String name, String functionExpression) {
}