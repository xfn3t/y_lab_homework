package ru.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.homework.DTO.User;
import ru.homework.DTO.Workspace;
import ru.homework.exception.EntityExistException;
import ru.homework.service.UserService;
import ru.homework.service.WorkspaceService;

import java.sql.SQLException;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final UserService userService;
    private final User user;


    @GetMapping
    public ResponseEntity<?> getWorkspaces(@RequestParam(required = false) Long id,
                                           @RequestParam(required = false) String title) {
        try {
            if (user == null) throw new SQLException("Need login");
            if (id != null) {
                return ResponseEntity.ok(workspaceService.findById(id));
            } else if (title != null) {
                return ResponseEntity.ok(workspaceService.findByTitle(title));
            } else {
                return ResponseEntity.ok(workspaceService.findAll());
            }
        } catch (NumberFormatException | SQLException e) {
            return ResponseEntity.status(500).body("{\"status\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping
    public ResponseEntity<?> addWorkspace(@RequestBody Workspace workspace) {
        try {

            if (user == null) throw new SQLException("Need login");
            if (workspaceService.exist(workspace)) throw new SQLException("Workspace exists");

            workspaceService.add(workspace);
            userService.addWorkspace(workspace,user.getUserId());

            return ResponseEntity.status(201).body("{\"status\":\"success\"}");
        } catch (EntityExistException | SQLException e) {
            return ResponseEntity.status(409).body("{\"status\":\"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteWorkspace(@RequestParam(required = false) Long id) {

        try {
            if (user == null) throw new SQLException("Need login");
            if (id != null && user.getUserWorkspace().getWorkspaceId().equals(id)) {
                workspaceService.remove(id);
            } else {
                throw new SQLException("No permission");
            }
            return ResponseEntity.ok("{\"status\":\"success delete\"}");
        } catch (NumberFormatException | SQLException e) {
            return ResponseEntity.status(500).body("{\"status\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping
    public ResponseEntity<?> updateWorkspace(@RequestBody Workspace workspace) {
        try {
            if (user == null) throw new SQLException("Need login");
            if (workspace.getWorkspaceId() == null) throw new SQLException("ID not found");

            workspaceService.update(workspace, workspace.getWorkspaceId());
            return ResponseEntity.status(201).body("{\"status\":\"success\"}");
        } catch (SQLException e) {
            return ResponseEntity.status(409).body("{\"status\":\"" + e.getMessage() + "\"}");
        }
    }
}
