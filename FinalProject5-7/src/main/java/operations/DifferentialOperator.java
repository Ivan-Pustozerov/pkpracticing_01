package operations;

import functions.interfaces.MathFunction;

//TODO: реализовать Сервис дифференциальных операрторов - наподобие интеграла
//TODO: проверить работу интеграла + добавить неопределенный интеграл

public interface DifferentialOperator <T extends MathFunction>{
    T derive(T function);
}
