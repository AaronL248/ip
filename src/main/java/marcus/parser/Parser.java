package marcus.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import marcus.task.Deadline;
import marcus.task.Event;
import marcus.task.Task;
import marcus.task.Todo;

/**
 * Interprets user commands and creates tasks from valid task-creation commands.
 */
public class Parser {
    /** Identifies the commands understood by Marcus. */
    public enum CommandType {
        /** Exits Marcus. */
        BYE("bye"),
        /** Lists all tasks. */
        LIST("list"),
        /** Finds dated tasks. */
        FIND("find"),
        /** Marks a task as completed. */
        MARK("mark"),
        /** Marks a task as incomplete. */
        UNMARK("unmark"),
        /** Deletes a task. */
        DELETE("delete"),
        /** Creates a to-do. */
        TODO("todo"),
        /** Creates a deadline. */
        DEADLINE("deadline"),
        /** Creates an event. */
        EVENT("event"),
        /** Represents unrecognized input. */
        UNKNOWN("");

        private final String keyword;

        /**
         * Creates a command type associated with its command-line keyword.
         *
         * @param keyword text that identifies the command.
         */
        CommandType(String keyword) {
            this.keyword = keyword;
        }
    }

    /** Creates a parser for interpreting Marcus commands. */
    public Parser() {
    }

    /**
     * Classifies a user command while preserving commands without required arguments as invalid.
     *
     * @param command user input.
     * @return the corresponding command type, or {@code UNKNOWN}.
     */
    public CommandType getCommandType(String command) {
        for (CommandType commandType : CommandType.values()) {
            if (commandType == CommandType.UNKNOWN) {
                continue;
            }
            if (command.equals(commandType.keyword) || command.startsWith(commandType.keyword + " ")) {
                return commandType;
            }
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Extracts the text after a command's keyword.
     *
     * @param command user input beginning with the command keyword.
     * @param commandType type of the command.
     * @return command arguments, or an empty string when none are present.
     */
    public String getArguments(String command, CommandType commandType) {
        return command.length() == commandType.keyword.length()
                ? "" : command.substring(commandType.keyword.length() + 1);
    }

    /**
     * Creates a task from a supported task-creation command.
     *
     * @param command user command to interpret.
     * @param commandType parsed type of the command.
     * @return the new task, or {@code null} when the command is invalid.
     */
    public Task createTask(String command, CommandType commandType) {
        String arguments = getArguments(command, commandType);
        if (commandType == CommandType.TODO && isValidTaskPart(arguments)) {
            return new Todo(arguments);
        }

        if (commandType == CommandType.DEADLINE) {
            String[] parts = arguments.split(" /by ", 2);
            if (parts.length == 2 && isValidTaskPart(parts[0]) && isValidTaskPart(parts[1])) {
                try {
                    return new Deadline(parts[0], LocalDate.parse(parts[1]));
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        }

        if (commandType == CommandType.EVENT) {
            String[] descriptionAndFrom = arguments.split(" /from ", 2);
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
     * Returns a helpful message for an invalid command.
     *
     * @param command invalid user command.
     * @param commandType parsed type of the command.
     * @return a command-specific error message.
     */
    public String getErrorMessage(String command, CommandType commandType) {
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
            return "Please enter a deadline date in yyyy-mm-dd format, eg. deadline return book /by 2019-10-15";
        }
        if (commandType == CommandType.EVENT) {
            return "Please enter task with event, eg. event project meeting /from Mon 2pm /to 4pm";
        }
        return "What do you mean by \"" + command + "\", please enter a valid command";
    }

    /**
     * Checks whether a task field can be displayed and saved safely.
     *
     * @param value task field to validate.
     * @return whether the field is non-blank and does not contain the file delimiter.
     */
    public static boolean isValidTaskPart(String value) {
        return !value.isBlank() && !value.contains("|");
    }
}
