package speed.fasttyping.dao;

import speed.fasttyping.model.TypingResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypingResultDao {
    private Connection connection;

    public TypingResultDao(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS results (
                id         INT AUTO_INCREMENT PRIMARY KEY,
                user_id    INT NOT NULL,
                wpm        INT NOT NULL,
                accuracy   DOUBLE NOT NULL,
                errors     INT NOT NULL,
                mode_name  VARCHAR(50) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        try(Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void save(TypingResult result) throws SQLException {
        String sql = "INSERT INTO results (user_id, wpm, accuracy, errors, mode_name) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, result.getUserId());
            pstmt.setInt(2, result.getWpm());
            pstmt.setDouble(3, result.getAccuracy());
            pstmt.setInt(4, result.getErrors());
            pstmt.setString(5, result.getModeName());
            pstmt.executeUpdate();
        }
    }

    public List<TypingResult> getByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM results WHERE user_id = ? ORDER BY created_at DESC";
        List<TypingResult> results = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TypingResult result = new TypingResult(
                            rs.getInt("user_id"),
                            rs.getInt("wpm"),
                            rs.getDouble("accuracy"),
                            rs.getInt("errors"),
                            rs.getString("mode_name")
                    );
                    result.setId(rs.getInt("id"));
                    results.add(result);
                }
            }
        }
        return results;
    }

    public int getBestWpm(int userId) throws SQLException {
        String sql = "SELECT MAX(wpm) FROM results WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }
}
