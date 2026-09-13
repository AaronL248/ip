package marcus.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests task-list integrity checks. */
class TaskListTest {
    /** Verifies that identical task details are recognised as duplicates. */
    @Test
    void containsEquivalentTask_duplicateDetails_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertTrue(tasks.containsEquivalentTask(new Todo("read book")));
    }

    /** Verifies that different task details are not rejected as duplicates. */
    @Test
    void containsEquivalentTask_differentDetails_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertFalse(tasks.containsEquivalentTask(new Todo("write notes")));
    }
}
