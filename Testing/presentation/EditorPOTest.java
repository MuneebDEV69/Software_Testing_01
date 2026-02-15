package presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import pl.EditorPO;

/**
 * Basic presentation-layer tests for EditorPO.
 *
 * These tests use reflection to exercise private helper methods that
 * support GUI behaviour (word count and line count labels).
 */
public class EditorPOTest {

    /**
     * Verifies that the internal word-count logic in the presentation layer
     * correctly counts whitespace-separated tokens.
     */
    @Test
    void testCalculateWordCountForSimpleText() throws Exception {
        // Arrange: create EditorPO instance (business object not needed here)
        EditorPO editor = new EditorPO(null);

        // Access private method: int calculateWordCount(String text)
        Method method = EditorPO.class.getDeclaredMethod("calculateWordCount", String.class);
        method.setAccessible(true);

        String text = "one two three four five"; // 5 words

        // Act
        int result = (int) method.invoke(editor, text);

        // Assert
        assertEquals(5, result, "Word count should match number of whitespace-separated words");
    }

    /**
     * Verifies that the internal line-count logic in the presentation layer
     * correctly counts the number of lines in multi-line content.
     */
    @Test
    void testCalculateLineCountForMultilineText() throws Exception {
        // Arrange
        EditorPO editor = new EditorPO(null);

        // Access private method: int calculateLineCount(String content)
        Method method = EditorPO.class.getDeclaredMethod("calculateLineCount", String.class);
        method.setAccessible(true);

        String content = "line1\nline2\nline3"; // 3 lines

        // Act
        int result = (int) method.invoke(editor, content);

        // Assert
        assertEquals(3, result, "Line count should match the number of newline-separated lines");
    }
}
