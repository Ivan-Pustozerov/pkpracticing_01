package requestDTO;

public record TabulatedFunctionResponse(Long id, String type, String name, Long ownerId, double[] xvals, double[] yvals) {
}