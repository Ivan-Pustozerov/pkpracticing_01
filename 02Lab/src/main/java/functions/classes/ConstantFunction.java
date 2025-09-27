package functions.classes;
import functions.interfaces.MathFunction;
public class ConstantFunction implements MathFunction {
    private final double Num;
    public ConstantFunction(Object a){
        Num=(a instanceof Number)? ((Number)a).doubleValue() :Double.NaN;
        System.out.println(Num);
    }
    @Override
    public double apply(Object x){return Num;}
    public double getConst(){return Num;}
}
