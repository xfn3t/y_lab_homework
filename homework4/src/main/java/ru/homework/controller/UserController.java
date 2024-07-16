package ru.homework.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(tags = "User Management", description = "Endpoints for managing users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final User user;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get users", notes = "Returns a list of users. You can filter by 'id' or 'name'.")
    public ResponseEntity<?> getUsers(
            @ApiParam(value = "ID of the user to retrieve")
            @RequestParam(required = false) Long id,
            @ApiParam(value = "Username of the user to retrieve")
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
    @ApiOperation(value = "Add a user", notes = "Add a new user to the system.")
    public ResponseEntity<?> addUser(
            @ApiParam(value = "User object to add", required = true)
            @RequestBody User user) {
        try {
            if (userService.exist(user.getUsername())) {
                throw new EntityExistException("User exists");
            }
            userService.add(user);
            return ResponseEntity.status(201).body("{\"status\":\"success\"}");
        } catch (EntityExistException | SQLException e) {
            return ResponseEntity.status(409).body("{\"status\":\"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping
    @ApiOperation(value = "Delete a user", notes = "Delete a user by 'id' or 'name' if logged in user matches.")
    public ResponseEntity<?> deleteUser(
            @ApiParam(value = "ID of the user to delete")
            @RequestParam(required = false) Long id,
            @ApiParam(value = "Username of the user to delete")
            @RequestParam(required = false) String name) {
        try {
            if (user == null) {
                throw new SQLException("Need login");
            }
            if (id != null) {
                if (user.getUserId().equals(id)) {
                    userService.remove(id);
                } else {
                    throw new SQLException("Permission denied");
                }
            } else if (name != null) {
                if (user.getUsername().equals(name)) {
                    userService.remove(name);
                } else {
                    throw new SQLException("Permission denied");
                }
            }
            return ResponseEntity.ok("{\"status\":\"success delete\"}");
        } catch (NumberFormatException | SQLException e) {
            return ResponseEntity.status(500).body("{\"status\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping
    @ApiOperation(value = "Update a user", notes = "Update the logged-in user information.")
    public ResponseEntity<?> updateUser(
            @ApiParam(value = "Updated user object", required = true)
            @RequestBody User u) {
        try {
            if (user == null) {
                throw new SQLException("Need login");
            }
            userService.update(u, user.getUserId());
            return ResponseEntity.status(201).body("{\"status\":\"success\"}");
        } catch (SQLException e) {
            return ResponseEntity.status(409).body("{\"status\":\"" + e.getMessage() + "\"}");
        }
    }
}
