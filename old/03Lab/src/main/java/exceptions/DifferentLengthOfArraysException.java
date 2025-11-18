package exceptions;

/// Непроверяемое Исключение массивов разных длин
public class DifferentLengthOfArraysException extends RuntimeException{

    public DifferentLengthOfArraysException(){
        super();
    }
    public DifferentLengthOfArraysException(String message){
        super(message);
    }
}
