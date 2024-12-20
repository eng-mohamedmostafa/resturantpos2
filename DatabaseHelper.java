package resturantpos2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3307/resturantpos3";
    private static final String USER = "root";
    private static final String PASS = "Root123456Root";
    private static DatabaseHelper instance;
    private Connection connection;

    DatabaseHelper() throws SQLException {
        try {
            this.connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            throw new SQLException("Failed to create a database connection.", e);
        }
    }

    public static synchronized DatabaseHelper getInstance() throws SQLException {
        if (instance == null || instance.connection == null || instance.connection.isClosed()) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        }
        return connection;
    }

    public static void closeConnection() {
        if (instance != null && instance.connection != null) {
            try {
                instance.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                instance = null;
            }
        }
    }

    public static void main(String[] args) {
        try {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance();
            Connection conn = dbHelper.getConnection();
            if (conn != null) {
                System.out.println("Connection successful!");
                DatabaseHelper.closeConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
