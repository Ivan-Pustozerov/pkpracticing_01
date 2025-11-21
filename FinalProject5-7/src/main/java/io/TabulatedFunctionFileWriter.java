package io;

import functions.classes.ArrayTabulatedFunction;
import functions.classes.LinkedListTabulatedFunction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TabulatedFunctionFileWriter {
    public static void main(String[] args) {
        double[] xVals = {1,2,3.5};
        double[] yVals = {10,20,30.5};

        ArrayTabulatedFunction array = new ArrayTabulatedFunction(xVals, yVals);
        LinkedListTabulatedFunction list = new LinkedListTabulatedFunction(xVals, yVals);

        try (BufferedWriter Arecord = new BufferedWriter(new FileWriter("03Lab/output/Array_function.txt"));
             BufferedWriter Lrecord = new BufferedWriter(new FileWriter("03Lab/output/List_function.txt")))
        {
                FunctionsIO.writeTabulatedFunction(Arecord,array);
                FunctionsIO.writeTabulatedFunction(Lrecord,list);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
