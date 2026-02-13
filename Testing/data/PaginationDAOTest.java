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

    /**
     * Test boundary case: Content with exactly 100 characters
     * Expected: Should return exactly 1 page (boundary condition)
     */
    @Test
    void testPaginateExactly100Characters() {
        // Create content with exactly 100 characters (page size boundary)
        String content = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
        
        // Verify content length is exactly 100
        assertEquals(100, content.length(), "Test setup: content should be exactly 100 chars");
        
        // Call the paginate method
        List<Pages> pages = PaginationDAO.paginate(content);
        
        // Assert that exactly 1 page is created (boundary test)
        assertEquals(1, pages.size(), "Content with exactly 100 chars should create exactly 1 page");
        
        // Assert that page number is 1
        assertEquals(1, pages.get(0).getPageNumber(), "Page number should be 1");
        
        // Assert that page content matches original content
        assertEquals(content, pages.get(0).getPageContent(), 
            "Page content should contain all 100 characters");
    }

    /**
     * Test boundary case: Content with 101 characters (exceeds page size by 1)
     * Expected: Should return 2 pages (first=100 chars, second=1 char)
     */
    @Test
    void testPaginate101Characters() {
        // Create content with 101 characters (1 more than page size)
        String content = "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901";
        
        // Verify content length is exactly 101
        assertEquals(101, content.length(), "Test setup: content should be exactly 101 chars");
        
        // Call the paginate method
        List<Pages> pages = PaginationDAO.paginate(content);
        
        // Assert that exactly 2 pages are created
        assertEquals(2, pages.size(), "Content with 101 chars should create 2 pages");
        
        // Assert first page has 100 characters
        assertEquals(100, pages.get(0).getPageContent().length(), 
            "First page should contain 100 characters");
        
        // Assert second page has 1 character
        assertEquals(1, pages.get(1).getPageContent().length(), 
            "Second page should contain 1 character");
        
        // Assert page numbers are correct
        assertEquals(1, pages.get(0).getPageNumber(), "First page number should be 1");
        assertEquals(2, pages.get(1).getPageNumber(), "Second page number should be 2");
    }

    /**
     * Test negative case: Null content
     * Expected: Should return 1 page with empty content (graceful handling)
     */
    @Test
    void testPaginateNullContent() {
        // Pass null as content (negative test case)
        String content = null;
        
        // Call the paginate method with null
        List<Pages> pages = PaginationDAO.paginate(content);
        
        // Assert that 1 empty page is created (graceful null handling)
        assertEquals(1, pages.size(), "Null content should return 1 empty page");
        
        // Assert that page content is empty string
        assertEquals("", pages.get(0).getPageContent(), 
            "Page content should be empty string for null input");
        
        // Assert page number is 1
        assertEquals(1, pages.get(0).getPageNumber(), "Page number should be 1");
    }

    /**
     * Test negative case: Empty string content
     * Expected: Should return 1 page with empty content (graceful handling)
     */
    @Test
    void testPaginateEmptyString() {
        // Pass empty string as content (negative test case)
        String content = "";
        
        // Call the paginate method with empty string
        List<Pages> pages = PaginationDAO.paginate(content);
        
        // Assert that 1 empty page is created (graceful empty string handling)
        assertEquals(1, pages.size(), "Empty string should return 1 empty page");
        
        // Assert that page content is empty string
        assertEquals("", pages.get(0).getPageContent(), 
            "Page content should be empty string for empty input");
        
        // Assert page number is 1
        assertEquals(1, pages.get(0).getPageNumber(), "Page number should be 1");
    }
}
