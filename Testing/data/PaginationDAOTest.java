package data;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import dal.PaginationDAO;
import dto.Pages;

/**
 * Test class for PaginationDAO.paginate() method
 * Tests splitting file content into pages of 100 characters
 */
public class PaginationDAOTest {

    /**
     * Test positive case: Normal content with less than 100 characters
     * Expected: Should return 1 page containing the entire content
     */
    @Test
    void testPaginateNormalContent() {
        // Create sample content with 50 characters (less than page size of 100)
        String content = "This is a test content for pagination logic test";
        
        // Call the paginate method
        List<Pages> pages = PaginationDAO.paginate(content);
        
        // Assert that exactly 1 page is created
        assertEquals(1, pages.size(), "Content less than 100 chars should create 1 page");
        
        // Assert that page number is 1
        assertEquals(1, pages.get(0).getPageNumber(), "First page number should be 1");
        
        // Assert that page content matches original content
        assertEquals(content, pages.get(0).getPageContent(), 
            "Page content should match the original content");
    }
}
