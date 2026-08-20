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

        // Store task descriptions together with their completion marker.
        String[] contents = new String[100];
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
                System.out.print(arrayToString(contents));
                System.out.println(DIVIDER);
            } else if (command.startsWith("mark ")) {
                System.out.println(DIVIDER);
                try {
                    int taskNumber = Integer.parseInt(command.substring(5));
                    if (taskNumber < 1 || taskNumber > currIndex) {
                        System.out.println(INDENT + "That task number does not exist.");
                    } else {
                        contents[taskNumber - 1] = "[X]" + contents[taskNumber - 1].substring(3);
                        System.out.println(INDENT + "Nice! I've marked this task as done:");
                        System.out.println(INDENT + "  " + contents[taskNumber - 1]);
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
                        contents[taskNumber - 1] = "[ ]" + contents[taskNumber - 1].substring(3);
                        System.out.println(INDENT + "OK, I've marked this task as not done yet:");
                        System.out.println(INDENT + "  " + contents[taskNumber - 1]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(INDENT + "Please provide a task number to unmark.");
                }
                System.out.println(DIVIDER);
            } else {
                System.out.println(DIVIDER);
                contents[currIndex] = "[ ] " + command;
                currIndex++;
                System.out.println(INDENT + "I just added \"" + command + "\" to the list");
                System.out.println(DIVIDER);
            }
        }
    }

    /**
     * Formats the current task list with its completion markers.
     *
     * @param array tasks to display
     * @return the formatted task list
     */
    public static String arrayToString(String[] array) {
        StringBuilder res = new StringBuilder(INDENT + "Here are the tasks in your list:\n");
        int currIndex = 0;
        while (currIndex < array.length && array[currIndex] != null) {
            res.append(INDENT).append(currIndex + 1).append(".").append(array[currIndex]).append("\n");
            currIndex++;
        }
        return res.toString();
    }
}
