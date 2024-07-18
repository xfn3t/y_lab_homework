package ru.homework.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(tags = "Workspace Management", description = "Endpoints for managing workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final UserService userService;
    private final User user;

    @GetMapping
    @ApiOperation(value = "Get workspaces", notes = "Returns a list of workspaces. You can filter by 'id' or 'title'.")
    public ResponseEntity<?> getWorkspaces(
            @ApiParam(value = "ID of the workspace to retrieve")
            @RequestParam(required = false) Long id,
            @ApiParam(value = "Title of the workspace to retrieve")
            @RequestParam(required = false) String title) {
        try {
            if (user == null) {
                throw new SQLException("Need login");
            }
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
    @ApiOperation(value = "Add a workspace", notes = "Add a new workspace to the system.")
    public ResponseEntity<?> addWorkspace(
            @ApiParam(value = "Workspace object to add", required = true)
            @RequestBody Workspace workspace) {
        try {
            if (user == null) {
                throw new SQLException("Need login");
            }
            if (workspaceService.exist(workspace)) {
                throw new EntityExistException("Workspace exists");
            }

            workspaceService.add(workspace);
            userService.addWorkspace(workspace, user.getUserId());

            return ResponseEntity.status(201).body("{\"status\":\"success\"}");
        } catch (EntityExistException | SQLException e) {
            return ResponseEntity.status(409).body("{\"status\":\"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping
    @ApiOperation(value = "Delete a workspace", notes = "Delete a workspace by its ID if logged-in user matches.")
    public ResponseEntity<?> deleteWorkspace(
            @ApiParam(value = "ID of the workspace to delete")
            @RequestParam(required = false) Long id) {
        try {
            if (user == null) {
                throw new SQLException("Need login");
            }
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
    @ApiOperation(value = "Update a workspace", notes = "Update an existing workspace.")
    public ResponseEntity<?> updateWorkspace(
            @ApiParam(value = "Updated workspace object", required = true)
            @RequestBody Workspace workspace) {
        try {
            if (user == null) {
                throw new SQLException("Need login");
            }
            if (workspace.getWorkspaceId() == null) {
                throw new SQLException("ID not found");
            }

            workspaceService.update(workspace, workspace.getWorkspaceId());
            return ResponseEntity.status(201).body("{\"status\":\"success\"}");
        } catch (SQLException e) {
            return ResponseEntity.status(409).body("{\"status\":\"" + e.getMessage() + "\"}");
        }
    }

}
