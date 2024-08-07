package ru.homework.DAO;

import ru.homework.DTO.User;
import ru.homework.connection.ConnectionManager;
import ru.homework.service.WorkspaceService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IDAO<User> {

    @Override
    public void add(User user) throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String addRequest = "INSERT INTO private.t_user(username, password) VALUES(?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(addRequest);
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getPassword());
        preparedStatement.executeUpdate();
    }

    @Override
    public List<User> findAll() throws SQLException {

        WorkspaceService workspaceService = new WorkspaceService();

        Connection connection = ConnectionManager.getConnection();

        List<User> users = new ArrayList<>();
        String findAllRequest = "SELECT * FROM private.t_user";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(findAllRequest);

        while (resultSet.next()) {
            User user = new User();

            user.setUserId(resultSet.getLong("user_id"));
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));

            Long workspaceId = resultSet.getLong("workspace_id");
            user.setUserWorkspace(workspaceService.findById(workspaceId));

            users.add(user);
        }

        return users;
    }

    @Override
    public User findById(Long id) throws SQLException {

        WorkspaceService workspaceService = new WorkspaceService();
        Connection connection = ConnectionManager.getConnection();

        String findByIdRequest = "SELECT * FROM private.t_user u WHERE u.user_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(findByIdRequest);
        preparedStatement.setLong(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.next())
            return null;

        User user = new User();
        user.setUserId(resultSet.getLong("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setUserWorkspace(workspaceService.findById(resultSet.getLong("workspace_id")));

        return user;
    }

    @Override
    public long findLastId() throws SQLException {
        Connection connection = ConnectionManager.getConnection();

        String findLastRequest = "SELECT * FROM private.t_user ORDER BY user_id DESC LIMIT 1";

        PreparedStatement preparedStatement = connection.prepareStatement(findLastRequest);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next())
            return resultSet.getLong("user_id");

        return 0L;
    }

    @Override
    public void update(User user, Long id) throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String sql = "UPDATE private.t_user SET username = ?, password = ?, workspace_id = ? WHERE user_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        statement.setLong(3, user.getUserWorkspace() == null ? null : user.getUserWorkspace().getWorkspaceId());
        statement.setLong(4, id);

        statement.executeUpdate();
    }

    @Override
    public void remove(Long id) throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String removeById = "DELETE FROM private.t_user u WHERE u.user_id = ?";
        PreparedStatement statement = connection.prepareStatement(removeById);
        statement.setLong(1, id);
        statement.executeUpdate();
    }

    @Override
    public void remove(User user) throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String removeById = "DELETE FROM private.t_user u WHERE u.user_id = ? AND u.username = ?";
        PreparedStatement statement = connection.prepareStatement(removeById);
        statement.setLong(1, user.getUserId());
        statement.setString(2, user.getUsername());
        statement.executeUpdate();
    }

    @Override
    public void removeAll() throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String removeById = "DELETE FROM private.t_user";
        PreparedStatement statement = connection.prepareStatement(removeById);
        statement.executeUpdate();
    }

}
