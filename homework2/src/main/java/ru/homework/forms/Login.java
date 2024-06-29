package ru.homework.forms;

import ru.homework.DTO.User;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * The Login class provides functionality for authenticating a user from a list of users.
 * It prompts the user to enter their login credentials and verifies them against the provided list.
 */
public class Login {

    /**
     * Prompts the user to enter their login and password, then searches for a matching user in the provided list.
     * If a user with the given login and password is found, an Optional containing the user is returned.
     * If no matching user is found, an empty Optional is returned.
     *
     * @param users The list of users to search through for authentication.
     * @return An Optional containing the authenticated User if found, otherwise an empty Optional.
     */
    public static Optional<User> loginUser(final List<User> users) {

        Scanner scanner = new Scanner(System.in);

        String login;
        String password;

        System.out.print("Enter LOGIN: ");
        login = scanner.nextLine().trim();

        System.out.print("Enter PASSWORD: ");
        password = scanner.nextLine().trim();

        Optional<User> foundUser = users.stream()
                .filter(x ->
                        x.getUsername().equals(login) && x.getPassword().equals(password)
                )
                .findFirst();


        return foundUser;
    }
}
