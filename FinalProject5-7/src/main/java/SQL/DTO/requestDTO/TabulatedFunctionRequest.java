package SQL.DTO.requestDTO;

public record TabulatedFunctionRequest
        (Long userId, String type, String name, Double[] xvals, Double[] yvals) {
}