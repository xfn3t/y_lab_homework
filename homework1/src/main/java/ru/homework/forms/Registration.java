package ru.homework.forms;

import ru.homework.DTO.User;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static ru.homework.Logger.infoMessage;
import static ru.homework.Logger.errorMessage;

/**
 * The Registration class provides functionality for registering a new user.
 * It includes methods for validating user input and checking for existing users.
 */
public class Registration {

    /**
     * Checks if a given string is null or empty.
     *
     * @param str The string to check.
     * @return true if the string is null or empty, false otherwise.
     */
    private boolean checkStringByNullOrEmpty(final String str) {
        return str == null || str.trim().equals("");
    }

    /**
     * Checks if two passwords are equal.
     *
     * @param password1 The first password.
     * @param password2 The second password.
     * @return true if the passwords are equal, false otherwise.
     */
    private boolean checkPassword(final String password1, final String password2) {
        return password1.equals(password2);
    }

    /**
     * Checks if a user with the given username already exists in the list of users.
     *
     * @param username The username to check.
     * @param users    The list of users.
     * @return true if a user with the given username exists, false otherwise.
     */
    private boolean userExist(final String username, final List<User> users) {
        return users.stream().anyMatch(x -> x.getUsername().equals(username));
    }

    /**
     * Prompts the user to enter a username and password for registration.
     * Validates the input and checks for existing users.
     * If the registration is successful, returns an Optional containing the new User.
     * If the registration fails, returns an empty Optional.
     *
     * @param users The list of existing users to check for duplicates.
     * @return An Optional containing the registered User if successful, otherwise an empty Optional.
     */
    public Optional<User> registrationNewUser(final List<User> users) {

        Scanner scanner = new Scanner(System.in);

        String username;
        String password;
        String repeatPassword = null;

        do {

            System.out.print("Enter USERNAME: ");
            username = scanner.nextLine();

            if (username.trim().equals("") || username.length() < 2)
                errorMessage("username should be not empty or length < 2");
            else if(userExist(username, users)) {
                errorMessage("user exist");
                return Optional.empty();
            }

        } while (checkStringByNullOrEmpty(username) || username.length() < 2);

        do {

            System.out.print("Enter PASSWORD: ");
            password = scanner.nextLine();

            if (password.trim().equals("") || password.length() < 3) {
                errorMessage("password should be not empty or length < 3");
                continue;
            }

            System.out.print("Repeat PASSWORD: ");
            repeatPassword = scanner.nextLine();

            if(!checkPassword(password, repeatPassword)) {
                errorMessage("passwords must match");
            }

        } while ((checkStringByNullOrEmpty(password) || password.length() < 3) || !checkPassword(password, repeatPassword));

        System.out.print("\033[H\033[2J");
        System.out.flush();
        infoMessage("Success registration");
        return Optional.of(new User(username, password));
    }
}
