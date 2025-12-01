package SQL.repositories.tools;

//accept = принять объект+ выполнить преобразования без вывода результата

import java.sql.SQLException;

@FunctionalInterface
public interface SQLConsumer<T> {
    void accept(T t) throws SQLException;
}
