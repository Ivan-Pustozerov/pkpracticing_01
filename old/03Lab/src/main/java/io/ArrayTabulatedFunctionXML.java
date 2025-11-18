package io;

import functions.classes.ArrayTabulatedFunction;
import functions.interfaces.TabulatedFunction;

import java.io.*;

public class ArrayTabulatedFunctionXML {
    public static void main(String[] args) {
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(new double[]{1,2,3}, new double[]{4,5,6});
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("03Lab/output/Array.xml"))){
            FunctionsIO.serializeXml(writer, func);
        }
        catch(IOException e){
            e.printStackTrace();
        }

        try(BufferedReader reader = new BufferedReader(new FileReader("03Lab/output/Array.xml"))){
            ArrayTabulatedFunction function = FunctionsIO.deserializeXml(reader);
            System.out.println(function);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
