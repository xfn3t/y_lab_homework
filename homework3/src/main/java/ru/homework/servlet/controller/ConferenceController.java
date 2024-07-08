package ru.homework.servlet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.homework.DTO.Conference;
import ru.homework.service.ConferenceService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class ConferenceController extends HttpServlet {

    private ObjectMapper objectMapper;
    private ConferenceService conferenceService;

    public ConferenceController() {
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.conferenceService = new ConferenceService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        String paramId = request.getParameter("id");
        String paramTitle = request.getParameter("title");

        byte[] message;

        try {
            if (paramId != null) {
                message = objectMapper.writeValueAsBytes(
                        conferenceService.findById(Long.parseLong(paramId))
                );

            } else if (paramTitle != null) {
                message = objectMapper.writeValueAsBytes(
                        conferenceService.findByTitle(paramTitle)
                );
            } else {
                message = objectMapper.writeValueAsBytes(
                        conferenceService.findAll()
                );
            }
        } catch (NumberFormatException | SQLException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            message = objectMapper.writeValueAsBytes("{\n\"status\": \"" + e.getMessage() + "\"\n}");
        }

        response.getOutputStream().write(message);
    }
}