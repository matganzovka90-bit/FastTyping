package speed.fasttyping.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseInitializer {
    private final Connection connection;

    public DatabaseInitializer(Connection connection) {
        this.connection = connection;
    }

    public void initialize() throws SQLException {
        new UserDao(connection).createUserTable();
        new TypingResultDao(connection).createTable();
    }
}
