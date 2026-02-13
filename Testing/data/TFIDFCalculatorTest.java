package data;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import dal.TFIDFCalculator;

/**
 * Tests for TFIDFCalculator.calculateDocumentTfIdf().
 * First incremental test: simple Arabic corpus where the
 * expected TF-IDF score can be computed manually.
 */
public class TFIDFCalculatorTest {

    /**
     * Positive test with a small Arabic corpus.
     *
     * Corpus documents (after preprocessing):
     *  d1 = "ا ا ب"
     *  d2 = "ا ب ب"
     *
     * Query document: "ا ب"
     * After manual calculation, the expected average TF-IDF
     * score is log(2/3) / 2.
     */
    @Test
    void testCalculateDocumentTfIdfWithSimpleArabicCorpus() {
        TFIDFCalculator calculator = new TFIDFCalculator();

        // Build a very small Arabic corpus
        calculator.addDocumentToCorpus("ا ا ب");
        calculator.addDocumentToCorpus("ا ب ب");

        double result = calculator.calculateDocumentTfIdf("ا ب");

        double expected = Math.log(2.0 / 3.0) / 2.0;

        assertEquals(expected, result, 1e-9,
                "TF-IDF score should match the manually computed value for the simple corpus");
    }
    
    /**
     * Negative test: When the input document is empty, the TF-IDF
     * calculation should safely return 0.0 instead of failing.
     */
    @Test
    void testCalculateDocumentTfIdfWithEmptyDocumentReturnsZero() {
        TFIDFCalculator calculator = new TFIDFCalculator();

        double result = calculator.calculateDocumentTfIdf("");

        assertEquals(0.0, result, 1e-9,
                "Empty document should produce a TF-IDF score of 0.0");
    }

    /**
     * Negative test: Document with only non-Arabic special characters.
     * After preprocessing, the document becomes empty, but if the corpus
     * already contains Arabic text, the method should still return a
     * finite TF-IDF score without errors.
     */
    @Test
    void testCalculateDocumentTfIdfWithSpecialCharactersOnly() {
        TFIDFCalculator calculator = new TFIDFCalculator();

        // Add one simple Arabic document to the corpus
        calculator.addDocumentToCorpus("ا ب");

        // This document contains only non-Arabic characters and punctuation,
        // which will be stripped by preprocessing.
        double result = calculator.calculateDocumentTfIdf("1234 !!!");

        double expected = Math.log(2.0); // corpus size is 1, so log(1 + 1)

        assertEquals(expected, result, 1e-9,
                "Special-character-only document should yield a deterministic TF-IDF score");
    }
}
