package data;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.List;

import dal.EditorDBDAO;
import dto.Documents;

/**
 * Tests that EditorDBDAO methods return safe defaults (not NullPointerException)
 * when the database connection is unavailable (conn == null).
 *
 * This covers the startup-crash fix where a missing or unreachable database
 * would cause a NullPointerException instead of a graceful failure.
 */
public class EditorDBDAONullConnectionTest {

    /**
     * Forces the internal connection field to null via reflection, simulating the
     * state where DatabaseConnection could not establish a connection.
     */
    private EditorDBDAO createDaoWithNullConnection() throws Exception {
        EditorDBDAO dao = new EditorDBDAO();
        Field connField = EditorDBDAO.class.getDeclaredField("conn");
        connField.setAccessible(true);
        connField.set(dao, null);
        return dao;
    }

    /**
     * Verifies that getFilesFromDB() returns an empty list (not NPE)
     * when the database connection is null.
     */
    @Test
    void testGetFilesFromDBReturnsEmptyListWhenConnectionIsNull() throws Exception {
        EditorDBDAO dao = createDaoWithNullConnection();

        List<Documents> result = dao.getFilesFromDB();

        assertNotNull(result, "getFilesFromDB() should return a non-null list when connection is null");
        assertTrue(result.isEmpty(), "getFilesFromDB() should return an empty list when connection is null");
    }

    /**
     * Verifies that createFileInDB() returns false (not NPE)
     * when the database connection is null.
     */
    @Test
    void testCreateFileInDBReturnsFalseWhenConnectionIsNull() throws Exception {
        EditorDBDAO dao = createDaoWithNullConnection();

        boolean result = dao.createFileInDB("test.txt", "sample content");

        assertFalse(result, "createFileInDB() should return false when connection is null");
    }

    /**
     * Verifies that updateFileInDB() returns false (not NPE)
     * when the database connection is null.
     */
    @Test
    void testUpdateFileInDBReturnsFalseWhenConnectionIsNull() throws Exception {
        EditorDBDAO dao = createDaoWithNullConnection();

        boolean result = dao.updateFileInDB(1, "test.txt", 1, "updated content");

        assertFalse(result, "updateFileInDB() should return false when connection is null");
    }

    /**
     * Verifies that deleteFileInDB() returns false (not NPE)
     * when the database connection is null.
     */
    @Test
    void testDeleteFileInDBReturnsFalseWhenConnectionIsNull() throws Exception {
        EditorDBDAO dao = createDaoWithNullConnection();

        boolean result = dao.deleteFileInDB(1);

        assertFalse(result, "deleteFileInDB() should return false when connection is null");
    }
}
