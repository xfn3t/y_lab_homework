package ru.homework.servlet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.homework.DTO.User;
import ru.homework.exception.EntityExistException;
import ru.homework.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class UserControllerServlet extends HttpServlet {

    private final ObjectMapper objectMapper;
    private final UserService userService;

    public UserControllerServlet() {
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        String paramId = request.getParameter("id");
        String paramUsername = request.getParameter("name");

        byte[] message;

        try {
            if (paramId != null) {
                message = objectMapper.writeValueAsBytes(
                        userService.findById(Long.parseLong(paramId))
                );

            } else if (paramUsername != null) {
                message = objectMapper.writeValueAsBytes(
                        userService.findByUsername(paramUsername)
                );
            } else {
                message = objectMapper.writeValueAsBytes(
                        userService.findAll()
                );
            }
        } catch (NumberFormatException | SQLException e) {
            message = objectMapper.writeValueAsBytes("\n\"status\": \"" + e.getMessage() + "\"\n}");
        }

        response.getOutputStream().write(message);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_CREATED);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        InputStream inputStream = request.getInputStream();
        User user = objectMapper.readValue(inputStream, User.class);

        try {
            if (userService.exist(user.getUsername())) throw new SQLException("User exist");
            userService.add(user);
            response.getWriter().write("{\"status\":\"success\"}");
        } catch (EntityExistException | SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            byte[] message = objectMapper.writeValueAsBytes("{\"status\":\"" + e.getMessage() + "\"\n}");
            response.getOutputStream().write(message);
        }

        System.out.println("Received user: " + user);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        String paramId = request.getParameter("id");
        String paramUsername = request.getParameter("name");

        byte[] message;

        try {
            if (paramId != null) {
                Long id = Long.parseLong(paramId);
                if (AuthUserController.getAuthUser().get().getUserId().equals(id))
                    userService.remove(id);
            } else if (paramUsername != null) {
                if (AuthUserController.getAuthUser().get().getUsername().equals(paramUsername))
                    userService.remove(paramUsername);
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
        User user = objectMapper.readValue(inputStream, User.class);

        try {
            if (user.getUserId() == null) throw new SQLException("Bad ID");
            userService.update(user, user.getUserId());
            response.getWriter().write("{\"status\":\"success\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            byte[] message = objectMapper.writeValueAsBytes("{\"status\":\"" + e.getMessage() + "\"\n}");
            response.getOutputStream().write(message);
        }

        System.out.println("Received user: " + user);
    }
}
