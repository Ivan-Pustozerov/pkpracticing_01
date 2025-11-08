package io;

import functions.classes.ArrayTabulatedFunction;
import functions.classes.LinkedListTabulatedFunction;
import functions.classes.Point;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.interfaces.TabulatedFunction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsIOTest {
    private static File Dir;
    private static ArrayTabulatedFunction Afunc;
    private static LinkedListTabulatedFunction Lfunc;
    private final static double[] xVals = {1,2,3,4};
    private final static double[] yVals = {5,6,7,8};
    private final static double delta = 1e-16;
    private final static int count = 4;
    private static NumberFormat format = NumberFormat.getInstance(Locale.forLanguageTag("ru"));


    private File createFile(String filename){
        File file = new File(Dir.getPath() + filename);
        try {
            if(!file.exists()){file.createNewFile();}
            return file;
        } catch (IOException e) {
            return null;
        }
    }

    @BeforeAll
    static void setUp() {
        Dir = new File("temp");
        Dir.mkdir();
        Afunc = new ArrayTabulatedFunction(xVals, yVals);
    }

    @Test
    void writeTabulatedFunction() throws IOException{
        File file = createFile("/WriteTest.txt");
        if(file == null) throw new IOException("System failed to create test file");

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            FunctionsIO.writeTabulatedFunction(writer, Afunc);
        }
        catch (IOException e){
            throw new IOException("System failed to read test file");
        }
        finally{
            try(BufferedReader reader = new BufferedReader(new FileReader(file))){
                int counter = Integer.parseInt(reader.readLine());
                for(int i=0; i < counter; ++i){
                    String[] line = reader.readLine().split(" ");
                    assertEquals(xVals[i], format.parse(line[0]).doubleValue(),delta);
                    assertEquals(yVals[i], format.parse(line[1]).doubleValue(),delta);
                }
            } catch (ParseException e) {
                throw new IOException("System failed to parse data");
            }
        }
    }

    @Test
    void readTabulatedFunction() throws  IOException{
        File file = createFile("/WriteTest.txt");
        if(file == null) throw new IOException("System failed to open test file");

        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            TabulatedFunction testAfunc = FunctionsIO.readTabulatedFunction(reader, new ArrayTabulatedFunctionFactory());
            int i=0;
            assertNotNull(testAfunc);
            for(Point p : testAfunc){
                assertEquals(xVals[i], p.x(),delta);
                assertEquals(yVals[i], p.y(),delta);
                ++i;
            }
        }
        catch (IOException e){
            throw new IOException("System failed to read test file");
        }
    }

    @Test
    void serialize() throws IOException{
        ByteArrayOutputStream AfuncSerData = new ByteArrayOutputStream();

        try(BufferedOutputStream out = new BufferedOutputStream(AfuncSerData)){
            FunctionsIO.serialize(out,Afunc);
            assertNotEquals(0,AfuncSerData.size());//объем буффера потока Afunc должен измениться
        }
    }

    @Test
    void test_deserialize_list() throws IOException, ClassNotFoundException{
        double[] xValues = {0.5, 1.5, 2.5};
        double[] yValues = {1.5, 2.5, 3.5};
        TabulatedFunction originalFunction = new LinkedListTabulatedFunction(xValues, yValues);

        var file = createFile("/WriteTest.txt");

        try (var outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            FunctionsIO.serialize(outputStream, originalFunction);
        }

        try (var inputStream = new BufferedInputStream(new FileInputStream(file))) {
            TabulatedFunction deserializedFunction = FunctionsIO.deserialize(inputStream);

            assertEquals(originalFunction, deserializedFunction);
        }
    }
    @Test
    void test_deserialize_array() throws IOException, ClassNotFoundException{
        double[] xValues = {0.5, 1.5, 2.5};
        double[] yValues = {1.5, 2.5, 3.5};
        TabulatedFunction func = new ArrayTabulatedFunction(xValues, yValues);

        var file = createFile("/WriteTest.txt");

        try (var outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            FunctionsIO.serialize(outputStream, func);
        }

        try (var inputStream = new BufferedInputStream(new FileInputStream(file))) {
            TabulatedFunction deserializedFunction = FunctionsIO.deserialize(inputStream);

            assertEquals(func, deserializedFunction);
        }
    }
    @Test
    void writeTabulatedFunctionWithBufferedOutputStream() throws IOException {
        // Подготовка тестовых данных
        double[] xValues = {1.5, 2.5, 3.5};
        double[] yValues = {4.5, 5.5, 6.5};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        var file = createFile("/WriteTest.txt");

        // Вызов тестируемого метода
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            FunctionsIO.writeTabulatedFunction(outputStream, function);
        }}
    @Test
    void readTabulatedFunctionWithBufferedInputStream() throws IOException {

        var file = createFile("/WriteTest.txt");

        try (var dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            dataOutputStream.writeInt(2);
            dataOutputStream.writeDouble(1.5);
            dataOutputStream.writeDouble(4.5);
            dataOutputStream.writeDouble(2.5);
            dataOutputStream.writeDouble(5.5);
        }


        try (var inputStream = new BufferedInputStream(new FileInputStream(file))) {
            var factory = new ArrayTabulatedFunctionFactory();
            TabulatedFunction function = FunctionsIO.readTabulatedFunction(inputStream, factory);

            // Проверка результата
            assertNotNull(function);
            assertEquals(2, function.getCount());
            assertEquals(1.5, function.getX(0), 0.0001);
            assertEquals(4.5, function.getY(0), 0.0001);
            assertEquals(2.5, function.getX(1), 0.0001);
            assertEquals(5.5, function.getY(1), 0.0001);
        }
    }
    @AfterAll
    static void clearEverything(){
        Dir.delete();
    }
}