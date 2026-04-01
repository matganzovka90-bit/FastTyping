package speed.fasttyping.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class DaoFactory {
    private static volatile DaoFactory instance;
    private final Connection connection;

    private DaoFactory(Connection connection) {
        this.connection = connection;
    }

    public static DaoFactory getInstance() throws SQLException {
        if (instance == null) {
            synchronized (DaoFactory.class) {
                if (instance == null) {
                    Connection conn = DatabaseConnection.getInstance().getConnection();
                    instance = new DaoFactory(conn);
                }
            }
        }
        return instance;
    }

    public UserDao getUserDao() {
        return new UserDao(connection);
    }

    public TypingResultDao getTypingResultDao() {
        return new TypingResultDao(connection);
    }

    public AchievementDao getAchievementDao() { return new AchievementDao(connection); }
}
