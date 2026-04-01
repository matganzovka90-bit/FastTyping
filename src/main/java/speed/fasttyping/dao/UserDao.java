package speed.fasttyping.dao;

import speed.fasttyping.model.User;
import speed.fasttyping.util.PasswordHasher;

import java.sql.*;

public class UserDao {
    private Connection connection;
    private final PasswordHasher hasher = new PasswordHasher();

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public void createUserTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) NOT NULL UNIQUE,
                password VARCHAR(100) NOT NULL,
                is_remembered TINYINT DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void create(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hasher.hash(password));
            pstmt.executeUpdate();
        }
    }

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT id, username, password FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && hasher.verify(password, rs.getString("password"))) {
                    return new User(rs.getInt("id"), rs.getString("username"));
                }
            }
        }
        return null;
    }

    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void setRememberMe(int userId, boolean remember) throws SQLException {
        String sql = "UPDATE users SET is_remembered = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, remember ? 1 : 0);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }

    public User findRememberedUser() throws SQLException {
        String sql = "SELECT id, username FROM users WHERE is_remembered = 1 LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"));
            }
        }
        return null;
    }

    public void updateUsername(int userId, String newUsername) throws SQLException {
        String sql = "UPDATE users SET username = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newUsername);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }

    public boolean verifyPassword(int userId, String password) throws SQLException {
        String sql = "SELECT password FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return hasher.verify(password, rs.getString("password"));
                }
            }
        }
        return false;
    }

    public void updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, hasher.hash(newPassword));
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }
}