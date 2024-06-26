package ru.homework.service;

import ru.homework.DAO.UserDAO;
import ru.homework.DTO.Conference;
import ru.homework.DTO.User;
import ru.homework.exceptions.EntityExistException;

import java.util.List;

public class UserService {

    private UserDAO userDAO = new UserDAO();

    public void add(User user) throws EntityExistException {
        if (exist(user)) throw new EntityExistException("User exist");
        user.setUserId(findLastId()+1);
        userDAO.add(user);
    }

    public List<User> findAll() {
        return userDAO.findAll();
    }

    public User findById(Long id) {
        return userDAO.findById(id);
    }

    public void update(User user, Long id) {
        userDAO.update(user, id);
    }

    public boolean exist(User user) {
        return userDAO.findAll().stream().anyMatch(
                x -> x.getUserId().equals(user.getUserId()) &&
                     x.getUsername().equals(user.getUsername())
        );
    }

    public boolean exist(final Long id) {
        return userDAO.findAll().stream().anyMatch(x -> x.getUserId().equals(id));
    }

    /**
     * Checks if a user with the given username already exists in the list of users.
     *
     * @param username The username to check.
     * @return true if a user with the given username exists, false otherwise.
     */
    public boolean exist(final String username) {
        return userDAO.findAll().stream().anyMatch(x -> x.getUsername().equals(username));
    }

    public Long findLastId() {
        List<User> users = userDAO.findAll();
        int size = users.size();
        if (size == 0) return 0L;
        return users.get(size-1).getUserId();
    }
}
