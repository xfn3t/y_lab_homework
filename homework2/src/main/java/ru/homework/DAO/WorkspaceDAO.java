package ru.homework.DAO;

import ru.homework.DTO.Workspace;
import ru.homework.connection.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkspaceDAO implements IDAO<Workspace> {

    @Override
    public void add(Workspace workspace) throws SQLException {

        Connection connection = ConnectionManager.getConnection();
        String addRequest = "INSERT INTO private.t_workspace(workspace_id, workspace_title, start_reservations, end_reservations) VALUES(?, ?, ?, ?)";


        PreparedStatement preparedStatement = connection.prepareStatement(addRequest);
        preparedStatement.setLong(1, findLastId()+1);
        preparedStatement.setString(2, workspace.getTitle());
        preparedStatement.setTimestamp(3, new Timestamp(workspace.getStartReservations().getTime()));
        preparedStatement.setTimestamp(4, new Timestamp(workspace.getEndReservations().getTime()));
        preparedStatement.executeUpdate();

    }

    @Override
    public List<Workspace> findAll() throws SQLException {

        List<Workspace> workspaces = new ArrayList<>();

        Connection connection = ConnectionManager.getConnection();
        String findAllRequest = "SELECT * FROM private.t_workspace";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(findAllRequest);

        while (resultSet.next()) {
            Workspace workspace = new Workspace();

            workspace.setWorkspaceId(resultSet.getLong("workspace_id"));
            workspace.setTitle(resultSet.getString("workspace_title"));
            workspace.setStartReservations(new Date(resultSet.getTimestamp("start_reservations").getTime()));
            workspace.setEndReservations(new Date(resultSet.getTimestamp("end_reservations").getTime()));

            workspaces.add(workspace);
        }

        return workspaces;
    }

    @Override
    public Workspace findById(Long id) throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String findByIdRequest = "SELECT * FROM private.t_workspace WHERE workspace_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(findByIdRequest);
        preparedStatement.setLong(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.next()) return null;
        Workspace workspace = new Workspace();

        workspace.setWorkspaceId(resultSet.getLong("workspace_id"));
        workspace.setTitle(resultSet.getString("workspace_title"));
        workspace.setStartReservations(new Date(resultSet.getTimestamp("start_reservations").getTime()));
        workspace.setEndReservations(new Date(resultSet.getTimestamp("end_reservations").getTime()));

        return workspace;
    }

    @Override
    public Long findLastId() throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String findLastRequest = "SELECT * FROM private.t_workspace ORDER BY workspace_id DESC LIMIT 1";

        PreparedStatement preparedStatement = connection.prepareStatement(findLastRequest);
        ResultSet resultSet = preparedStatement.executeQuery();

        long result = 0L;
        if(resultSet.next())
            result = resultSet.getLong("workspace_id");

        return result;

    }

    @Override
    public void update(Workspace workspace, Long id) throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String updateRequest = "UPDATE private.t_workspace SET workspace_title = ?, start_reservations = ?, end_reservations = ? WHERE workspace_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateRequest);
        preparedStatement.setString(1, workspace.getTitle());
        preparedStatement.setTimestamp(2, new Timestamp(workspace.getStartReservations().getTime()));
        preparedStatement.setTimestamp(3, new Timestamp(workspace.getEndReservations().getTime()));
        preparedStatement.setLong(4, id);

        preparedStatement.executeUpdate();
    }

    @Override
    public void remove(Long id) throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String removeByIdRequest = "DELETE FROM private.t_workspace AS c WHERE c.workspace_id = ?";
        PreparedStatement statement = connection.prepareStatement(removeByIdRequest);
        statement.setLong(1, id);
        statement.executeUpdate();
    }

    @Override
    public void remove(Workspace workspace) throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String removeByEntity = "DELETE FROM private.t_workspace AS w WHERE w.workspace_id = ? AND w.workspace_title = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(removeByEntity);
        preparedStatement.setLong(1, workspace.getWorkspaceId());
        preparedStatement.setString(2, workspace.getTitle());

        preparedStatement.executeUpdate();

    }

    @Override
    public void removeAll() throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String removeAllRequest = "DELETE FROM private.t_workspace";
        PreparedStatement statement = connection.prepareStatement(removeAllRequest);
        statement.executeUpdate();
    }
}
