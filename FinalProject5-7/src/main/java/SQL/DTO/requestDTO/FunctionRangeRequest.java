package SQL.DTO.requestDTO;

public record FunctionRangeRequest(double from, double to, double step) {
    public FunctionRangeRequest {
        if (step <= 0) {
            throw new IllegalArgumentException("Step must be positive");
        }
        if (to < from) {
            throw new IllegalArgumentException("End value must be greater than or equal to start value");
        }
    }

}