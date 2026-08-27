package marcus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the display, persistence, and date-matching behavior of {@link Event}.
 */
class EventTest {
    @Test
    void toString_newEvent_showsEventTimesAndIncompleteStatus() {
        Event event = new Event("team meeting", "2026-08-27 14:00", "2026-08-27 16:00");

        assertEquals("[E][ ] team meeting (from: 2026-08-27 14:00 to: 2026-08-27 16:00)",
                event.toString());
    }

    @Test
    void toString_completedEvent_showsCompletedStatus() {
        Event event = new Event("team meeting", "2026-08-27 14:00", "2026-08-27 16:00");
        event.markAsDone();

        assertEquals("[E][X] team meeting (from: 2026-08-27 14:00 to: 2026-08-27 16:00)",
                event.toString());
    }

    @Test
    void occursOn_singleDayEventDate_returnsTrue() {
        Event event = new Event("team meeting", "2026-08-27 14:00", "2026-08-27 16:00");

        assertTrue(event.occursOn(LocalDate.parse("2026-08-27")));
    }

    @Test
    void occursOn_multiDayEventStartAndEndDates_returnsTrue() {
        Event event = new Event("camp", "2026-08-27 09:00", "2026-08-29 17:00");

        assertTrue(event.occursOn(LocalDate.parse("2026-08-27")));
        assertTrue(event.occursOn(LocalDate.parse("2026-08-29")));
    }

    @Test
    void occursOn_dateInsideMultiDayEvent_returnsTrue() {
        Event event = new Event("camp", "2026-08-27 09:00", "2026-08-29 17:00");

        assertTrue(event.occursOn(LocalDate.parse("2026-08-28")));
    }

    @Test
    void occursOn_dateOutsideMultiDayEvent_returnsFalse() {
        Event event = new Event("camp", "2026-08-27 09:00", "2026-08-29 17:00");

        assertFalse(event.occursOn(LocalDate.parse("2026-08-26")));
        assertFalse(event.occursOn(LocalDate.parse("2026-08-30")));
    }

    @Test
    void occursOn_freeTextTimes_returnsFalse() {
        Event event = new Event("team meeting", "Monday 2pm", "4pm");

        assertFalse(event.occursOn(LocalDate.parse("2026-08-27")));
    }

    @Test
    void occursOn_onlyOneIsoTime_returnsFalse() {
        Event event = new Event("team meeting", "2026-08-27 14:00", "later");

        assertFalse(event.occursOn(LocalDate.parse("2026-08-27")));
    }

    @Test
    void toFileString_newEvent_savesTimesAndIncompleteStatus() {
        Event event = new Event("team meeting", "2026-08-27 14:00", "2026-08-27 16:00");

        assertEquals("E | 0 | team meeting | 2026-08-27 14:00 | 2026-08-27 16:00",
                event.toFileString());
    }

    @Test
    void toFileString_completedEvent_savesCompletedStatus() {
        Event event = new Event("team meeting", "2026-08-27 14:00", "2026-08-27 16:00");
        event.markAsDone();

        assertEquals("E | 1 | team meeting | 2026-08-27 14:00 | 2026-08-27 16:00",
                event.toFileString());
    }
}
