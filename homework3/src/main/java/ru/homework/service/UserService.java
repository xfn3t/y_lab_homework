package ru.homework.service;

import ru.homework.DAO.IDAO;
import ru.homework.DAO.UserDAO;
import ru.homework.DTO.User;
import ru.homework.DTO.Workspace;
import ru.homework.connection.ConnectionManager;
import ru.homework.exception.EntityExistException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserService implements Service<User> {

    private IDAO<User> userDAO = new UserDAO();

    @Override
    public void add(User user) throws EntityExistException, SQLException {
        if (exist(user)) throw new EntityExistException("User exist");
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

    public User findByUsername(String username) throws SQLException {

        WorkspaceService workspaceService = new WorkspaceService();
        Connection connection = ConnectionManager.getConnection();

        String findByIdRequest = "SELECT * FROM private.t_user u WHERE u.username = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(findByIdRequest);
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();

        resultSet.next();

        User user = new User();
        user.setUserId(resultSet.getLong("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setUserWorkspace(workspaceService.findById(resultSet.getLong("workspace_id")));

        return user;
    }

    @Override
    public void update(User user, Long id) throws SQLException {
        userDAO.update(user, id);
    }

    @Override
    public void remove(User user) throws SQLException {
        userDAO.remove(user);
    }

    @Override
    public void remove(Long id) throws SQLException {
        userDAO.remove(id);
    }

    @Override
    public void remove(String username) throws SQLException {
        userDAO.remove(findByUsername(username));
    }

    @Override
    public boolean exist(User user) throws SQLException {
        return userDAO.findAll().stream().anyMatch(
                x -> x.getUserId().equals(user.getUserId()) &&
                     x.getUsername().equals(user.getUsername())
        );
    }

    @Override
    public long findLastId() throws SQLException {
        return userDAO.findLastId();
    }

    @Override
    public boolean exist(final Long id) throws SQLException {
        return userDAO.findAll().stream().anyMatch(x -> x.getUserId().equals(id));
    }

    /**
     * Checks if a user with the given username already exists in the list of users.
     *
     * @param username The username to check.
     * @return true if a user with the given username exists, false otherwise.
     */
    public boolean exist(final String username) throws SQLException {
        return userDAO.findAll().stream().anyMatch(x -> x.getUsername().equals(username));
    }

    public void addWorkspace(Workspace workspace, Long userId) throws SQLException, EntityExistException {

        Connection connection = ConnectionManager.getConnection();
        WorkspaceService workspaceService = new WorkspaceService();


        Workspace w = findById(userId).getUserWorkspace();
        if(w != null)
            workspaceService.remove(w);
        workspaceService.add(workspace);

        String addWorkspace = "UPDATE private.t_user SET workspace_id = ? WHERE user_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(addWorkspace);
        preparedStatement.setLong(1, workspaceService.findLastId());
        preparedStatement.setLong(2, userId);

        preparedStatement.executeUpdate();

    }

    public void removeWorkspace(Long userId) throws SQLException {

        WorkspaceService workspaceService = new WorkspaceService();

        Connection connection = ConnectionManager.getConnection();
        User user = findById(userId);

        String sql = "UPDATE private.t_user SET workspace_id = null WHERE user_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, user.getUserId());

        statement.executeUpdate();

        workspaceService.remove(user.getUserWorkspace().getWorkspaceId());
    }
}
