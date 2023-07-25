package memorypriority.data;

import memorypriority.domain.User;
import memorypriority.util.MemoryPriorityException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbcAuthorizationRepository implements AuthorizationRepository {

    public static final Logger LOGGER = Logger.getLogger(JdbcAuthorizationRepository.class.getName());
    public User authenticateUser(String username, String hashedPassword) {
        String sql = "SELECT * FROM users WHERE username = ? AND hashed_password = ?";
        try (Connection conn = JdbcConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("username"),
                            rs.getString("hashed_password")
                    );
                } else {
                    LOGGER.log(Level.INFO, "user found");
                    return null;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database Error", ex);
            throw new MemoryPriorityException("Database error", ex);
        }
    }
}
