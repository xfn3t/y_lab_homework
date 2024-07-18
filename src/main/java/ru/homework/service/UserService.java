package ru.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.homework.DAO.IDAO;
import ru.homework.DTO.User;
import ru.homework.DTO.Workspace;
import ru.homework.connection.ConnectionManager;
import ru.homework.exception.EntityExistException;

import java.sql.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements ru.homework.service.Service<User> {

    private final IDAO<User> userDAO;

    private final @Lazy WorkspaceService workspaceService;


    @Override
    @Transactional
    public void add(User user) throws EntityExistException, SQLException {
        if (exist(user)) throw new EntityExistException("User exists");
        userDAO.add(user);
    }

    @Override
    public List<User> findAll() throws SQLException {
        return userDAO.findAll();
    }

    @Override
    public User findById(Long id) throws SQLException {
        return userDAO.findById(id);
    }

    @Transactional
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM private.t_user WHERE username = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
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
    @Transactional
    public void update(User user, Long id) throws SQLException {
        userDAO.update(user, id);
    }

    @Override
    @Transactional
    public void remove(User user) throws SQLException {
        userDAO.remove(user);
    }

    @Override
    @Transactional
    public void remove(Long id) throws SQLException {
        userDAO.remove(id);
    }

    @Override
    @Transactional
    public void remove(String username) throws SQLException {
        String sql = "DELETE FROM private.t_user WHERE username = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    @Override
    public long findLastId() throws SQLException {
        return userDAO.findLastId();
    }

    @Override
    public boolean exist(User user) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM private.t_user WHERE user_id = ? AND username = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, user.getUserId());
            stmt.setString(2, user.getUsername());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count > 0;
                }
            }
        }
        return false;
    }

    public boolean exist(Long id) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM private.t_user WHERE user_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count > 0;
                }
            }
        }
        return false;
    }

    public boolean exist(String username) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM private.t_user WHERE username = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count > 0;
                }
            }
        }
        return false;
    }

    public void updateWorkspaceId(Long userId, Long workspaceId) throws SQLException {
        String sql = "UPDATE private.t_user SET workspace_id = ? WHERE user_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (workspaceId == null) {
                stmt.setNull(1, Types.BIGINT);
            } else {
                stmt.setLong(1, workspaceId);
            }
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        }
    }

    @Transactional
    public void addWorkspace(Workspace workspace, Long userId) throws SQLException, EntityExistException {
        User user = findById(userId);
        Workspace existingWorkspace = user.getUserWorkspace();
        if (existingWorkspace != null) {
            workspaceService.remove(existingWorkspace.getWorkspaceId());
        }
        workspaceService.add(workspace);
        updateWorkspaceId(userId, workspaceService.findLastId());
    }

    @Transactional
    public void removeWorkspace(Long userId) throws SQLException {
        User user = findById(userId);
        Workspace workspace = user.getUserWorkspace();
        if (workspace != null) {
            updateWorkspaceId(userId, null);
            workspaceService.remove(workspace.getWorkspaceId());
        }
    }

}
