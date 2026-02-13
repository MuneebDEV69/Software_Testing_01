package data;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import dal.HashCalculator;

/**
 * Tests for HashCalculator.calculateHash().
 * First incremental test: the same text input should always
 * produce the same MD5 hash value.
 */
public class HashCalculatorTest {

    /**
     * Positive test: Same input text should produce the same hash
     * value when calculateHash is called multiple times.
     */
    @Test
    void testCalculateHashReturnsSameValueForSameText() throws Exception {
        String text = "sample-text";

        String hash1 = HashCalculator.calculateHash(text);
        String hash2 = HashCalculator.calculateHash(text);

        assertEquals(hash1, hash2,
                "HashCalculator should return the same hash for identical input text");
    }
}
