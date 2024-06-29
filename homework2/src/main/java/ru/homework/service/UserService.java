package ru.homework.service;

import ru.homework.DAO.UserDAO;
import ru.homework.DTO.Conference;
import ru.homework.DTO.User;
import ru.homework.exceptions.EntityExistException;

import java.sql.SQLException;
import java.util.List;

public class UserService {

    private UserDAO userDAO = new UserDAO();

    public void add(User user) throws EntityExistException, SQLException {
        if (exist(user)) throw new EntityExistException("User exist");
        user.setUserId(findLastId()+1);
        userDAO.add(user);
    }

    public List<User> findAll() throws SQLException {
        return userDAO.findAll();
    }

    public User findById(Long id) throws SQLException {
        return userDAO.findById(id);
    }

    public void update(User user, Long id) throws SQLException {
        userDAO.update(user, id);
    }

    public boolean exist(User user) throws SQLException {
        return userDAO.findAll().stream().anyMatch(
                x -> x.getUserId().equals(user.getUserId()) &&
                     x.getUsername().equals(user.getUsername())
        );
    }

    public boolean exist(final Long id) throws SQLException {
        return userDAO.findAll().stream().anyMatch(x -> x.getUserId().equals(id));
    }

    /**
     * Checks if a user with the given username already exists in the list of users.
     *
     * @param username The username to check.
     * @return true if a user with the given username exists, false otherwise.
     */
    public boolean exist(final String username) throws SQLException {
        return userDAO.findAll().stream().anyMatch(x -> x.getUsername().equals(username));
    }

    public Long findLastId() throws SQLException {
        List<User> users = userDAO.findAll();
        int size = users.size();
        if (size == 0) return 0L;
        return users.get(size-1).getUserId();
    }
}
