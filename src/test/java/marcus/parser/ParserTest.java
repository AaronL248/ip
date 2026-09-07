package marcus.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests command parsing and tag validation.
 */
class ParserTest {
    private final Parser parser = new Parser();

    /** Verifies that tagging commands are classified correctly. */
    @Test
    void getCommandType_taggingCommands_returnsTagCommandTypes() {
        assertTrue(parser.getCommandType("tag 1 #fun") == Parser.CommandType.TAG);
        assertTrue(parser.getCommandType("untag 1 #fun") == Parser.CommandType.UNTAG);
    }

    /** Verifies that tag validation accepts the supported syntax. */
    @Test
    void isValidTag_supportedTagFormats_returnsTrue() {
        assertTrue(Parser.isValidTag("#fun"));
        assertTrue(Parser.isValidTag("#weekend-2026"));
        assertTrue(Parser.isValidTag("#school_project"));
    }

    /** Verifies that tag validation rejects missing hashes and spaces. */
    @Test
    void isValidTag_invalidTagFormats_returnsFalse() {
        assertFalse(Parser.isValidTag("fun"));
        assertFalse(Parser.isValidTag("#school project"));
        assertFalse(Parser.isValidTag("#"));
    }
}
