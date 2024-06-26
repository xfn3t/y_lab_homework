package ru.homework.DAO;

import ru.homework.DTO.Conference;
import ru.homework.DTO.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private List<User> users = new ArrayList<>();

    public void add(User user) {
        users.add(user);
    }

    public List<User> findAll() {
        return users;
    }

    public User findById(Long id) {
        return users.stream()
                .filter(x -> x.getUserId().equals(id))
                .findFirst()
                .get();

    }

    public void update(User user, Long id) {
        users.set(
                users.indexOf(findById(id)),
                user
        );
    }

    public void remove(Long id) {
        users.remove(findById(id));
    }

    public void remove(User user) {
        users.remove(user);
    }

    public void removeAll() {
        users.clear();
    }

}
