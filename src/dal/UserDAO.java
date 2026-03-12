package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserDAO implements IUserDAO {

    private static final Logger LOGGER = LogManager.getLogger(UserDAO.class);

    private final Connection conn;

    public UserDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Constructor used for testing – allows injecting a custom connection.
     *
     * @param conn the JDBC connection to use
     */
    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean authenticate(String username, String passwordHash) {
        String query = "SELECT 1 FROM users WHERE username = ? AND passwordHash = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.error("Authentication query failed for user '{}': {}", username, e.getMessage());
            return false;
        }
    }
}
