package ru.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.homework.DTO.User;
import ru.homework.generate.TokenGenerator;
import ru.homework.service.UserService;

import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final User user;
    private final UserService userService;

    public boolean authenticate(String username, String password) throws SQLException {
        User u = userService.findByUsername(username);
        if (u.getPassword().equals(password)) {
            user.setUserId(u.getUserId());
            user.setUsername(u.getUsername());
            user.setPassword(u.getPassword());
            user.setUserConferences(u.getUserConferences());
            user.setUserWorkspace(u.getUserWorkspace());
            return true;
        }
        return false;
    }

//    public User getCurrentUser() {
//        return user;
//    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody User u) throws SQLException {
        if (authenticate(u.getUsername(), u.getPassword())) {
            return ResponseEntity.ok().body("Login success");
        } else {
            return ResponseEntity.badRequest().body("Bad username or password");
        }
    }
}
