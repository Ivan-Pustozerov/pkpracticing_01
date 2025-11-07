package io;

import functions.classes.Point;
import functions.interfaces.TabulatedFunction;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.Writer;

/// Статический Сервис для ввода-вывода
public final class FunctionsIO {

    /// Недоступный конструктор для Отмены создания объектов
    private FunctionsIO(){
        throw new UnsupportedOperationException("Can not create object");
    }

///==================ФУНКЦИОНАЛ================================================================
    /// Печатает в поток символов таб функцию - Printer - не выбрасывает исключений никогда
    public static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction func){
        PrintWriter printer = new PrintWriter(writer); // создание обертки над Writer
        printer.println(func.getCount());             // печать кол-ва точек
        for(Point p : func){
            printer.printf("%f %f\n", p.x(), p.y());       // печать точек
        }
        printer.flush();                         // подтвердить изменения и записать внутренний буффер в поток
    }
}
