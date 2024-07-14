package ru.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.homework.DTO.Conference;
import ru.homework.DTO.User;
import ru.homework.service.ConferenceService;
import ru.homework.service.UserService;

import java.sql.SQLException;

@Controller
@RequestMapping("/conference")
@RequiredArgsConstructor
public class ConferenceController {

    private final ConferenceService conferenceService;
    private final UserService userService;
    private final User user;

    @GetMapping
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
    public ResponseEntity<?> addConference(@RequestBody Conference conference) {

        if (user == null) return ResponseEntity.badRequest().body("Need login");

        try {
            if (conferenceService.exist(conference.getConferenceTitle())) throw new SQLException("Conference exists");

            conference.setAuthor(user);
            conferenceService.add(conference);

            userService.findByUsername(user.getUsername()).getUserConferences().add(conference);
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteConference(@RequestParam Long id) {

        if (user == null) return ResponseEntity.badRequest().body("Need login");

        try {

            if (id != null){
                Conference conference = conferenceService.findById(id);

                if (!conferenceService.findAllByUserId(user.getUserId()).contains(conference)) {
                    if (user.getUserId().equals(conference.getAuthor().getUserId())) {
                        conferenceService.remove(id);
                    } else {
                        throw new SQLException("No permissions");
                    }
                }
            }

            return ResponseEntity.ok("{\"status\":\"success delete\"}");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("{\"status\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping
    public ResponseEntity<?> updateConference(@RequestBody Conference conference) {
        try {
            if (user == null) return ResponseEntity.badRequest().body("Need login");
            if (conference.getConferenceId() == null) throw new SQLException("ID not found");
            if (conferenceService.findAllByUserId(user.getUserId()).isEmpty()) throw new SQLException("No permissions");

            conferenceService.update(conference, conference.getConferenceId());
            return ResponseEntity.ok("{\"status\":\"success\"}");
        } catch (SQLException e) {
            return ResponseEntity.status(409).body("{\"status\":\"" + e.getMessage() + "\"}");
        }
    }
}
