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

    /** Loads saved tasks before a user interface starts accepting commands. */
    public void startSession() {
        tasks = new TaskList(storage.load(ui));
    }

    /** Runs Marcus's command loop. */
    public void run() {
        ui.showWelcome();
        startSession();
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            if (!processCommand(command, ui)) {
                break;
            }
        }
    }

    /**
     * Processes one command and sends its response to the supplied interface.
     *
     * @param command user command.
     * @param responseHandler interface that displays the response.
     * @return whether the session should continue accepting commands.
     */
    public boolean processCommand(String command, ResponseHandler responseHandler) {
        Parser.CommandType commandType = parser.getCommandType(command);
        if (commandType == Parser.CommandType.BYE) {
            responseHandler.showGoodbye();
            return false;
        }
        if (commandType == Parser.CommandType.LIST) {
            responseHandler.showTaskList(tasks.toDisplayString());
        } else if (commandType == Parser.CommandType.FIND) {
            processFind(command, commandType, responseHandler);
        } else if (commandType == Parser.CommandType.MARK) {
            processMark(command, commandType, responseHandler);
        } else if (commandType == Parser.CommandType.UNMARK) {
            processUnmark(command, commandType, responseHandler);
        } else if (commandType == Parser.CommandType.DELETE) {
            processDelete(command, commandType, responseHandler);
        } else {
            processCreate(command, commandType, responseHandler);
        }
        return true;
    }

    private void processFind(String command, Parser.CommandType commandType, ResponseHandler responseHandler) {
        String searchTerm = parser.getArguments(command, commandType);
        try {
            LocalDate date = LocalDate.parse(searchTerm);
            responseHandler.showTaskList(tasks.tasksOnDateToString(date));
        } catch (DateTimeParseException e) {
            if (searchTerm.isBlank() || searchTerm.matches("\\d{4}-\\d{2}-\\d{2}")) {
                responseHandler.showMessage("Please provide a keyword or a date in yyyy-mm-dd format, "
                        + "eg. find book or find 2019-10-15");
            } else {
                responseHandler.showTaskList(tasks.tasksMatchingKeywordToString(searchTerm));
            }
        }
    }

    private void processMark(String command, Parser.CommandType commandType, ResponseHandler responseHandler) {
        try {
            int taskNumber = Integer.parseInt(parser.getArguments(command, commandType));
            Task task = tasks.get(taskNumber);
            if (task == null) {
                responseHandler.showMessage("That task number does not exist.");
            } else {
                task.markAsDone();
                storage.save(tasks, ui);
                responseHandler.showMessage("Nice! I've marked this task as done:\n  " + task);
            }
        } catch (NumberFormatException e) {
            responseHandler.showMessage("Please provide a task number to mark.");
        }
    }

    private void processUnmark(String command, Parser.CommandType commandType, ResponseHandler responseHandler) {
        try {
            int taskNumber = Integer.parseInt(parser.getArguments(command, commandType));
            Task task = tasks.get(taskNumber);
            if (task == null) {
                responseHandler.showMessage("That task number does not exist.");
            } else {
                task.unmarkAsDone();
                storage.save(tasks, ui);
                responseHandler.showMessage("OK, I've marked this task as not done yet:\n  " + task);
            }
        } catch (NumberFormatException e) {
            responseHandler.showMessage("Please provide a task number to unmark.");
        }
    }

    private void processDelete(String command, Parser.CommandType commandType, ResponseHandler responseHandler) {
        try {
            int taskNumber = Integer.parseInt(parser.getArguments(command, commandType));
            Task removedTask = tasks.remove(taskNumber);
            if (removedTask == null) {
                responseHandler.showMessage("That task number does not exist.");
            } else {
                storage.save(tasks, ui);
                responseHandler.showMessage("Noted. I've removed this task:\n  " + removedTask
                        + "\nNow you have " + tasks.size() + " tasks in the list.");
            }
        } catch (NumberFormatException e) {
            responseHandler.showMessage("Please provide a task number to delete.");
        }
    }

    private void processCreate(String command, Parser.CommandType commandType, ResponseHandler responseHandler) {
        Task newTask = parser.createTask(command, commandType);
        if (newTask == null) {
            responseHandler.showMessage(parser.getErrorMessage(command, commandType));
        } else if (tasks.isFull()) {
            responseHandler.showMessage("Your task list is full.");
        } else {
            tasks.add(newTask);
            storage.save(tasks, ui);
            responseHandler.showMessage("Got it. I've added this task:\n  " + newTask
                    + "\nNow you have " + tasks.size() + " tasks in the list.");
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
