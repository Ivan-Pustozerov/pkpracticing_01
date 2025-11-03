package operations;
import functions.classes.Point;
import functions.interfaces.TabulatedFunction;

public class TabulatedFunctionOperationService {
    public static Point[] asPoints(TabulatedFunction func){
        int i=0;
        Point[] res = new Point[func.getCount()];
        for(Point p : func){
            res[i++] = p;
        }
        return res;
    }
}
