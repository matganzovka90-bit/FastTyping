package speed.fasttyping.dao;

import speed.fasttyping.model.TypingResult;

import java.sql.*;
import java.time.LocalDateTime;
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
                    results.add(mapRow(rs));
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

    public double getAverageWpm(int userId) throws SQLException {
        String sql = "SELECT AVG(wpm) FROM results WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble(1);
                    return Math.round(avg * 10.0) / 10.0;
                }
            }
        }
        return 0.0;
    }

    public List<TypingResult> getResultsByMode(int userId, String modeName) throws SQLException {
        if (modeName == null || modeName.isBlank()) return new ArrayList<>();

        String sql = "SELECT * FROM results WHERE user_id = ? AND mode_name = ? ORDER BY created_at DESC";
        List<TypingResult> results = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, modeName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    public List<TypingResult> getTopResults(int userId, int limit) throws SQLException {
        if (limit <= 0) return new ArrayList<>();

        String sql = "SELECT * FROM results WHERE user_id = ? ORDER BY wpm DESC LIMIT ?";
        List<TypingResult> results = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    private TypingResult mapRow(ResultSet rs) throws SQLException {
        LocalDateTime dateTime = rs.getObject("created_at", LocalDateTime.class);
        TypingResult result = new TypingResult(
                rs.getInt("user_id"),
                rs.getInt("wpm"),
                rs.getDouble("accuracy"),
                rs.getInt("errors"),
                rs.getString("mode_name"),
                dateTime
        );
        result.setId(rs.getInt("id"));
        return result;
    }
}
