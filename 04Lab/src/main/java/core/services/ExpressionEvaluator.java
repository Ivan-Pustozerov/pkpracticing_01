package core.services;
import core.DTO.PointDTO;
import functions.classes.Point;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExpressionEvaluator {

    public double evaluate(String expression, double x) {
        try {
            // Создаем выражение с переменной x
            Expression exp = new ExpressionBuilder(expression)
                    .variables("x")
                    .build()
                    .setVariable("x", x);

            return exp.evaluate();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка вычисления выражения: " + expression +
                    " при x=" + x + ". " + e.getMessage(), e);
        }
    }

    public List<PointDTO> evaluateRange(String expression, double from, double to, double step) {
        List<PointDTO> points = new ArrayList<>();

        for (double x = from; x <= to; x += step) {
            try {
                double y = evaluate(expression, x);
                if (!Double.isNaN(y) && !Double.isInfinite(y)) {
                    points.add(new PointDTO(x, y));
                }
            } catch (Exception e) {
                // Пропускаем точки, где вычисление не удалось
                System.out.println("Пропуск точки x=" + x + ": " + e.getMessage());
            }
        }

        return points;
    }

    // Проверка валидности выражения без вычисления
    public boolean isValidExpression(String expression) {
        try {
            new ExpressionBuilder(expression)
                    .variables("x")
                    .build();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
