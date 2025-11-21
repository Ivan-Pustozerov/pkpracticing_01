package io;

import functions.classes.LinkedListTabulatedFunction;
import functions.interfaces.TabulatedFunction;
import operations.TabulatedDifferentialOperator;

import java.io.*;

public class LinkedListTabulatedFunctionSerialization {
    public static void main(String[] args) {
        try(var _out = new FileOutputStream("03Lab/output/serialized linked list functions.bin")){
        BufferedOutputStream outp = new BufferedOutputStream(_out);

        TabulatedDifferentialOperator Aoper = new TabulatedDifferentialOperator();

        LinkedListTabulatedFunction lfunc = new LinkedListTabulatedFunction(new double[]{1,2,3},new double[]{1,2,3});
        TabulatedFunction dAfunc1 = Aoper.derive(lfunc);
        TabulatedFunction dAfunc2 = Aoper.derive(dAfunc1);

        FunctionsIO.serialize(outp, lfunc);
        FunctionsIO.serialize(outp, dAfunc1);
        FunctionsIO.serialize(outp, dAfunc2);
    } catch (IOException e){
        e.printStackTrace(System.err);
    }
        try(var _in = new FileInputStream("03Lab/output/serialized linked list functions.bin")){
            var inp = new BufferedInputStream(_in);

            TabulatedFunction lfunc = FunctionsIO.deserialize(inp);
            TabulatedFunction dAfunc1 = FunctionsIO.deserialize(inp);
            TabulatedFunction dAfunc2 = FunctionsIO.deserialize(inp);

            System.out.println(lfunc.toString());
            System.out.println(dAfunc1.toString());
            System.out.println(dAfunc2.toString());
        }catch(IOException | ClassNotFoundException e){
            e.printStackTrace(System.err);
        }
}}
