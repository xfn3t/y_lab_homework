package ru.homework.DAO;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import ru.homework.DTO.User;
import ru.homework.connection.ConnectionManager;
import ru.homework.service.ConferenceService;
import ru.homework.service.WorkspaceService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserDAO implements IDAO<User> {

    private final @Lazy WorkspaceService workspaceService;

    @Override
    public void add(User user) throws SQLException {
        String sql = "INSERT INTO private.t_user (user_id, username, password) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, findLastId()+1);
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM private.t_user";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getLong("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setUserWorkspace(workspaceService.findById(rs.getLong("workspace_id")));
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public User findById(Long id) throws SQLException {
        String sql = "SELECT * FROM private.t_user WHERE user_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getLong("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setUserWorkspace(workspaceService.findById(rs.getLong("workspace_id")));
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public void update(User user, Long id) throws SQLException {
        String sql = "UPDATE private.t_user SET username = ?, password = ? WHERE user_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setLong(3, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public void remove(User user) throws SQLException {
        String sql = "DELETE FROM private.t_user WHERE user_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, user.getUserId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void remove(Long id) throws SQLException {
        String sql = "DELETE FROM private.t_user WHERE user_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public void removeAll() throws SQLException {
        String sql = "DELETE FROM private.t_user";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    @Override
    public long findLastId() throws SQLException {
        String sql = "SELECT MAX(user_id) FROM private.t_user";
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

}
