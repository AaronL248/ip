package marcus;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import marcus.parser.Parser;
import marcus.storage.Storage;
import marcus.task.Task;
import marcus.task.TaskList;
import marcus.ui.Ui;

/**
 * Starts Marcus and coordinates its user interface, command parser, task list, and storage.
 */
public class Marcus {
    private final Storage storage;
    private final Ui ui;
    private final Parser parser;
    private TaskList tasks;

    /**
     * Creates Marcus with a task data file at the supplied path.
     *
     * @param filePath path of the task data file.
     */
    public Marcus(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();
        tasks = new TaskList();
    }

    /** Runs Marcus's command loop. */
    public void run() {
        ui.showWelcome();
        tasks = new TaskList(storage.load(ui));
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            Parser.CommandType commandType = parser.getCommandType(command);
            if (commandType == Parser.CommandType.BYE) {
                ui.showGoodbye();
                break;
            } else if (commandType == Parser.CommandType.LIST) {
                ui.showTaskList(tasks.toDisplayString());
            } else if (commandType == Parser.CommandType.FIND) {
                try {
                    LocalDate date = LocalDate.parse(parser.getArguments(command, commandType));
                    ui.showTaskList(tasks.tasksOnDateToString(date));
                } catch (DateTimeParseException e) {
                    ui.showMessage("Please provide a date in yyyy-mm-dd format, eg. find 2019-10-15");
                }
            } else if (commandType == Parser.CommandType.MARK) {
                try {
                    int taskNumber = Integer.parseInt(parser.getArguments(command, commandType));
                    Task task = tasks.get(taskNumber);
                    if (task == null) {
                        ui.showMessage("That task number does not exist.");
                    } else {
                        task.markAsDone();
                        storage.save(tasks, ui);
                        ui.showMessage("Nice! I've marked this task as done:\n  " + task);
                    }
                } catch (NumberFormatException e) {
                    ui.showMessage("Please provide a task number to mark.");
                }
            } else if (commandType == Parser.CommandType.UNMARK) {
                try {
                    int taskNumber = Integer.parseInt(parser.getArguments(command, commandType));
                    Task task = tasks.get(taskNumber);
                    if (task == null) {
                        ui.showMessage("That task number does not exist.");
                    } else {
                        task.unmarkAsDone();
                        storage.save(tasks, ui);
                        ui.showMessage("OK, I've marked this task as not done yet:\n  " + task);
                    }
                } catch (NumberFormatException e) {
                    ui.showMessage("Please provide a task number to unmark.");
                }
            } else if (commandType == Parser.CommandType.DELETE) {
                try {
                    int taskNumber = Integer.parseInt(parser.getArguments(command, commandType));
                    Task removedTask = tasks.remove(taskNumber);
                    if (removedTask == null) {
                        ui.showMessage("That task number does not exist.");
                    } else {
                        storage.save(tasks, ui);
                        ui.showMessage("Noted. I've removed this task:\n  " + removedTask
                                + "\nNow you have " + tasks.size() + " tasks in the list.");
                    }
                } catch (NumberFormatException e) {
                    ui.showMessage("Please provide a task number to delete.");
                }
            } else {
                Task newTask = parser.createTask(command, commandType);
                if (newTask == null) {
                    ui.showMessage(parser.getErrorMessage(command, commandType));
                } else if (tasks.isFull()) {
                    ui.showMessage("Your task list is full.");
                } else {
                    tasks.add(newTask);
                    storage.save(tasks, ui);
                    ui.showMessage("Got it. I've added this task:\n  " + newTask
                            + "\nNow you have " + tasks.size() + " tasks in the list.");
                }
            }
        }
    }

    /**
     * Starts Marcus using its project-relative data file.
     *
     * @param args command-line arguments, currently unused.
     */
    public static void main(String[] args) {
        new Marcus("data/results.txt").run();
    }
}
