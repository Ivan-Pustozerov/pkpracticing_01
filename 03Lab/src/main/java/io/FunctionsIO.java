package io;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import functions.classes.ArrayTabulatedFunction;
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
    private FunctionsIO() {
        throw new UnsupportedOperationException("Can not create object");
    }

    /// ==================ФУНКЦИОНАЛ================================================================
    /// Печатает в поток СИМВОЛОВ таб функцию - Printer - не выбрасывает вызываемых исключений никогда
    public static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction func) {
        PrintWriter printer = new PrintWriter(writer);    // создание обертки над Writer
        printer.println(func.getCount());                // печать кол-ва точек
        for (Point p : func) {
            printer.printf("%f %f\n", p.x(), p.y());   // печать точек
        }
        printer.flush();                             // подтвердить изменения и записать внутренний буффер в поток
    }

    /// Читает функцию из СИМВОЛЬНОГО потока записи - не выбрасывает вызываемых исключений никогда
    public static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory) {
        try {
            int count = Integer.parseInt(reader.readLine());
            double[] xVals = new double[count];
            double[] yVals = new double[count];
            NumberFormat format = NumberFormat.getInstance(Locale.forLanguageTag("ru"));

            try {
                for (int i = 0; i < count; ++i) {
                    String[] line = reader.readLine().split(" ");
                    xVals[i] = format.parse(line[0]).doubleValue();
                    yVals[i] = format.parse(line[1]).doubleValue();
                }
            } catch (ParseException pe) {
                IOException e = new IOException("Parse Error");
                e.initCause(pe);
                throw e;
            }
            return factory.create(xVals, yVals);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /// Печатает буферизованный байтовый поток outputStream - выбрасывает исключение IOException
    public static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction func) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream); // создание обертки над outputStream
        dataOutputStream.writeInt(func.getCount());             // печать кол-ва точек
        for (Point p : func) {                                  // печать значений
            dataOutputStream.writeDouble(p.x());
            dataOutputStream.writeDouble(p.y());
        }
        dataOutputStream.flush();                       // подтвердить изменения и записать внутренний буффер в поток
    }
    /// Считывает из буферизованного байтового потока inputStream и на их основе создать новую функцию с помощью фабрики factory(выбрасывает IOException)
    public static TabulatedFunction  readTabulatedFunction(BufferedInputStream inputStream, TabulatedFunctionFactory factory) throws IOException{
        DataInputStream datainputStream = new DataInputStream(inputStream); // создание обертки входного потока в DataInputStream
        int count = datainputStream.readInt();                              // Считываем длинну у обьекта полученного ранее

        double[] xVals = new double[count];                 //просто
        double[] yVals = new double[count];               //два массива для значений
        for (int i=0;i<count;i++){
            xVals[i]=datainputStream.readDouble();        //засовываем
            yVals[i]=datainputStream.readDouble();
        }
        return factory.create(xVals,yVals);             //возвращаем новую функцию создавая ее через фабрику
    }
    /// Механизм сериализации объекта-функции  - выбрасывает проверяемое исключение
    public static void serialize(BufferedOutputStream stream, TabulatedFunction func) throws IOException {
        ObjectOutputStream objStream = new ObjectOutputStream(stream);
        objStream.writeObject(func);
        objStream.flush();
    }

    public static TabulatedFunction deserialize(BufferedInputStream stream) throws IOException, ClassNotFoundException {
        var inputstream = new ObjectInputStream(stream);
        return (TabulatedFunction) inputstream.readObject();
    }

    public static void serializeXml(BufferedWriter writer, ArrayTabulatedFunction func) throws IOException {
        XStream xstream = new XStream();  // с помощью рефлексии способен автоматически сериализовать
        writer.write(xstream.toXML(func));
        writer.flush();
    }

    public static ArrayTabulatedFunction deserializeXml(BufferedReader reader) throws IOException {
        XStream xstream = new XStream();
        // Разрешить использовать класс ArrayTabulatedFunction
        xstream.addPermission(NoTypePermission.NONE); // отключить все разрешения
        xstream.addPermission(AnyTypePermission.ANY);// разрешить все классы
        Object func = xstream.fromXML(reader);

        if(func instanceof ArrayTabulatedFunction) return (ArrayTabulatedFunction)func;
        else throw new IOException("Wrong Type was read");
    }


}
