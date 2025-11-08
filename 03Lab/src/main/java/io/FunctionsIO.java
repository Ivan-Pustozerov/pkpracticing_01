package io;

import functions.classes.Point;
import functions.factory.TabulatedFunctionFactory;
import functions.interfaces.TabulatedFunction;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/// Статический Сервис для ввода-вывода
public final class FunctionsIO {

    /// Недоступный конструктор для Отмены создания объектов
    private FunctionsIO(){
        throw new UnsupportedOperationException("Can not create object");
    }

///==================ФУНКЦИОНАЛ================================================================
    /// Печатает в поток СИМВОЛОВ таб функцию - Printer - не выбрасывает вызываемых исключений никогда
    public static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction func){
        PrintWriter printer = new PrintWriter(writer);    // создание обертки над Writer
        printer.println(func.getCount());                // печать кол-ва точек
        for(Point p : func){
            printer.printf("%f %f\n", p.x(), p.y());   // печать точек
        }
        printer.flush();                             // подтвердить изменения и записать внутренний буффер в поток
    }

    /// Читает функцию из СИМВОЛЬНОГО потока записи - не выбрасывает вызываемых исключений никогда
    public static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory){
        try{
            int count = Integer.parseInt(reader.readLine());
            double[] xVals = new double[count];
            double[] yVals = new double[count];
            NumberFormat format = NumberFormat.getInstance(Locale.forLanguageTag("ru"));

            try{
                for(int i=0; i < count; ++i){
                    String[] line=reader.readLine().split(" ");
                    xVals[i] = format.parse(line[0]).doubleValue();
                    yVals[i] = format.parse(line[1]).doubleValue();
                }
            }
            catch (ParseException pe){
                IOException e = new IOException("Parse Error");
                e.initCause(pe);
                throw e;
            }
            return factory.create(xVals, yVals);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /// Механизм сериализации объекта-функции  - выбрасывает проверяемое исключение
    public static void serialize(BufferedOutputStream stream, TabulatedFunction func) throws IOException {
        ObjectOutputStream objStream = new ObjectOutputStream(stream);
        objStream.writeObject(func);
        objStream.flush();
    }

    public static TabulatedFunction deserialize(BufferedInputStream stream)throws IOException, ClassNotFoundException {
        return null;
    }








}
