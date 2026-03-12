package data;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import dal.DatabaseConnection;

/**
 * Test class for DatabaseConnection Singleton Pattern
 * Verifies that only one instance is created and returned
 */
public class DatabaseConnectionTest {

    /**
     * Test to verify Singleton pattern implementation
     * This test checks if getInstance() always returns the same object reference
     */
    @Test
    void testSingletonPattern() {
        // Get first instance of DatabaseConnection
        DatabaseConnection instance1 = DatabaseConnection.getInstance();
        
        // Get second instance of DatabaseConnection
        DatabaseConnection instance2 = DatabaseConnection.getInstance();
        
        // Assert that both references point to the same object (Singleton pattern)
        // If this fails, Singleton pattern is broken
        assertSame(instance1, instance2, 
            "DatabaseConnection should return the same instance (Singleton pattern)");
    }

    /**
     * Verifies that isConnected() does not throw an exception even when
     * no database is available (e.g., in CI), and returns a boolean result.
     */
    @Test
    void testIsConnectedDoesNotThrow() {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        assertDoesNotThrow(() -> instance.isConnected(),
            "isConnected() should never throw an exception regardless of connection state");
    }
}
