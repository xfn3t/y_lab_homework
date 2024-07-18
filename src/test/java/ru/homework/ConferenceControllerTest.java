package ru.homework;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.homework.DTO.Conference;
import ru.homework.DTO.User;
import ru.homework.controller.ConferenceController;
import ru.homework.service.ConferenceService;
import ru.homework.service.UserService;

import java.sql.SQLException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ConferenceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ConferenceService conferenceService;

    @Mock
    private UserService userService;

    @Mock
    private User user;

    @InjectMocks
    private ConferenceController conferenceController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(conferenceController).build();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        conferenceService.removeAll();
    }

    @Test
    public void testGetConferenceById() throws Exception {
        Conference conference = new Conference();
        conference.setConferenceId(1L);
        when(conferenceService.findById(anyLong())).thenReturn(conference);

        mockMvc.perform(get("/conference?id=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conferenceId").value(1L));

        verify(conferenceService, times(1)).findById(anyLong());
    }

    @Test
    public void testAddConference() throws Exception {
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(user.getUsername()).thenReturn("testuser");

        Conference conference = new Conference();
        conference.setConferenceTitle("Test Conference");
        conference.setAuthor(user);

        when(conferenceService.exist(anyString())).thenReturn(false);

        mockMvc.perform(post("/conference")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"conferenceTitle\": \"Test Conference\"}"))
                .andExpect(status().isOk());

        verify(conferenceService, times(1)).add(any(Conference.class));
    }

    @Test
    public void testDeleteConference() throws Exception {
        Conference conference = new Conference();
        conference.setConferenceId(1L);
        conference.setAuthor(user);

        when(conferenceService.findById(anyLong())).thenReturn(conference);
        when(user.getUserId()).thenReturn(1L);
        when(conferenceService.findAllByUserId(anyLong())).thenReturn(Collections.singletonList(conference));

        mockMvc.perform(delete("/conference?id=1"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"status\":\"success delete\"}"));

        verify(conferenceService, times(1)).remove(anyLong());
    }

    @Test
    public void testUpdateConference() throws Exception {
        Conference conference = new Conference();
        conference.setConferenceId(1L);
        conference.setConferenceTitle("Updated Conference");

        when(user.getUserId()).thenReturn(1L);
        when(conferenceService.findAllByUserId(anyLong())).thenReturn(Collections.singletonList(conference));

        mockMvc.perform(put("/conference")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"conferenceId\": 1, \"conferenceTitle\": \"Updated Conference\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"status\":\"success\"}"));

        verify(conferenceService, times(1)).update(any(Conference.class), anyLong());
    }

}
