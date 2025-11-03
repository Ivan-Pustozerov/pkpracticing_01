package functions.interfaces;
import functions.classes.Point;

public interface TabulatedFunction extends MathFunction, Iterable<Point> {
    int getCount();

    double getX(int index);
    double getY(int index);

    <T extends Number> void setY(int index,T y);
    <T extends Number> void setX(int index,T x);

    <T extends Number> int indexOfX(T x);
    <T extends Number> int indexOfY(T y);

    double leftBound();
    double rightBound();
}
