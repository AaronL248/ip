package marcus.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import marcus.parser.Parser;
import marcus.task.Deadline;
import marcus.task.Event;
import marcus.task.Task;
import marcus.task.TaskList;
import marcus.task.Todo;
import marcus.ui.Ui;

/**
 * Saves tasks to and loads tasks from Marcus's data file.
 */
public class Storage {
    private final Path saveFile;

    /**
     * Creates storage using a path relative to the program's working directory.
     *
     * @param filePath path of the task data file.
     */
    public Storage(String filePath) {
        saveFile = Path.of(filePath);
    }

    /**
     * Saves all current tasks to the data file.
     *
     * @param tasks tasks to save.
     * @param ui user interface used to display save errors.
     * @return whether the save completed successfully.
     */
    public boolean save(TaskList tasks, Ui ui) {
        try {
            Path parent = saveFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(saveFile, tasks.toFileString(), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            ui.showStartupMessage("Unable to save tasks: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads saved tasks from the data file.
     *
     * @param ui user interface used to display loading messages.
     * @return array containing the loaded tasks.
     */
    public Task[] load(Ui ui) {
        Task[] tasks = new Task[TaskList.CAPACITY];
        if (!Files.exists(saveFile)) {
            return tasks;
        }
        if (!Files.isRegularFile(saveFile)) {
            ui.showStartupMessage("Unable to load tasks: " + saveFile + " is not a file.");
            return tasks;
        }

        int taskCount = 0;
        try {
            List<String> savedLines = Files.readAllLines(saveFile, StandardCharsets.UTF_8);
            for (String savedLine : savedLines) {
                if (savedLine.isBlank()) {
                    continue;
                }
                if (taskCount == tasks.length) {
                    ui.showStartupMessage("Only the first " + tasks.length + " saved tasks were loaded.");
                    break;
                }
                Task task = createTaskFromFile(savedLine);
                if (task == null) {
                    ui.showStartupMessage("Skipped invalid saved task: " + savedLine);
                    continue;
                }
                tasks[taskCount] = task;
                taskCount++;
            }
        } catch (IOException e) {
            ui.showStartupMessage("Unable to load tasks: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Recreates one task from its pipe-delimited saved representation.
     *
     * @param savedLine one line from the data file.
     * @return reconstructed task, or {@code null} when the record is invalid.
     */
    private Task createTaskFromFile(String savedLine) {
        String[] parts = savedLine.split(" \\| ", -1);
        if (parts.length < 3 || !(parts[1].equals("0") || parts[1].equals("1"))) {
            return null;
        }

        Task task;
        if (parts[0].equals("T") && parts.length == 3 && Parser.isValidTaskPart(parts[2])) {
            task = new Todo(parts[2]);
        } else if (parts[0].equals("D") && parts.length == 4
                && Parser.isValidTaskPart(parts[2]) && Parser.isValidTaskPart(parts[3])) {
            try {
                task = new Deadline(parts[2], LocalDate.parse(parts[3]));
            } catch (DateTimeParseException e) {
                return null;
            }
        } else if (parts[0].equals("E") && parts.length == 5
                && Parser.isValidTaskPart(parts[2]) && Parser.isValidTaskPart(parts[3])
                && Parser.isValidTaskPart(parts[4])) {
            task = new Event(parts[2], parts[3], parts[4]);
        } else {
            return null;
        }
        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }
}
