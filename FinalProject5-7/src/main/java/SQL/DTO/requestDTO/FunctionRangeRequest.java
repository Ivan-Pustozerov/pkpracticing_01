package SQL.DTO.requestDTO;

public record FunctionRangeRequest(Double from, Double to, Double step) {
    public boolean validate() {
        if (from == null || to == null || step == null) {
            return false;
        }
        if (from >= to) {
            return false;
        }
        if (step <= 0) {
            return false;
        }
        // Проверяем, чтобы не было слишком много точек
        long numPoints = (long) ((to - from) / step);
        if (numPoints > 10000) {
            return false;
        }
        return true;
    }

}