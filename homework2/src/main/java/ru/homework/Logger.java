package ru.homework;

/**
 * The Logger class provides methods for logging error and information messages to the console.
 * It includes methods to log messages with or without a custom title.
 */
public class Logger {

    /**
     * Logs an error message to the console.
     * The message is converted to uppercase and surrounded by exclamation marks.
     *
     * @param message The error message to log.
     */
    public static void errorMessage(final String message) {
        System.out.println("ERROR");
        System.out.println("!!! " + message.toUpperCase() + " !!!\n");
    }

    /**
     * Logs an information message to the console.
     * The message is surrounded by plus signs.
     *
     * @param message The information message to log.
     */
    public static void infoMessage(final String message) {
        System.out.println("INFO");
        System.out.println("+++ " + message + " +++");
    }

    /**
     * Logs an error message with a custom title to the console.
     * The message is converted to uppercase and surrounded by exclamation marks.
     *
     * @param title   The title for the error message.
     * @param message The error message to log.
     */
    public static void errorMessage(final String title, final String message) {
        System.out.println(title);
        System.out.println("!!! " + message.toUpperCase() + " !!!\n");
    }

    /**
     * Logs an information message with a custom title to the console.
     * The message is surrounded by plus signs.
     *
     * @param title   The title for the information message.
     * @param message The information message to log.
     */
    public static void infoMessage(final String title, final String message) {
        System.out.println(title);
        System.out.println("+++ " + message + " +++");
    }
}
