package business;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import bll.EditorBO;
import bll.IEditorBO;
import dal.IFacadeDAO;

/**
 * Business-layer tests for import and transliteration behaviour through the
 * IEditorBO interface. These act as command-like tests for Import/Transliterate
 * functionality as described in the assignment.
 */
public class EditorBOImportAndTransliterateTest {

    /**
     * Simple façade stub that records calls made by EditorBO.
     */
    private static class FacadeDAOMock implements IFacadeDAO {
        boolean createFileCalled = false;
        String lastCreatedFileName;
        String lastCreatedContent;

        @Override
        public boolean createFileInDB(String nameOfFile, String content) {
            createFileCalled = true;
            lastCreatedFileName = nameOfFile;
            lastCreatedContent = content;
            return true;
        }

        @Override
        public boolean updateFileInDB(int id, String fileName, int pageNumber, String content) {
            return false;
        }

        @Override
        public boolean deleteFileInDB(int id) {
            return false;
        }

        @Override
        public java.util.List<dto.Documents> getFilesFromDB() {
            return Collections.emptyList();
        }

        @Override
        public String transliterateInDB(int pageId, String arabicText) {
            // Simulate a simple transliteration result
            return "TRANSLITERATED:" + arabicText;
        }

        @Override
        public java.util.Map<String, String> lemmatizeWords(String text) {
            return Collections.emptyMap();
        }

        @Override
        public java.util.Map<String, java.util.List<String>> extractPOS(String text) {
            return Collections.emptyMap();
        }

        @Override
        public java.util.Map<String, String> extractRoots(String text) {
            return Collections.emptyMap();
        }

        @Override
        public double performTFIDF(java.util.List<String> unSelectedDocsContent, String selectedDocContent) {
            return 0.0;
        }

        @Override
        public java.util.Map<String, Double> performPMI(String content) {
            return Collections.emptyMap();
        }

        @Override
        public java.util.Map<String, Double> performPKL(String content) {
            return Collections.emptyMap();
        }

        @Override
        public java.util.Map<String, String> stemWords(String text) {
            return Collections.emptyMap();
        }

        @Override
        public java.util.Map<String, String> segmentWords(String text) {
            return Collections.emptyMap();
        }
    }

    @Test
    void testImportTextFilesCallsCreateFileOnFacade() throws IOException {
        FacadeDAOMock mockDao = new FacadeDAOMock();
        IEditorBO editor = new EditorBO(mockDao);

        // Create a temporary .txt file with known content
        File tempFile = File.createTempFile("import-test", ".txt");
        tempFile.deleteOnExit();

        String content = "Line 1" + System.lineSeparator() + "Line 2";
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }

        String fileName = "sample-import.txt";

        boolean result = editor.importTextFiles(tempFile, fileName);

        assertTrue(result, "Import should report success when facade createFileInDB succeeds");
        assertTrue(mockDao.createFileCalled, "Facade createFileInDB should be called by importTextFiles");
        assertEquals(fileName, mockDao.lastCreatedFileName, "Facade should receive the same file name");
        assertNotNull(mockDao.lastCreatedContent, "Facade should receive non-null file content");
        assertTrue(mockDao.lastCreatedContent.contains("Line 1"),
                "Facade content should contain the text from the imported file");
    }

    @Test
    void testTransliterateDelegatesToFacade() {
        FacadeDAOMock mockDao = new FacadeDAOMock();
        IEditorBO editor = new EditorBO(mockDao);

        String arabicText = "سلام";
        String result = editor.transliterate(1, arabicText);

        assertEquals("TRANSLITERATED:" + arabicText, result,
                "Transliterate should delegate to facade transliterateInDB and return its result");
    }
}
