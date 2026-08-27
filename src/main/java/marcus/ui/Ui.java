package marcus.ui;

import java.util.Scanner;

/**
 * Handles all console input and output for Marcus.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String INDENT = "     ";

    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays Marcus's banner and initial greeting. */
    public void showWelcome() {
        String banner = " __  __    _    ____   ____ _   _ ____ \n"
                + "|  \\/  |  / \\  |  _ \\ / ___| | | / ___|\n"
                + "| |\\/| | / _ \\ | |_) | |   | | | \\___ \\\n"
                + "| |  | |/ ___ \\|  _ <| |___| |_| |___) |\n"
                + "|_|  |_/_/   \\_\\_| \\_\\\\____|\\___/|____/\n";
        System.out.println(banner);
        System.out.println("Hello, I am Marcus the Chatbot!\nWhat can I do for you?");
        System.out.println(DIVIDER);
    }

    /**
     * Reports whether another command is available from the user.
     *
     * @return whether another input line is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next user command.
     *
     * @return the user's command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Displays the goodbye message. */
    public void showGoodbye() {
        showMessage("Bye. Hope to see you again soon!");
    }

    /**
     * Displays a response inside Marcus's divider border.
     *
     * @param message response text; each line is indented for display.
     */
    public void showMessage(String message) {
        System.out.println(DIVIDER);
        for (String line : message.split("\\n", -1)) {
            System.out.println(INDENT + line);
        }
        System.out.println(DIVIDER);
    }

    /**
     * Displays an already formatted task list inside Marcus's divider border.
     *
     * @param taskList formatted task-list text.
     */
    public void showTaskList(String taskList) {
        System.out.println(DIVIDER);
        System.out.print(taskList);
        System.out.println(DIVIDER);
    }

    /**
     * Displays a message without adding dividers, for example while loading saved data.
     *
     * @param message message to show.
     */
    public void showStartupMessage(String message) {
        System.out.println(INDENT + message);
    }
}
