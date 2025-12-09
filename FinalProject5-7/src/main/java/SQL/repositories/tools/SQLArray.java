package SQL.repositories.tools;

import SQL.repositories.SQLRepositoryException;

import java.sql.Array;
import java.sql.SQLException;

public class SQLArray implements AutoCloseable {
    private final java.sql.Array innerArray;
    private volatile boolean isAlive; //чтобы не менялся порядок операций

    public SQLArray(Array innerArray){
        this.innerArray = innerArray;
        isAlive = true;
    }

    public java.sql.Array innerArray(){ return innerArray;}
    public boolean isAlive(){ return isAlive;}


    @Override
    public void close() throws SQLRepositoryException {
        isAlive = false;
        try{
            if(innerArray != null) this.innerArray.free();
        } catch (SQLException e) {
            throw new SQLRepositoryException("SQL Array Close Error");
        }

    }
}
