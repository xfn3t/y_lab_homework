package ru.homework.servlet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.homework.DTO.User;
import ru.homework.forms.Login;
import ru.homework.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Optional;


public class AuthUserController extends HttpServlet {

    private static Optional<User> authUser = Optional.empty();

    public static Optional<User> getAuthUser() {
        return authUser;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (!authUser.isEmpty()) authUser = Optional.empty();

        UserService userService = new UserService();
        response.setStatus(HttpServletResponse.SC_OK);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        InputStream inputStream = request.getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(inputStream, User.class);
        try {
            authUser = Optional.of(
                    userService.findByUsername(
                            Login.loginUser(user.getUsername(), user.getPassword()).get().getUsername()
                    )
            );
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        response.getWriter().write("{\"status\":\"user authed\"}");

        System.out.println("User auth: " + authUser.get());
    }
}
