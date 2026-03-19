package speed.fasttyping.dao;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class AchievementDao {
    private final Connection connection;

    public AchievementDao(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS achievements (
                id             INT AUTO_INCREMENT PRIMARY KEY,
                user_id        INT NOT NULL,
                achievement_id VARCHAR(50) NOT NULL,
                unlocked_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                CONSTRAINT unique_user_achievement UNIQUE(user_id, achievement_id)
            )
            """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void save(int userId, String achievementId) throws SQLException {
        String sql = """
                MERGE INTO achievements (user_id, achievement_id)
                    KEY (user_id, achievement_id)
                    VALUES (?, ?)""";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, achievementId);
            pstmt.executeUpdate();
        }
    }

    public Set<String> getUnlockedIds(int userId) throws SQLException {
        String sql = "SELECT achievement_id FROM achievements WHERE user_id = ?";
        Set<String> ids = new HashSet<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString("achievement_id"));
                }
            }
        }
        return ids;
    }
}
