package functions.interfaces;
public interface TabulatedFunction extends MathFunction {
    int getCount();
    double getX(int index);
    double getY(int index);
    void setY(int index,Object val);
    /// /Unnecessary!
    void setX(int index,Object x);
    /// /
    int indexOfX(Object x);
    int indexOfY(Object y);
    double leftBound();
    double rightBound();
}
