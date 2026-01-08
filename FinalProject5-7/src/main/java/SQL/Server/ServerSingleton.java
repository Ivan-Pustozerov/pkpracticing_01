package SQL.Server;

public class ServerSingleton {
    private static final Server INSTANCE = new Server();
    private ServerSingleton(){}

    public static Server getINSTANCE() {
        return INSTANCE;
    }
}
