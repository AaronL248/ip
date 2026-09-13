package marcus.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    /** Verifies that harmless surrounding and repeated whitespace is accepted. */
    @Test
    void getCommandType_extraWhitespace_stillRecognisesCommand() {
        assertTrue(parser.getCommandType("  todo   read book  ") == Parser.CommandType.TODO);
        assertTrue(parser.getArguments("  todo   read book  ", Parser.CommandType.TODO)
                .equals("read book"));
    }

    /** Verifies that null input is treated as an invalid command instead of crashing. */
    @Test
    void getCommandType_nullCommand_returnsUnknown() {
        assertTrue(parser.getCommandType(null) == Parser.CommandType.UNKNOWN);
        assertNull(parser.createTask(null, Parser.CommandType.TODO));
    }

    /** Verifies that an event cannot end at or before its start time. */
    @Test
    void createTask_invalidEventOrder_returnsNull() {
        assertNull(parser.createTask("event meeting /from 2026-09-15 16:00 /to 2026-09-15 16:00",
                Parser.CommandType.EVENT));
        assertNull(parser.createTask("event meeting /from 2026-09-15 17:00 /to 2026-09-15 16:00",
                Parser.CommandType.EVENT));
    }

    /** Verifies that repeated event markers are rejected rather than parsed ambiguously. */
    @Test
    void createTask_repeatedEventMarker_returnsNull() {
        assertNull(parser.createTask("event meeting /from Monday /from Tuesday /to 5pm",
                Parser.CommandType.EVENT));
    }
}
