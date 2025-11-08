package io;

import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.interfaces.TabulatedFunction;
import operations.TabulatedDifferentialOperator;

import java.io.*;

public class TabulatedFunctionFileInputStream  {
    public static void main(String[] args){

        ///Функция из файла:
        try (var _binary = new FileInputStream("03Lab/input/binary function.bin");
            ){
            var ABuff = new BufferedInputStream(_binary);
            var arrayFactory = new ArrayTabulatedFunctionFactory();
            TabulatedFunction infilefunc = FunctionsIO.readTabulatedFunction(ABuff,arrayFactory);

            System.out.println("Функция из файла:");
            System.out.println(infilefunc.toString());

        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        ///Функция из консоли:
        ///ПОМЕТКА: Мы не могли использовать тут try-with-resources, так как она закрывает потоки, а поток System.in закрываться не должен
        try
        {
            var c = new BufferedReader(new InputStreamReader(System.in));

            var listfactory = new LinkedListTabulatedFunctionFactory();
            TabulatedFunction console_function = FunctionsIO.readTabulatedFunction(c,listfactory);

            System.out.println("Введенная функция:");
            System.out.println(console_function.toString());

            var dif_op = new TabulatedDifferentialOperator();
            TabulatedFunction deriv = dif_op.derive(console_function);

            System.out.println("Производная данной функции:");
            System.out.println(deriv.toString());

        } catch (NullPointerException e) {
            System.err.println("!!BINARY FILE OUTPUT WRITE ERR!!");
            e.printStackTrace();
        }
    }
}
