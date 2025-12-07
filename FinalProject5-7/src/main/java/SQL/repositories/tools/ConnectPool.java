package SQL.repositories.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectPool{
    private final String url;
    private final String username;
    private final String password;
    private final Connection[] pool;
    private final boolean[] freeSlots;

    public ConnectPool(String url, String username, String password, int poolLen) throws ConnectPoolException {
        this.url = url;
        this.username = username;
        this.password = password;
        pool = new Connection[poolLen];
        freeSlots = new boolean[poolLen];
        for(int i = 0; i<pool.length;++i){
            try {
                pool[i]=DriverManager.getConnection(url,username,password);
                pool[i].setAutoCommit(true);
            }
            catch (SQLException e) {
                for(int j =0; i< pool.length; ++j){
                    try {
                        if(pool[j] != null) pool[j].close();
                    } catch (SQLException ex) {
                        System.out.println("Pool Close Error");
                    }
                }
                throw new ConnectPoolException("Pool Error");
            }
        }
        for(int i = 0; i<pool.length;++i){freeSlots[i] = true;}
    }

    private Connection get(int i) throws ConnectPoolException {
        try {
            if(!pool[i].isValid(2)){
                pool[i] = DriverManager.getConnection(url,username,password);
            }
            freeSlots[i] = false;
            return pool[i];

        } catch (SQLException e) {
            throw new ConnectPoolException("Get Error");
        }
    }

    public Connection getConnection(int index) throws ConnectPoolException {
        if(!freeSlots[index]) return null;
        if( index < 0 || index >= pool.length) return get(0);
        return get(index);
    }

    public boolean isEmpty(){
        for(boolean b : freeSlots){
            if(b) return false;
        }
        return true;
    }

    public void free(){
        for(int i =0; i< pool.length;++i){
            freeSlots[i]=true;
        }
    }


}
