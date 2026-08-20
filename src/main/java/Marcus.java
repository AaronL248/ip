import java.util.Scanner;

public class Marcus {
    private static final String DIVIDER = "____________________________________________________________";

    public static void main(String[] args) {
        // Banner
        String banner = " __  __    _    ____   ____ _   _ ____ \n"
                + "|  \\/  |  / \\  |  _ \\ / ___| | | / ___|\n"
                + "| |\\/| | / _ \\ | |_) | |   | | | \\___ \\\n"
                + "| |  | |/ ___ \\|  _ <| |___| |_| |___) |\n"
                + "|_|  |_/_/   \\_\\_| \\_\\\\____|\\___/|____/\n";
        System.out.println(banner);

        String space = "     ";

        // Greeting
        String greeting = "Hello, I am Marcus the Chatbot!\n"
                + "What can I do for you?";
        System.out.println(greeting);
        System.out.println(DIVIDER);

        // Add items to list and print contents
        String[] contents = new String[100];
        Integer currIndex = 0;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            if (command.equals("bye")) {
                System.out.println(DIVIDER);
                System.out.println(space + "Bye. Hope to see you again soon!");
                System.out.println(DIVIDER);
                break;
            } else if (command.equals("list")) {
                System.out.print(arrayToString(contents));
                System.out.println(DIVIDER);
            } else {
                System.out.println(DIVIDER);
                contents[currIndex] = command;
                currIndex++;
                System.out.println(space + "I just added \"" + command + "\" to the list");
                System.out.println(DIVIDER);
            }
        }
    }

    public static String arrayToString(String[] array) {
        StringBuilder res = new StringBuilder();
        int currIndex = 0;
        while (currIndex < array.length && array[currIndex] != null) {
            currIndex++;
            res.append(currIndex).append(". " + array[currIndex]).append("\n");
        }
        return res.toString();
    }
}
