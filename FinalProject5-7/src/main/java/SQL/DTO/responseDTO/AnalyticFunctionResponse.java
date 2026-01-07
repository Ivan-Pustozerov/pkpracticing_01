package SQL.DTO.responseDTO;

public record AnalyticFunctionResponse
        (Long id, String type, String name, Long ownerId, String functionExpression)
        implements MathFunctionDetailsResponse {}