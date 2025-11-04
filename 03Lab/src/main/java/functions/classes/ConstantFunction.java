package functions.classes;

import functions.interfaces.MathFunction;

public class ConstantFunction implements MathFunction {
    private final double Num;
    public <T extends Number> ConstantFunction(T a){
        Num=a.doubleValue();
    }

    public double getConst(){
        return Num;
    }


    @Override
    public <T extends Number> double apply(T x){return Num;}


}
