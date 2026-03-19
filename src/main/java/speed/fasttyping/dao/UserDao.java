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
                username VARCHAR(50) NOT NULL,
                password VARCHAR(100) NOT NULL
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void create(String username, String password) {
        String hashedPassword = hasher.hash(password);

        String sql = """
                INSERT INTO users (username, password)
                VALUES (?, ?)""";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);

            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Помилка при збереженні: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT id, username, password FROM users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");

                    if (hasher.verify(password, storedHash)) {
                        return new User(
                                rs.getInt("id"),
                                rs.getString("username")
                        );
                    }
                }
            }
        }
        return null;
    }

    public boolean existsByUsername(String username) throws SQLException {
        String sql = """
                SELECT id FROM users
                WHERE username = ?""";

        try(PreparedStatement prsmt = connection.prepareStatement(sql)) {
            prsmt.setString(1, username);

            try(ResultSet rs = prsmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}