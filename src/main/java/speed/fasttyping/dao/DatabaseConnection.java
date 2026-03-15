package speed.fasttyping.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private Connection connection;

    private String url = "jdbc:h2:./data/typing_tutor_db";
    private String user = "root";
    private String password = "";

    private DatabaseConnection() throws SQLException {
        try {
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if(instance == null) {
            instance = new DatabaseConnection();
        } else if(instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }

        return instance;
    }
}
