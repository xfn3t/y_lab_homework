package ru.homework.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.homework.DTO.Conference;
import ru.homework.DTO.User;
import ru.homework.service.ConferenceService;
import ru.homework.service.UserService;

import java.sql.SQLException;

@RestController
@RequestMapping("/conference")
@Api(tags = "Conference Management", description = "Endpoints for managing conferences")
@RequiredArgsConstructor
public class ConferenceController {

    private final ConferenceService conferenceService;
    private final UserService userService;
    private final User user;

    @GetMapping
    @ApiOperation(value = "Get conferences", notes = "Returns a list of conferences. You can filter by 'id' or 'title'.")
    public ResponseEntity<?> getConference(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String title) {

        try {
            if (id != null) {
                return ResponseEntity.ok(conferenceService.findById(id));
            } else if (title != null) {
                return ResponseEntity.ok(conferenceService.findByTitle(title));
            } else {
                return ResponseEntity.ok(conferenceService.findAll());
            }
        } catch(SQLException e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping
    @ApiOperation(value = "Add a conference", notes = "Add a new conference to the system.")
    public ResponseEntity<?> addConference(@RequestBody Conference conference) {

        if (user == null) {
            return ResponseEntity.badRequest().body("Need login");
        }

        try {
            if (conferenceService.exist(conference.getConferenceTitle())) {
                throw new SQLException("Conference exists");
            }

            conference.setAuthor(user);
            conferenceService.add(conference);

            userService.findByUsername(user.getUsername()).getUserConferences().add(conference);
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping
    @ApiOperation(value = "Delete a conference", notes = "Delete a conference by its ID.")
    public ResponseEntity<?> deleteConference(
            @ApiParam(value = "ID of the conference to delete", required = true)
            @RequestParam Long id) {

        if (user == null) {
            return ResponseEntity.badRequest().body("Need login");
        }

        try {
            Conference conference = conferenceService.findById(id);

            if (conference == null) {
                return ResponseEntity.status(404).body("Conference not found");
            }

            if (!conferenceService.findAllByUserId(user.getUserId()).contains(conference)) {
                if (!user.getUserId().equals(conference.getAuthor().getUserId())) {
                    throw new SQLException("No permissions");
                }
            }

            conferenceService.remove(id);
            return ResponseEntity.ok("{\"status\":\"success delete\"}");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("{\"status\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping
    @ApiOperation(value = "Update a conference", notes = "Update an existing conference.")
    public ResponseEntity<?> updateConference(@RequestBody Conference conference) {

        if (user == null) {
            return ResponseEntity.badRequest().body("Need login");
        }

        try {
            if (conference.getConferenceId() == null) {
                throw new SQLException("ID not found");
            }

            if (conferenceService.findAllByUserId(user.getUserId()).isEmpty()) {
                throw new SQLException("No permissions");
            }

            conferenceService.update(conference, conference.getConferenceId());
            return ResponseEntity.ok("{\"status\":\"success\"}");
        } catch (SQLException e) {
            return ResponseEntity.status(409).body("{\"status\":\"" + e.getMessage() + "\"}");
        }
    }

}
