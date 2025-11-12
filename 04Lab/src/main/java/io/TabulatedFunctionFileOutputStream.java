package io;

import functions.classes.ArrayTabulatedFunction;
import functions.classes.LinkedListTabulatedFunction;

import java.io.*;
public class TabulatedFunctionFileOutputStream {
    public static void main(String[] args){
        double[] xVals = {1,2,3.5};
        double[] yVals = {10,20,30.5};

        ArrayTabulatedFunction array = new ArrayTabulatedFunction(xVals, yVals);
        LinkedListTabulatedFunction list = new LinkedListTabulatedFunction(xVals, yVals);

        try (FileOutputStream Arecord = new FileOutputStream("03Lab/output/Array_function.txt");
             BufferedOutputStream ABuff = new BufferedOutputStream(Arecord);

             FileOutputStream Lrecord = new FileOutputStream("03Lab/output/List_function.txt");
             BufferedOutputStream LBuff = new BufferedOutputStream(Lrecord))
        {
            FunctionsIO.writeTabulatedFunction(ABuff,array);
            FunctionsIO.writeTabulatedFunction(LBuff,list);
            System.out.println("Бинарные файлы успешно созданы:");
            System.out.println("- output/array function.bin");
            System.out.println("- output/linked list function.bin");
            System.out.println("Файлы не подлежат к чтинию - они бинарные");

        } catch (IOException e) {
            System.err.println("!!BINARY FILE OUTPUT WRITE ERR!!");
            e.printStackTrace();
        }

    }
}
