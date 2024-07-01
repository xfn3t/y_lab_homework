package ru.homework.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * The User class represents a user with a username, password, a list of conferences,
 * and an associated workspace. It provides constructors, getters, setters, and a
 * toString method to display user details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {


    private Long userId;
    /**
     * The username of the user.
     */
    private String username;

    /**
     * The password of the user.
     */
    private String password;

    /**
     * The list of conferences associated with the user.
     */
    private List<Conference> userConferences = new ArrayList<>();

    /**
     * The workspace associated with the user.
     */
    private Workspace userWorkspace;

    /**
     * Constructs a User with the specified username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(long userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    /**
     * Returns a string representation of the user with the username, list of conferences, and workspace details.
     *
     * @return A string representation of the user.
     */
    @Override
    public String toString() {
        return new StringBuilder("{")
                .append("ID: ").append(userId).append(", ")
                .append("Username: ").append(username).append(", ")
                .append("Conferences: ").append(userConferences).append(", ")
                .append("Workspace: ").append(userWorkspace)
                .append("}")
                .toString();
    }
}
