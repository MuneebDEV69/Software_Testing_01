package data;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.List;

import org.junit.jupiter.api.Test;

import dal.EditorDBDAO;
import dal.HashCalculator;
import dal.IEditorDBDAO;
import dto.Documents;
import dto.Pages;

/**
 * Integration-style test to verify hashing metadata behaviour in the
 * persistence layer.
 *
 * Requirement mapping:
 *  - "editing a file changes the current session hash but retains the
 *    original import hash in the database metadata".
 *
 * In the given schema, the files table stores a single fileHash value
 * (original import hash). Updates modify page content and analytics but do
 * not touch this stored hash. This test checks that behaviour explicitly.
 */
public class HashMetadataIntegrityTest {

    @Test
    void testEditingFileKeepsStoredHashButChangesComputedHash() {
        // Use the data-layer interface type to keep the test swappable.
        IEditorDBDAO dao = new EditorDBDAO();

        // Arrange: use an existing document from the database metadata
        List<Documents> documents = dao.getFilesFromDB();
        assumeFalse(documents.isEmpty(),
                "Skipping hash metadata test because no documents exist in the database");

        Documents createdDoc = documents.get(0);
        String fileName = createdDoc.getName();

        String originalStoredHash = createdDoc.getHash();
        assertNotNull(originalStoredHash, "Stored hash should not be null after import");

        // Compute a new hash for modified content to represent the current session hash
                String modifiedContent = "Modified content for hash metadata test.";
                String currentSessionHash;
                try {
                        currentSessionHash = HashCalculator.calculateHash(modifiedContent);
                } catch (Exception e) {
                        throw new AssertionError("Hash calculation failed for modified content", e);
                }

        // Sanity check: current session hash after edit should differ from original import hash
        assertNotEquals(originalStoredHash, currentSessionHash,
                "Current session hash for modified content should differ from original stored hash");

        // Act: update the file content in the database (simulating an edit/save operation)
                // Use the first available page number for this document when updating
                int pageNumber = 1;
                List<Pages> pages = createdDoc.getPages();
                if (pages != null && !pages.isEmpty()) {
                        pageNumber = pages.get(0).getPageNumber();
                }

                boolean updated = dao.updateFileInDB(createdDoc.getId(), fileName, pageNumber, modifiedContent);
        assertTrue(updated, "File update operation should succeed");

        // Reload the document metadata from the database
        List<Documents> documentsAfterUpdate = dao.getFilesFromDB();
        Documents updatedDoc = documentsAfterUpdate.stream()
                .filter(d -> d.getId() == createdDoc.getId())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Updated document not found in database"));

        String storedHashAfterUpdate = updatedDoc.getHash();

        // Assert: database continues to store the original import hash
        assertEquals(originalStoredHash, storedHashAfterUpdate,
                "Database metadata should retain the original import hash even after edits");

        // Note: we intentionally do not revert the content here; only the
        // metadata behaviour (hash field) is under test.
    }
}
