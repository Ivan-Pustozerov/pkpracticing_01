package exceptions;

/// Непроверяемое Исключение несоответствия данных функции
public class InconsistentFunctionsException extends RuntimeException {

    public InconsistentFunctionsException() {
        super();
    }
    public InconsistentFunctionsException(String message) {
        super(message);
    }
}
