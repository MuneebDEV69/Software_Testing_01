package business;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bll.LoginBO;
import dal.HashCalculator;
import dal.IUserDAO;

/**
 * Unit tests for {@link LoginBO}.
 *
 * A lightweight stub of {@link IUserDAO} is used so the tests run without a
 * real database connection.
 */
public class LoginBOTest {

    /** Stub DAO that accepts only the pre-computed hash of "secret". */
    private static class StubUserDAO implements IUserDAO {
        private final String validUsername;
        private final String validPasswordHash;

        StubUserDAO(String validUsername, String validPasswordHash) {
            this.validUsername = validUsername;
            this.validPasswordHash = validPasswordHash;
        }

        @Override
        public boolean authenticate(String username, String passwordHash) {
            return validUsername.equals(username) && validPasswordHash.equals(passwordHash);
        }
    }

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "secret";

    private LoginBO loginBO;

    @BeforeEach
    void setUp() throws Exception {
        String passwordHash = HashCalculator.calculateHash(PASSWORD);
        loginBO = new LoginBO(new StubUserDAO(USERNAME, passwordHash));
    }

    /**
     * Positive test: valid username and password should return true.
     */
    @Test
    void testLoginWithValidCredentialsReturnsTrue() {
        assertTrue(loginBO.login(USERNAME, PASSWORD),
                "Login should succeed with correct credentials");
    }

    /**
     * Negative test: wrong password should return false.
     */
    @Test
    void testLoginWithWrongPasswordReturnsFalse() {
        assertFalse(loginBO.login(USERNAME, "wrongpassword"),
                "Login should fail with incorrect password");
    }

    /**
     * Negative test: wrong username should return false.
     */
    @Test
    void testLoginWithWrongUsernameReturnsFalse() {
        assertFalse(loginBO.login("unknownuser", PASSWORD),
                "Login should fail with incorrect username");
    }

    /**
     * Boundary test: empty username should return false without calling the DAO.
     */
    @Test
    void testLoginWithEmptyUsernameReturnsFalse() {
        assertFalse(loginBO.login("", PASSWORD),
                "Login should fail when username is empty");
    }

    /**
     * Boundary test: blank (whitespace-only) username should return false.
     */
    @Test
    void testLoginWithBlankUsernameReturnsFalse() {
        assertFalse(loginBO.login("   ", PASSWORD),
                "Login should fail when username is blank");
    }

    /**
     * Boundary test: empty password should return false without calling the DAO.
     */
    @Test
    void testLoginWithEmptyPasswordReturnsFalse() {
        assertFalse(loginBO.login(USERNAME, ""),
                "Login should fail when password is empty");
    }

    /**
     * Boundary test: null username should return false.
     */
    @Test
    void testLoginWithNullUsernameReturnsFalse() {
        assertFalse(loginBO.login(null, PASSWORD),
                "Login should fail when username is null");
    }

    /**
     * Boundary test: null password should return false.
     */
    @Test
    void testLoginWithNullPasswordReturnsFalse() {
        assertFalse(loginBO.login(USERNAME, null),
                "Login should fail when password is null");
    }
}
