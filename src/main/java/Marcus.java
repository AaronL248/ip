import java.util.Scanner;

public class Marcus {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String INDENT = "     ";

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
            if (command.equals("bye")) {
                System.out.println(DIVIDER);
                System.out.println(INDENT + "Bye. Hope to see you again soon!");
                System.out.println(DIVIDER);
                break;
            } else if (command.equals("list")) {
                System.out.println(DIVIDER);
                System.out.print(arrayToString(tasks));
                System.out.println(DIVIDER);
            } else if (command.startsWith("mark ")) {
                System.out.println(DIVIDER);
                try {
                    int taskNumber = Integer.parseInt(command.substring(5));
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
            } else if (command.startsWith("unmark ")) {
                System.out.println(DIVIDER);
                try {
                    int taskNumber = Integer.parseInt(command.substring(7));
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
            } else {
                Task newTask = createTask(command);
                System.out.println(DIVIDER);
                if (newTask == null) {
                    System.out.println(INDENT + "Please use todo, deadline, or event to add a task.");
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
     * @return the new task, or {@code null} when the command is invalid
     */
    private static Task createTask(String command) {
        if (command.startsWith("todo ")) {
            return new Todo(command.substring(5));
        }

        if (command.startsWith("deadline ")) {
            String[] parts = command.substring(9).split(" /by ", 2);
            if (parts.length == 2 && !parts[0].isBlank() && !parts[1].isBlank()) {
                return new Deadline(parts[0], parts[1]);
            }
        }

        if (command.startsWith("event ")) {
            String[] descriptionAndFrom = command.substring(6).split(" /from ", 2);
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
