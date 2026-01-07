package SQL.DTO.requestDTO;

public record FunctionRangeRequest(Double from, Double to, Double step) {
    public void validate() {
        if (from == null || to == null || step == null) {
            throw new IllegalArgumentException("All fields (from, to, step) are required");
        }
        if (from >= to) {
            throw new IllegalArgumentException("'from' must be less than 'to'");
        }
        if (step <= 0) {
            throw new IllegalArgumentException("'step' must be positive");
        }
        // Проверяем, чтобы не было слишком много точек
        long numPoints = (long) ((to - from) / step);
        if (numPoints > 10000) {
            throw new IllegalArgumentException("Too many points requested. Reduce range or increase step.");
        }
    }

}