package functions.classes;
import functions.interfaces.MathFunction;
public class CompositeFunction implements MathFunction{
    public MathFunction ff;
    public MathFunction sf;

    public CompositeFunction(MathFunction ff,MathFunction sf){
        this.ff = ff;
        this.sf = sf;
    }
    @Override
    public <T extends Number> double apply(T x){
        double first = ff.apply(x);
        return sf.apply(first);
    }
}
