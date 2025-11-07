package exceptions;

/// Непроверяемое Исключение интерполяции (не пригодилось)
public class InterpolationException extends RuntimeException{

    public InterpolationException(){
        super();
    }
    public InterpolationException(String message){
        super(message);
    }
}
