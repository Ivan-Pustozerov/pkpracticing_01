package requestDTO;

public record TabulatedFunctionRequest(Long userId, String type, String name, double[] xvals, double[] yvals) {
}