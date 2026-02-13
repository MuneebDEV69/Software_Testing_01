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
}
