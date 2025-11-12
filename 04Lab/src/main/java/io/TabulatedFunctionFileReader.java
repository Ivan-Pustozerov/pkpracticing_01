package io;

import functions.classes.ArrayTabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.interfaces.TabulatedFunction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TabulatedFunctionFileReader {
    public static void main(String[] args) {
        try(BufferedReader Aread = new BufferedReader(new FileReader("03Lab/input/function.txt"));
            BufferedReader Lread = new BufferedReader(new FileReader("03Lab/input/function.txt"))){

            ArrayTabulatedFunctionFactory Afact = new ArrayTabulatedFunctionFactory();
            LinkedListTabulatedFunctionFactory Lfact = new LinkedListTabulatedFunctionFactory();

            TabulatedFunction Afunc = FunctionsIO.readTabulatedFunction(Aread,Afact);
            TabulatedFunction Lfunc = FunctionsIO.readTabulatedFunction(Lread, Lfact);

            System.out.println(Afunc.toString());
            System.out.println("\n");
            System.out.println(Lfunc.toString());
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
