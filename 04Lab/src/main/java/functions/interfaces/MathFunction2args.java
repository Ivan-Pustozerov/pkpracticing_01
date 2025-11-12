package functions.interfaces;

/// Функционал функции от двух переменных
public interface MathFunction2args {
    <T extends Number> double apply(T x, T y);
}
