import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Marcus {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String INDENT = "     ";
    private static final Path SAVE_FILE = Path.of("data", "results.txt");

    /** Identifies the commands understood by Marcus. */
    private enum CommandType {
        BYE("bye"), LIST("list"), MARK("mark"), UNMARK("unmark"), DELETE("delete"),
        TODO("todo"), DEADLINE("deadline"), EVENT("event"), UNKNOWN("");

        private final String keyword;

        CommandType(String keyword) {
            this.keyword = keyword;
        }

        /**
         * Classifies a user command while preserving commands without required arguments as invalid.
         *
         * @param command user input
         * @return the corresponding command type, or {@code UNKNOWN}
         */
        private static CommandType from(String command) {
            if (command.equals(BYE.keyword)) {
                return BYE;
            }
            if (command.equals(LIST.keyword)) {
                return LIST;
            }
            if (command.equals(MARK.keyword) || command.startsWith(MARK.keyword + " ")) {
                return MARK;
            }
            if (command.equals(UNMARK.keyword) || command.startsWith(UNMARK.keyword + " ")) {
                return UNMARK;
            }
            if (command.equals(DELETE.keyword) || command.startsWith(DELETE.keyword + " ")) {
                return DELETE;
            }
            if (command.equals(TODO.keyword) || command.startsWith(TODO.keyword + " ")) {
                return TODO;
            }
            if (command.equals(DEADLINE.keyword) || command.startsWith(DEADLINE.keyword + " ")) {
                return DEADLINE;
            }
            if (command.equals(EVENT.keyword) || command.startsWith(EVENT.keyword + " ")) {
                return EVENT;
            }
            return UNKNOWN;
        }

        /**
         * Extracts the text after this command's keyword.
         *
         * @param command user input beginning with this command keyword
         * @return command arguments, or an empty string when none are present
         */
        private String getArguments(String command) {
            return command.length() == keyword.length() ? "" : command.substring(keyword.length() + 1);
        }
    }

    public static void main(String[] args) {
        // Banner
        String banner = " __  __    _    ____   ____ _   _ ____ \n"
                + "|  \\/  |  / \\  |  _ \\ / ___| | | / ___|\n"
                + "| |\\/| | / _ \\ | |_) | |   | | | \\___ \\\n"
                + "| |  | |/ ___ \\|  _ <| |___| |_| |___) |\n"
                + "|_|  |_/_/   \\_\\_| \\_\\\\____|\\___/|____/\n";
        System.out.println(banner);

        // Greeting
        String greeting = "Hello, I am Marcus the Chatbot!\n"
                + "What can I do for you?";
        System.out.println(greeting);
        System.out.println(DIVIDER);

        // Store tasks and let each task manage its own completion state.
        Task[] tasks = new Task[100];
        int currIndex = loadTasks(tasks);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            CommandType commandType = CommandType.from(command);
            if (commandType == CommandType.BYE) {
                System.out.println(DIVIDER);
                System.out.println(INDENT + "Bye. Hope to see you again soon!");
                System.out.println(DIVIDER);
                break;
            } else if (commandType == CommandType.LIST) {
                System.out.println(DIVIDER);
                System.out.print(arrayToString(tasks));
                System.out.println(DIVIDER);
            } else if (commandType == CommandType.MARK) {
                System.out.println(DIVIDER);
                try {
                    int taskNumber = Integer.parseInt(commandType.getArguments(command));
                    if (taskNumber < 1 || taskNumber > currIndex) {
                        System.out.println(INDENT + "That task number does not exist.");
                    } else {
                        tasks[taskNumber - 1].markAsDone();
                        saveTasks(tasks, currIndex);
                        System.out.println(INDENT + "Nice! I've marked this task as done:");
                        System.out.println(INDENT + "  " + tasks[taskNumber - 1]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(INDENT + "Please provide a task number to mark.");
                }
                System.out.println(DIVIDER);
            } else if (commandType == CommandType.UNMARK) {
                System.out.println(DIVIDER);
                try {
                    int taskNumber = Integer.parseInt(commandType.getArguments(command));
                    if (taskNumber < 1 || taskNumber > currIndex) {
                        System.out.println(INDENT + "That task number does not exist.");
                    } else {
                        tasks[taskNumber - 1].unmarkAsDone();
                        saveTasks(tasks, currIndex);
                        System.out.println(INDENT + "OK, I've marked this task as not done yet:");
                        System.out.println(INDENT + "  " + tasks[taskNumber - 1]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(INDENT + "Please provide a task number to unmark.");
                }
                System.out.println(DIVIDER);
            } else if (commandType == CommandType.DELETE) {
                System.out.println(DIVIDER);
                try {
                    int taskNumber = Integer.parseInt(commandType.getArguments(command));
                    if (taskNumber < 1 || taskNumber > currIndex) {
                        System.out.println(INDENT + "That task number does not exist.");
                    } else {
                        Task removedTask = tasks[taskNumber - 1];
                        for (int index = taskNumber - 1; index < currIndex - 1; index++) {
                            tasks[index] = tasks[index + 1];
                        }
                        tasks[currIndex - 1] = null;
                        currIndex--;
                        saveTasks(tasks, currIndex);
                        System.out.println(INDENT + "Noted. I've removed this task:");
                        System.out.println(INDENT + "  " + removedTask);
                        System.out.println(INDENT + "Now you have " + currIndex + " tasks in the list.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println(INDENT + "Please provide a task number to delete.");
                }
                System.out.println(DIVIDER);
            } else {
                Task newTask = createTask(command, commandType);
                System.out.println(DIVIDER);
                if (newTask == null) {
                    System.out.println(INDENT + getErrorMessage(command, commandType));
                } else if (currIndex == tasks.length) {
                    System.out.println(INDENT + "Your task list is full.");
                } else {
                    tasks[currIndex] = newTask;
                    currIndex++;
                    saveTasks(tasks, currIndex);
                    System.out.println(INDENT + "Got it. I've added this task:");
                    System.out.println(INDENT + "  " + newTask);
                    System.out.println(INDENT + "Now you have " + currIndex + " tasks in the list.");
                }
                System.out.println(DIVIDER);
            }
        }
    }

    /**
     * Creates a task from a supported task-creation command.
     *
     * @param command user command to interpret
     * @param commandType parsed type of the command
     * @return the new task, or {@code null} when the command is invalid
     */
    private static Task createTask(String command, CommandType commandType) {
        if (commandType == CommandType.TODO) {
            String description = commandType.getArguments(command);
            if (isValidTaskPart(description)) {
                return new Todo(description);
            }
        }

        if (commandType == CommandType.DEADLINE) {
            String[] parts = commandType.getArguments(command).split(" /by ", 2);
            if (parts.length == 2 && isValidTaskPart(parts[0]) && isValidTaskPart(parts[1])) {
                return new Deadline(parts[0], parts[1]);
            }
        }

        if (commandType == CommandType.EVENT) {
            String[] descriptionAndFrom = commandType.getArguments(command).split(" /from ", 2);
            if (descriptionAndFrom.length == 2) {
                String[] fromAndTo = descriptionAndFrom[1].split(" /to ", 2);
                if (fromAndTo.length == 2 && isValidTaskPart(descriptionAndFrom[0])
                        && isValidTaskPart(fromAndTo[0]) && isValidTaskPart(fromAndTo[1])) {
                    return new Event(descriptionAndFrom[0], fromAndTo[0], fromAndTo[1]);
                }
            }
        }

        return null;
    }

    /**
     * Returns a helpful message for an invalid task-creation command.
     *
     * @param command invalid user command
     * @param commandType parsed type of the command
     * @return a command-specific error message
     */
    private static String getErrorMessage(String command, CommandType commandType) {
        if (command.isBlank()) {
            return "Please enter a command.";
        }
        if ((commandType == CommandType.TODO || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT) && command.contains("|")) {
            return "Task details cannot contain the | character.";
        }
        if (commandType == CommandType.TODO) {
            return "Please enter task with todo, eg. todo go for a run";
        }
        if (commandType == CommandType.DEADLINE) {
            return "Please enter task with deadline, eg. deadline return book /by Sunday";
        }
        if (commandType == CommandType.EVENT) {
            return "Please enter task with event, eg. event project meeting /from Mon 2pm /to 4pm";
        }
        return "What do you mean by \"" + command + "\", please enter a valid command";
    }

    /**
     * Checks whether a task field can be displayed and saved safely.
     *
     * @param value task field to validate
     * @return whether the field is non-blank and does not contain the file delimiter
     */
    private static boolean isValidTaskPart(String value) {
        return !value.isBlank() && !value.contains("|");
    }

    /**
     * Saves all current tasks to the project's data file.
     *
     * @param tasks tasks to save
     * @param taskCount number of populated entries in {@code tasks}
     */
    private static boolean saveTasks(Task[] tasks, int taskCount) {
        StringBuilder savedTasks = new StringBuilder();
        for (int index = 0; index < taskCount; index++) {
            savedTasks.append(tasks[index].toFileString()).append(System.lineSeparator());
        }

        try {
            Files.createDirectories(SAVE_FILE.getParent());
            Files.writeString(SAVE_FILE, savedTasks.toString(), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println(INDENT + "Unable to save tasks: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads saved tasks from the project's data file.
     *
     * @param tasks array to populate with saved tasks
     * @return number of loaded tasks
     */
    private static int loadTasks(Task[] tasks) {
        if (!Files.exists(SAVE_FILE)) {
            return 0;
        }
        if (!Files.isRegularFile(SAVE_FILE)) {
            System.out.println(INDENT + "Unable to load tasks: " + SAVE_FILE + " is not a file.");
            return 0;
        }

        int taskCount = 0;
        try {
            List<String> savedLines = Files.readAllLines(SAVE_FILE, StandardCharsets.UTF_8);
            for (String savedLine : savedLines) {
                if (savedLine.isBlank()) {
                    continue;
                }
                if (taskCount == tasks.length) {
                    System.out.println(INDENT + "Only the first " + tasks.length + " saved tasks were loaded.");
                    break;
                }
                Task task = createTaskFromFile(savedLine);
                if (task == null) {
                    System.out.println(INDENT + "Skipped invalid saved task: " + savedLine);
                    continue;
                }
                tasks[taskCount] = task;
                taskCount++;
            }
        } catch (IOException e) {
            System.out.println(INDENT + "Unable to load tasks: " + e.getMessage());
        }
        return taskCount;
    }

    /**
     * Recreates one task from its pipe-delimited saved representation.
     *
     * @param savedLine one line from the data file
     * @return the reconstructed task
     */
    private static Task createTaskFromFile(String savedLine) {
        String[] parts = savedLine.split(" \\| ", -1);
        if (parts.length < 3 || !(parts[1].equals("0") || parts[1].equals("1"))) {
            return null;
        }

        Task task;
        if (parts[0].equals("T") && parts.length == 3 && isValidTaskPart(parts[2])) {
            task = new Todo(parts[2]);
        } else if (parts[0].equals("D") && parts.length == 4
                && isValidTaskPart(parts[2]) && isValidTaskPart(parts[3])) {
            task = new Deadline(parts[2], parts[3]);
        } else if (parts[0].equals("E") && parts.length == 5
                && isValidTaskPart(parts[2]) && isValidTaskPart(parts[3]) && isValidTaskPart(parts[4])) {
            task = new Event(parts[2], parts[3], parts[4]);
        } else {
            return null;
        }
        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Formats the current task list with its completion markers.
     *
     * @param tasks tasks to display
     * @return the formatted task list
     */
    public static String arrayToString(Task[] tasks) {
        StringBuilder res = new StringBuilder(INDENT + "Here are the tasks in your list:\n");
        int currIndex = 0;
        while (currIndex < tasks.length && tasks[currIndex] != null) {
            res.append(INDENT).append(currIndex + 1).append(".").append(tasks[currIndex]).append("\n");
            currIndex++;
        }
        return res.toString();
    }
}
