package marcus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * Tests the display and persistence behavior of {@link Todo}.
 */
class TodoTest {
    @Test
    void toString_newTodo_showsTodoTypeAndIncompleteStatus() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void markAsDone_incompleteTodo_showsCompletedStatus() {
        Todo todo = new Todo("read book");

        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    void unmarkAsDone_completedTodo_showsIncompleteStatus() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        todo.unmarkAsDone();

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void occursOn_anyDate_returnsFalse() {
        Todo todo = new Todo("read book");

        assertFalse(todo.occursOn(LocalDate.parse("2026-08-27")));
    }

    @Test
    void toFileString_newTodo_savesIncompleteStatus() {
        Todo todo = new Todo("read book");

        assertEquals("T | 0 | read book", todo.toFileString());
    }

    @Test
    void toFileString_completedTodo_savesCompletedStatus() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("T | 1 | read book", todo.toFileString());
    }
}
