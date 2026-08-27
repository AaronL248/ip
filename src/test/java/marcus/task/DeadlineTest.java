package marcus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the behaviour of {@link Deadline}.
 */
class DeadlineTest {
    @Test
    void toString_newDeadline_showsDeadlineAndIncompleteStatus() {
        LocalDate date = LocalDate.parse("2026-08-27");
        Deadline deadline = new Deadline("Meet Drake", date);

        assertEquals("[D][ ] Meet Drake (by: Aug 27 2026)", deadline.toString());
    }

    @Test
    void toString_completedDeadline_showsCompletedStatus() {
        Deadline deadline = new Deadline("Meet Drake", LocalDate.parse("2026-08-27"));
        deadline.markAsDone();

        assertEquals("[D][X] Meet Drake (by: Aug 27 2026)", deadline.toString());
    }

    @Test
    void occursOn_matchingDate_returnsTrue() {
        Deadline deadline = new Deadline("Meet Drake", LocalDate.parse("2026-08-27"));

        assertTrue(deadline.occursOn(LocalDate.parse("2026-08-27")));
    }

    @Test
    void occursOn_earlierDate_returnsFalse() {
        Deadline deadline = new Deadline("Meet Drake", LocalDate.parse("2026-08-27"));

        assertFalse(deadline.occursOn(LocalDate.parse("2026-08-26")));
    }

    @Test
    void occursOn_laterDate_returnsFalse() {
        Deadline deadline = new Deadline("Meet Drake", LocalDate.parse("2026-08-27"));

        assertFalse(deadline.occursOn(LocalDate.parse("2026-08-28")));
    }

    @Test
    void toFileString_newDeadline_savesIsoDateAndIncompleteStatus() {
        Deadline deadline = new Deadline("Meet Drake", LocalDate.parse("2026-08-27"));

        assertEquals("D | 0 | Meet Drake | 2026-08-27", deadline.toFileString());
    }

    @Test
    void toFileString_completedDeadline_savesCompletedStatus() {
        Deadline deadline = new Deadline("Meet Drake", LocalDate.parse("2026-08-27"));
        deadline.markAsDone();

        assertEquals("D | 1 | Meet Drake | 2026-08-27", deadline.toFileString());
    }
}
