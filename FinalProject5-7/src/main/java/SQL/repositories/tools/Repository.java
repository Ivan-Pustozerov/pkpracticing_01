package SQL.repositories.tools;

import SQL.DTO.DTO;
import SQL.repositories.SQLRepositoryException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Repository {

    /// Универсальный конструктор таблиц
    protected int initTable(Connection connect, String sql) throws SQLRepositoryException
    {
        try(Statement statement = connect.createStatement()){
            return statement.executeUpdate(sql);

        } catch (SQLException e) {
            throw new SQLRepositoryException("Init Table Error!");
        }
    }

    /// Универсальный запрос на обновление
    protected int executeUpdate(Connection connect, String sql,
                                SQLConsumer<PreparedStatement> pstateTemplate) throws SQLRepositoryException
    {
        try(PreparedStatement pstate = connect.prepareStatement(sql)) {

            pstateTemplate.accept(pstate);//внутреннее состояние объекта pstate изменяется void методом

            return pstate.executeUpdate();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new SQLRepositoryException("PreparedStatement Update Error!");
        }
    }

    /// Универсальный запрос на поиск
    protected <T extends DTO> ArrayList<T> executeQuery(Connection connect, String sql,
                                                        SQLConsumer<PreparedStatement> pstateTemplate,
                                                        SQLResultSetFunction<T> funcTemplate)
                                                            throws SQLRepositoryException
    {
        ArrayList<T> result = new ArrayList<>();
        try(PreparedStatement pstate = connect.prepareStatement(sql.trim());) {

            pstateTemplate.accept(pstate);


            try(ResultSet set = pstate.executeQuery()){///ОБЯЗАТЕЛЬНО! ResultSet и PreparedStatement, Array, Statement
                while(set.next()){                           ///ЗАКРЫВАТЬ - ОБЕРТКИ НАД доступом к БД!
                    result.add(funcTemplate.apply(set));
                }

                return result;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLRepositoryException("Execute Query Error!");
        }
    }


///---------------------------------------ВСПОМОГАТЕЛЬНЫЙ-ФУНКЦИОНАЛ----------------------------------
    protected String readCommand(String resourcePath) {
            // Удаляем начальные слеши, если есть
            if (resourcePath.startsWith("/")) {
                resourcePath = resourcePath.substring(1);
            }

            InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (is == null) {
                // Попробуем другой способ
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
            }

            if (is == null) {
                throw new RuntimeException("Resource not found in classpath: " + resourcePath);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                throw new RuntimeException("Failed to read resource: " + resourcePath, e);
            }
        }

    /*
    protected static String readCommand(String filepath){
        StringBuffer res = new StringBuffer();
        String line;
        try(BufferedReader reader = new BufferedReader(new FileReader(filepath))){
            while((line = reader.readLine()) != null){
                res.append(line);
            }
            return res.toString();

        }catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Command Error");
        }
    }*/


    protected static double[] toDoubleBaseArray(java.sql.Array array) throws SQLRepositoryException{
        double[] result = null;
        try {
            Double[] arr = (Double[]) array.getArray();
            result = new double[arr.length];
            for(int i = 0;i<arr.length;++i) result[i] = arr[i];
        }
        catch (SQLException e) {
            throw new SQLRepositoryException("From SQLArray Cast Error");
        }
        finally{
            try{
                array.free();
            }
            catch (SQLException e) {
                throw new SQLRepositoryException("SQLArray Free Error");
            }


        }
        return result;
    }

    protected SQLArray toDoubleSQLArray(Connection connect, double[] array) throws SQLRepositoryException{
        if(array == null) return new SQLArray(null);
        Double[] arr = new Double[array.length];
        for(int i =0; i < array.length;++i){ arr[i] = Double.valueOf(array[i]); }

        try{
            return new SQLArray(connect.createArrayOf("float8", arr));
        } catch (SQLException e) {
            throw new SQLRepositoryException("To SQL Array Cast Exception");
        }
    }
    protected SQLArray toLongSQLArray(Connection connect, long[] array) throws SQLRepositoryException{
        if(array == null) return new SQLArray(null);

        Long[] arr = new Long[array.length];
        for(int i =0; i < array.length;++i){ arr[i] = Long.valueOf(array[i]); }

        try{
            return new SQLArray(connect.createArrayOf("int8", arr));
        } catch (SQLException e) {
            throw new SQLRepositoryException("To SQL Array Cast Exception");
        }
    }
    protected SQLArray toStringSQLArray(Connection connect, String[] array) throws SQLRepositoryException{
        if(array == null) return new SQLArray(null);
        try{
            return new SQLArray(connect.createArrayOf("varchar", array));
        } catch (SQLException e) {
            throw new SQLRepositoryException("To SQL Array Cast Exception");
        }
    }
}

