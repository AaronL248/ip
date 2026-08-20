import java.util.Scanner;

public class Marcus {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String INDENT = "     ";

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
            if (command.startsWith(MARK.keyword + " ")) {
                return MARK;
            }
            if (command.startsWith(UNMARK.keyword + " ")) {
                return UNMARK;
            }
            if (command.startsWith(DELETE.keyword + " ")) {
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
        int currIndex = 0;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
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
                } else {
                    tasks[currIndex] = newTask;
                    currIndex++;
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
            if (!description.isBlank()) {
                return new Todo(description);
            }
        }

        if (commandType == CommandType.DEADLINE) {
            String[] parts = commandType.getArguments(command).split(" /by ", 2);
            if (parts.length == 2 && !parts[0].isBlank() && !parts[1].isBlank()) {
                return new Deadline(parts[0], parts[1]);
            }
        }

        if (commandType == CommandType.EVENT) {
            String[] descriptionAndFrom = commandType.getArguments(command).split(" /from ", 2);
            if (descriptionAndFrom.length == 2) {
                String[] fromAndTo = descriptionAndFrom[1].split(" /to ", 2);
                if (fromAndTo.length == 2 && !descriptionAndFrom[0].isBlank()
                        && !fromAndTo[0].isBlank() && !fromAndTo[1].isBlank()) {
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
