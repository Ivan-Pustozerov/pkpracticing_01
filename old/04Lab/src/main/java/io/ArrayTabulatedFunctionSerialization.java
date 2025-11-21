package io;

import functions.classes.ArrayTabulatedFunction;
import functions.interfaces.TabulatedFunction;
import operations.TabulatedDifferentialOperator;

import java.io.*;

public class ArrayTabulatedFunctionSerialization {
    public static void main(String[] args) {
        try(FileOutputStream wstream = new FileOutputStream("03Lab/output/serialized_array_functions.bin")){
            BufferedOutputStream BWstream = new BufferedOutputStream(wstream);

            TabulatedDifferentialOperator Aoper = new TabulatedDifferentialOperator();

            ArrayTabulatedFunction Afunc = new ArrayTabulatedFunction(new double[]{1,2,3},new double[]{1,2,3});
            TabulatedFunction dAfunc1 = Aoper.derive(Afunc);
            TabulatedFunction dAfunc2 = Aoper.derive(dAfunc1);

            FunctionsIO.serialize(BWstream, Afunc);
            FunctionsIO.serialize(BWstream, dAfunc1);
            FunctionsIO.serialize(BWstream, dAfunc2);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        try(FileInputStream rstream = new FileInputStream("03Lab/output/serialized_array_functions.bin")){
            BufferedInputStream BRstream = new BufferedInputStream(rstream);

            TabulatedFunction AfuncR = FunctionsIO.deserialize(BRstream);
            TabulatedFunction dAfuncR1 = FunctionsIO.deserialize(BRstream);
            TabulatedFunction dAfuncR2 = FunctionsIO.deserialize(BRstream);

            System.out.println(AfuncR.toString());
            System.out.println(dAfuncR1.toString());
            System.out.println(dAfuncR2.toString());
        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
