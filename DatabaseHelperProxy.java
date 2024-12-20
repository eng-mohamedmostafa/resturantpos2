package resturantpos2;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseHelperProxy extends DatabaseHelper {
    private static DatabaseHelperProxy instance;


    private DatabaseHelperProxy() throws SQLException {
        super();
    }

    public static synchronized DatabaseHelperProxy getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseHelperProxy();
        }
        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        System.out.println("Proxy: Getting database connection...");
        return super.getConnection();
    }
}
