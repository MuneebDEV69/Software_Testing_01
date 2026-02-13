package business;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import bll.SearchWord;
import dto.Documents;
import dto.Pages;

/**
 * Test class for SearchWord.searchKeyword() method.
 * First incremental test: positive case where a valid keyword
 * (length greater than or equal to 3) is found in the document.
 */
public class SearchWordTest {

    /**
     * Positive test: Valid keyword (more than 3 characters) is found
     * in the document pages.
     */
    @Test
    void testSearchKeywordFound() {
        Pages page1 = new Pages(1, 1, 1, "This is for testing the search functionality");
        List<Pages> pagesList = new ArrayList<>();
        pagesList.add(page1);

        Documents doc1 = new Documents(1, "TestFile.txt", "hash123", "2024-01-01", "2024-01-01", pagesList);
        List<Documents> docs = new ArrayList<>();
        docs.add(doc1);

        List<String> results = SearchWord.searchKeyword("testing", docs);

        assertFalse(results.isEmpty(), "Search should find the keyword 'testing'");
        assertTrue(results.get(0).contains("TestFile.txt"), "Result should contain the document name");
        assertTrue(results.get(0).contains("testing"), "Result should contain the search keyword");
    }
}
