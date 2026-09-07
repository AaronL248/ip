package marcus.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Stores Marcus's tasks and provides task-list operations.
 */
public class TaskList {
    /** Maximum number of tasks held by the current in-memory task list. */
    public static final int CAPACITY = 100;
    private static final String INDENT = "     ";
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    private final Task[] tasks;
    private int size;

    /** Creates an empty task list. */
    public TaskList() {
        this(new Task[CAPACITY]);
    }

    /**
     * Creates a task list from an array whose populated entries appear first.
     *
     * @param tasks tasks to store.
     */
    public TaskList(Task[] tasks) {
        assert tasks != null : "Task storage must not be null";
        this.tasks = tasks;
        while (size < tasks.length && tasks[size] != null) {
            size++;
        }
        for (int index = size; index < tasks.length; index++) {
            assert tasks[index] == null : "Task storage must be packed from the beginning";
        }
    }

    /**
     * Reports whether this task list has reached its capacity.
     *
     * @return whether this task list is full.
     */
    public boolean isFull() {
        return size == tasks.length;
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return number of tasks in this list.
     */
    public int size() {
        return size;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        assert !isFull() : "Cannot add a task to a full task list";
        assert task != null : "A task list must not store a null task";
        tasks[size] = task;
        size++;
    }

    /**
     * Returns the task at a one-based user-facing task number.
     *
     * @param taskNumber one-based task number.
     * @return the matching task, or {@code null} when the number is invalid.
     */
    public Task get(int taskNumber) {
        if (taskNumber < 1 || taskNumber > size) {
            return null;
        }
        return tasks[taskNumber - 1];
    }

    /**
     * Removes the task at a one-based user-facing task number.
     *
     * @param taskNumber one-based task number.
     * @return removed task, or {@code null} when the number is invalid.
     */
    public Task remove(int taskNumber) {
        Task removedTask = get(taskNumber);
        if (removedTask == null) {
            return null;
        }
        for (int index = taskNumber - 1; index < size - 1; index++) {
            tasks[index] = tasks[index + 1];
        }
        tasks[size - 1] = null;
        size--;
        return removedTask;
    }

    /**
     * Formats the current task list with its completion markers.
     *
     * @return formatted task-list text.
     */
    public String toDisplayString() {
        StringBuilder result = new StringBuilder(INDENT + "Here are the tasks in your list:\n");
        for (int index = 0; index < size; index++) {
            result.append(INDENT).append(index + 1).append(".").append(tasks[index]).append("\n");
        }
        return result.toString();
    }

    /**
     * Formats tasks that occur on a specified date, retaining their task-list numbers.
     *
     * @param date date to match.
     * @return formatted matching tasks or a message when none match.
     */
    public String tasksOnDateToString(LocalDate date) {
        StringBuilder result = new StringBuilder(INDENT + "Here are the tasks occurring on "
                + date.format(DISPLAY_DATE_FORMAT) + ":\n");
        boolean hasMatches = false;
        for (int index = 0; index < size; index++) {
            if (tasks[index].occursOn(date)) {
                result.append(INDENT).append(index + 1).append(".").append(tasks[index]).append("\n");
                hasMatches = true;
            }
        }
        if (!hasMatches) {
            result.append(INDENT).append("There are no tasks occurring on ")
                    .append(date.format(DISPLAY_DATE_FORMAT)).append(".\n");
        }
        return result.toString();
    }

    /**
     * Formats tasks whose descriptions contain a keyword, retaining their task-list numbers.
     *
     * @param keyword keyword to match.
     * @return formatted matching tasks or a message when none match.
     */
    public String tasksMatchingKeywordToString(String keyword) {
        StringBuilder result = new StringBuilder(INDENT + "Here are the matching tasks in your list:\n");
        boolean hasMatches = false;
        for (int index = 0; index < size; index++) {
            if (tasks[index].matchesKeyword(keyword)) {
                result.append(INDENT).append(index + 1).append(".").append(tasks[index]).append("\n");
                hasMatches = true;
            }
        }
        if (!hasMatches) {
            result.append(INDENT).append("There are no matching tasks in your list.\n");
        }
        return result.toString();
    }

    /**
     * Returns all tasks in file format, one task per line.
     *
     * @return serialized task list.
     */
    public String toFileString() {
        StringBuilder savedTasks = new StringBuilder();
        for (int index = 0; index < size; index++) {
            savedTasks.append(tasks[index].toFileString()).append(System.lineSeparator());
        }
        return savedTasks.toString();
    }
}
