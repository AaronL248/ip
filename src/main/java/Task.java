import java.time.LocalDate;

/**
 * Represents one task and whether it has been completed.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon used when displaying the task.
     *
     * @return {@code X} for a completed task, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not completed. */
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Reports whether this task occurs on a particular date.
     * Tasks without a date, such as to-dos, do not occur on any date.
     *
     * @param date date to check
     * @return {@code false} for a task without a date
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns this task in the format used for persistence.
     *
     * @return task type, completion state, and description separated by pipes
     */
    public String toFileString() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns this task in its display format.
     *
     * @return status icon followed by the task description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
