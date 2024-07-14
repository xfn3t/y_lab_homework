package ru.homework.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.homework.DTO.User;
import ru.homework.exception.EntityExistException;
import ru.homework.service.UserService;

import java.sql.SQLException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUsers(@RequestParam(required = false) Long id,
                                      @RequestParam(required = false) String name) {
        try {
            if (id != null) {
                return ResponseEntity.ok(userService.findById(id));
            } else if (name != null) {
                return ResponseEntity.ok(userService.findByUsername(name));
            } else {
                return ResponseEntity.ok(userService.findAll());
            }
        } catch (NumberFormatException | SQLException e) {
            return ResponseEntity.status(500).body("{\"status\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            if (userService.exist(user.getUsername())) throw new SQLException("User exists");
            userService.add(user);
            return ResponseEntity.status(201).body("{\"status\":\"success\"}");
        } catch (EntityExistException | SQLException e) {
            return ResponseEntity.status(409).body("{\"status\":\"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestParam(required = false) Long id,
                                        @RequestParam(required = false) String name,
                                        HttpSession session) {
        User user = (User) session.getAttribute("user");
        try {
            if (id != null) {
                userService.remove(id);
            } else if (name != null) {
                userService.remove(name);
            }
            return ResponseEntity.ok("{\"status\":\"success delete\"}");
        } catch (NumberFormatException | SQLException e) {
            return ResponseEntity.status(500).body("{\"status\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        try {
            if (user.getUserId() == null) throw new SQLException("Bad ID");
            userService.update(user, user.getUserId());
            return ResponseEntity.status(201).body("{\"status\":\"success\"}");
        } catch (SQLException e) {
            return ResponseEntity.status(409).body("{\"status\":\"" + e.getMessage() + "\"}");
        }
    }
}
