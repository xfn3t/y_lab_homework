package ru.homework.servlet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.homework.DTO.Conference;
import ru.homework.DTO.Workspace;
import ru.homework.exception.EntityExistException;
import ru.homework.service.UserService;
import ru.homework.service.WorkspaceService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class WorkspaceController extends HttpServlet {

    private ObjectMapper objectMapper;
    private WorkspaceService workspaceService;

    public WorkspaceController() {
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.workspaceService = new WorkspaceService();
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
                        workspaceService.findById(Long.parseLong(paramId))
                );

            } else if (paramTitle != null) {
                message = objectMapper.writeValueAsBytes(
                        workspaceService.findByTitle(paramTitle)
                );
            } else {
                message = objectMapper.writeValueAsBytes(
                        workspaceService.findAll()
                );
            }
        } catch (NumberFormatException | SQLException e) {
            message = objectMapper.writeValueAsBytes("\n\"status\": \"" + e.getMessage() + "\"\n}");
        }

        response.getOutputStream().write(message);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        UserService userService = new UserService();

        response.setStatus(HttpServletResponse.SC_CREATED);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        InputStream inputStream = request.getInputStream();
        Workspace workspace = objectMapper.readValue(inputStream, Workspace.class);

        System.out.println(workspace);

        try {
            if (workspaceService.exist(workspace)) throw new SQLException("Workspace exist");
            if (AuthUserController.getAuthUser().isEmpty()) throw new SQLException("Need login");

            AuthUserController.getAuthUser().get().setUserWorkspace(workspace);
            workspaceService.add(workspace);
            userService.addWorkspace(workspace, AuthUserController.getAuthUser().get().getUserId());

            response.getWriter().write("{\"status\":\"success\"}");

        } catch (EntityExistException | SQLException e) {
            System.out.println(e.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            byte[] message = objectMapper.writeValueAsBytes("{\"status\":\"" + e.getMessage() + "\"\n}");
            response.getOutputStream().write(message);
        }

        System.out.println("Received workspace: " + workspace.getTitle());
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
                if (AuthUserController.getAuthUser().get().getUserId().equals(workspaceService.findById(id)))
                    workspaceService.remove(id);
                else
                    throw new SQLException("No permissions");

            } else if (paramTitle != null) {
                if (AuthUserController.getAuthUser().get().getUserId().equals(workspaceService.findByTitle(paramTitle)))
                    workspaceService.remove(paramTitle);
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
        Workspace workspace = objectMapper.readValue(inputStream, Workspace.class);

        try {
            if (workspace.getWorkspaceId() == null) throw new SQLException("ID not found");
            if (AuthUserController.getAuthUser().get().getUserConferences().equals(workspace.getWorkspaceId()))
                throw new SQLException("No permissions");

            workspaceService.update(workspace, workspace.getWorkspaceId());
            response.getWriter().write("{\"status\":\"success\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            byte[] message = objectMapper.writeValueAsBytes("{\"status\":\"" + e.getMessage() + "\"\n}");
            response.getOutputStream().write(message);
        }

        System.out.println("Received workspace: " + workspace.getTitle());
    }
}
