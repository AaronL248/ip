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

        // Greeting
        String greeting = "Hello, I am Marcus the Chatbot!\n"
                + "What can I do for you?";
        System.out.println(greeting);
        System.out.println(DIVIDER);

        // Echo user text except bye
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            if (command.equals("bye")) {
                System.out.println(DIVIDER);
                System.out.println("        Bye. Hope to see you again soon!");
                System.out.println(DIVIDER);
                break;
            }

            System.out.println(DIVIDER);
            System.out.println("        " + command);
            System.out.println(DIVIDER);
        }
    }
}
