package SQL.DTO.responseDTO;

public record TabulatedFunctionResponse
        (Long id, String type, String name, Long ownerId, Double[] xvals, Double[] yvals)
        implements MathFunctionDetailsResponse {}