package SQL.repositories.tools;

import SQL.DTO.DTO;
import SQL.SQLRepositoryException;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class Repository {
///=================================СЛУЖЕБНЫЕ=СОСТОЯНИЯ===============================================
    protected final String url;
    protected final String username;
    protected final String password;
///===================================================================================================

    /// Универсальный конструктор таблиц
    protected int initTable(String sql) throws SQLRepositoryException
    {
        try(Connection connect = DriverManager.getConnection(url,username,password)) {
            Statement statement = connect.createStatement();
            return statement.executeUpdate(sql);

        } catch (SQLException e) {
            //log
            throw new SQLRepositoryException("Init Table Error!");
        }
    }

    /// Универсальный запрос на обновление
    protected int executeUpdate(String sql,
                                SQLConsumer<PreparedStatement> pstateTemplate) throws SQLRepositoryException
    {
        try(Connection connect = DriverManager.getConnection(url,username,password);
            PreparedStatement pstate = connect.prepareStatement(sql)) {


            pstateTemplate.accept(pstate);//внутреннее состояние объекта pstate изменяется void методом

            return pstate.executeUpdate();
        }
        catch (SQLException e) {
            //log
            System.err.println(e.toString());
            throw new SQLRepositoryException("Execute Update Error!");
        }
    }

    /// Универсальный запрос на поиск
    protected <T extends DTO> ArrayList<T> executeQuery(String sql,
                                                        SQLConsumer<PreparedStatement> pstateTemplate,
                                                        SQLResultSetFunction<T> funcTemplate)
                                                            throws SQLRepositoryException
    {
        try(Connection connect = DriverManager.getConnection(url,username,password);
            PreparedStatement pstate = connect.prepareStatement(sql.trim());) {


            ArrayList<T> result = new ArrayList<>();


            pstateTemplate.accept(pstate);


            ///ОБЯЗАТЕЛЬНО! ResultSet и PreparedStatement, Array ЗАКРЫВАТЬ - ОБЕРТКИ НАД СИСТЕМНЫМИ ПОТОКАМИ!
            try(ResultSet set = pstate.executeQuery()){
                while(set.next()){
                    result.add(funcTemplate.apply(set));
                }

                return result;
            }

        } catch (SQLException e) {
            //log
            e.printStackTrace();
            throw new SQLRepositoryException("Execute Query Error!");
        }
    }


///---------------------------------------ВСПОМОГАТЕЛЬНЫЙ-ФУНКЦИОНАЛ----------------------------------
    protected Repository(String url, String username, String password){
        this.url = url;            //   абстрактные классы могут иметь конструкторы
        this.username = username; //более того компиль всегда добавляет пустой конструктор
        this.password = password;//                 если не указаны другие
    }

    protected static String readCommand(String filepath){
        StringBuffer res = new StringBuffer();
        String line;
        try(BufferedReader reader = new BufferedReader(new FileReader(filepath))){
            while((line = reader.readLine()) != null){
                res.append(line);
            }
            return res.toString();

        }catch (IOException e) {
            //log
            throw new RuntimeException("Command Error");
        }
    }

    protected static double[] toDoubleBaseArray(java.sql.Array array) throws SQLRepositoryException{
        double[] result = null;
        try {
            Double[] arr = (Double[]) array.getArray();
            result = new double[arr.length];
            for(int i = 0;i<arr.length;++i) result[i] = arr[i];
        }
        catch (SQLException e) {
            //log
            throw new SQLRepositoryException("From SQLArray Cast Error");
        }
        finally{
            try{
                array.free();
            }
            catch (SQLException e) {
                //log
                throw new SQLRepositoryException("SQLArray Free Error");
            }


        }
        return result;
    }

    protected java.sql.Array toDoubleSQLArray(double[] array) throws SQLRepositoryException{
        Double[] arr = new Double[array.length];
        for(int i =0; i < array.length;++i){ arr[i] = Double.valueOf(array[i]); }

        try(Connection connect = DriverManager.getConnection(url,username,password)){
            return connect.createArrayOf("float8", arr);

        } catch (SQLException e) {
            throw new SQLRepositoryException("To SQL Array Cast Exception");
        }
    }

}

