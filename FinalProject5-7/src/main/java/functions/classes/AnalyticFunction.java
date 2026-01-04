package functions.classes;

import functions.interfaces.MathFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Implements MathFunction interface to represent an analytical function defined by a string expression.
 * The string expression can contain basic mathematical operations and functions.
 * Supported operations: +, -, *, /, ^ (power), parentheses
 * Supported functions: sin, cos, tan, asin, acos, atan, log, ln, sqrt, abs, exp
 */
public class AnalyticFunction implements MathFunction {
    private final String expression;
    private final Map<String, Function<Double, Double>> functions;

    /**
     * Constructor that takes a string expression representing the mathematical function
     * @param expression String representation of the mathematical function, where 'x' is the variable
     * @throws IllegalArgumentException if the expression contains invalid characters
     */
    public AnalyticFunction(String expression) {
        if (expression == null) {
            throw new IllegalArgumentException("Expression cannot be null");
        }

        this.expression = expression;
        this.functions = new HashMap<>();
        initializeFunctions();
    }

    private void initializeFunctions() {
        functions.put("sin", Math::sin);
        functions.put("cos", Math::cos);
        functions.put("tan", Math::tan);
        functions.put("asin", Math::asin);
        functions.put("acos", Math::acos);
        functions.put("atan", Math::atan);
        functions.put("log", Math::log10);
        functions.put("ln", Math::log);
        functions.put("sqrt", Math::sqrt);
        functions.put("abs", Math::abs);
        functions.put("exp", Math::exp);
    }

    @Override
    public <T extends Number> double apply(T x) {
        if (x == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        ExpressionParser parser = new ExpressionParser(expression, x.doubleValue(), functions);
        return parser.parse();
    }

    /**
     * Returns the original expression string
     */
    public String getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "AnalyticFunction{expression='" + expression + "'}";
    }

    /**
     * A recursive descent parser for mathematical expressions
     */
    private static class ExpressionParser {
        private final String expression;
        private final double xValue;
        private final Map<String, Function<Double, Double>> functions;
        private int pos = 0;

        public ExpressionParser(String expression, double xValue, Map<String, Function<Double, Double>> functions) {
            this.expression = expression.replaceAll("\\s+", "");
            this.xValue = xValue;
            this.functions = functions;
        }

        public double parse() {
            pos = 0;
            double result = parseExpression();
            if (pos < expression.length()) {
                throw new RuntimeException("Unexpected character at position " + pos + ": " + expression.charAt(pos));
            }
            return result;
        }

        // Grammar:
        // expression = term ( ("+" | "-") term )*
        // term = factor ( ("*" | "/") factor )*
        // factor = power ( ("^") power )*
        // power = unary | power "^" factor
        // unary = "-" primary | primary
        // primary = number | "x" | function | "(" expression ")"

        private double parseExpression() {
            double result = parseTerm();

            while (pos < expression.length() && (expression.charAt(pos) == '+' || expression.charAt(pos) == '-')) {
                char op = expression.charAt(pos);
                pos++; // consume operator
                double term = parseTerm();
                if (op == '+') {
                    result += term;
                } else {
                    result -= term;
                }
            }

            return result;
        }

        private double parseTerm() {
            double result = parseFactor();

            while (pos < expression.length() && (expression.charAt(pos) == '*' || expression.charAt(pos) == '/')) {
                char op = expression.charAt(pos);
                pos++; // consume operator
                double factor = parseFactor();
                if (op == '*') {
                    result *= factor;
                } else {
                    result /= factor;
                }
            }

            return result;
        }

        private double parseFactor() {
            double result = parsePower();

            while (pos < expression.length() && expression.charAt(pos) == '^') {
                pos++; // consume operator
                double power = parsePower(); // right associative: a^b^c = a^(b^c)
                result = Math.pow(result, power);
            }

            return result;
        }

        private double parsePower() {
            if (pos < expression.length() && expression.charAt(pos) == '-') {
                pos++; // consume '-'
                return -parseUnary();
            }
            return parseUnary();
        }

        private double parseUnary() {
            // Check for function calls
            if (pos < expression.length()) {
                for (String funcName : functions.keySet()) {
                    if (expression.startsWith(funcName, pos)) {
                        pos += funcName.length();
                        if (pos < expression.length() && expression.charAt(pos) == '(') {
                            pos++; // consume '('
                            double arg = parseExpression();
                            if (pos >= expression.length() || expression.charAt(pos) != ')') {
                                throw new RuntimeException("Expected ')' at position " + pos);
                            }
                            pos++; // consume ')'
                            return functions.get(funcName).apply(arg);
                        } else {
                            // Not a function call, backtrack
                            pos -= funcName.length();
                        }
                    }
                }
            }

            // Check for parenthesized expression
            if (pos < expression.length() && expression.charAt(pos) == '(') {
                pos++; // consume '('
                double result = parseExpression();
                if (pos >= expression.length() || expression.charAt(pos) != ')') {
                    throw new RuntimeException("Expected ')' at position " + pos);
                }
                pos++; // consume ')'
                return result;
            }

            // Check for variable 'x'
            if (pos < expression.length() && expression.charAt(pos) == 'x') {
                pos++; // consume 'x'
                return xValue;
            }

            // Parse number
            return parseNumber();
        }

        private double parseNumber() {
            int start = pos;
            boolean hasDecimal = false;

            if (pos < expression.length() && expression.charAt(pos) == '-') {
                pos++;
            }

            while (pos < expression.length() &&
                    (Character.isDigit(expression.charAt(pos)) || expression.charAt(pos) == '.')) {
                if (expression.charAt(pos) == '.') {
                    if (hasDecimal) {
                        throw new RuntimeException("Invalid number format at position " + pos);
                    }
                    hasDecimal = true;
                }
                pos++;
            }

            if (start == pos) {
                throw new RuntimeException("Expected number at position " + pos);
            }

            return Double.parseDouble(expression.substring(start, pos));
        }
    }
}
