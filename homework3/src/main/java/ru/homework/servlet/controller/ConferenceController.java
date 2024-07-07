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


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_CREATED);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        InputStream inputStream = request.getInputStream();
        Conference conference = objectMapper.readValue(inputStream, Conference.class);

        try {
            if (conferenceService.exist(conference.getConferenceTitle())) throw new SQLException("Conference exist");

            conference.setAuthor(AuthUserController.getAuthUser().get());
            conferenceService.add(conference);

            AuthUserController.getAuthUser().get().getUserConferences().add(conference);
            response.getWriter().write("{\"status\":\"success\"}");

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            byte[] message = objectMapper.writeValueAsBytes("{\"status\":\"" + e.getMessage() + "\"\n}");
            response.getOutputStream().write(message);
        }

        System.out.println("Received conference: " + conference.getConferenceTitle());
    }


    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        String paramId = request.getParameter("id");
        String paramTitle = request.getParameter("title");

        byte[] message;

        try {
            if (paramId != null) {
                Long id = Long.parseLong(paramId);
                if (AuthUserController.getAuthUser().get().getUserId().equals(conferenceService.findById(id)))
                    conferenceService.remove(id);
                else
                    throw new SQLException("No permissions");

            } else if (paramTitle != null) {
                if (AuthUserController.getAuthUser().get().getUserId().equals(conferenceService.findByTitle(paramTitle)))
                    conferenceService.remove(paramTitle);
                else
                    throw new SQLException("No permissions");
            }

            message = objectMapper.writeValueAsBytes("{\n\"status\":\"success delete\"\n}");
        } catch (NumberFormatException | SQLException e) {
            message = objectMapper.writeValueAsBytes("{\n\"status\": \"" + e.getMessage() + "\"\n}");
        }

        response.getOutputStream().write(message);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_CREATED);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        InputStream inputStream = request.getInputStream();
        Conference conference = objectMapper.readValue(inputStream, Conference.class);

        try {
            if (conference.getConferenceId() == null) throw new SQLException("ID not found");
            if (AuthUserController.getAuthUser().isEmpty()) throw new SQLException("Need login");
            if (AuthUserController.getAuthUser().get().getUserConferences().equals(conference.getConferenceId()))
                throw new SQLException("No permissions");

            conferenceService.update(conference, conference.getConferenceId());
            response.getWriter().write("{\"status\":\"success\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            byte[] message = objectMapper.writeValueAsBytes("{\"status\":\"" + e.getMessage() + "\"\n}");
            response.getOutputStream().write(message);
        }

        System.out.println("Received conference: " + conference.getConferenceTitle());
    }
}
