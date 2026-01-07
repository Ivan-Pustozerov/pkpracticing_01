package SQL.Mappers;

public class Converter {

    public static Double[] toDoubleArray(double[] array){
        Double[] res = new Double[array.length];

        for(int i=0; i< array.length;++i){
            res[i] = (Double)array[i];
        }
        return res;
    }

    public static double[] todoubleArray(Double[] array){
        double[] res = new double[array.length];

        for(int i=0; i< array.length;++i){
            res[i] = array[i];
        }
        return res;
    }
}
