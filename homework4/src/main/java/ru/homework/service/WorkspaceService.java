package ru.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.homework.DAO.IDAO;
import ru.homework.DTO.Workspace;
import ru.homework.connection.ConnectionManager;
import ru.homework.exception.EntityExistException;

import java.sql.*;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceService implements ru.homework.service.Service<Workspace> {

    private final IDAO<Workspace> workspaceDAO;

    @Override
    public void add(Workspace workspace) throws EntityExistException, SQLException {
        workspaceDAO.add(workspace);
    }

    @Override
    public List<Workspace> findAll() throws SQLException {
        return workspaceDAO.findAll();
    }

    @Override
    public Workspace findById(Long id) throws SQLException {
        return workspaceDAO.findById(id);
    }

    public Workspace findByTitle(String title) throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String findByIdRequest = "SELECT * FROM private.t_workspace WHERE workspace_title = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(findByIdRequest);
        preparedStatement.setString(1, title);
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
    public void update(Workspace workspace, Long id) throws SQLException {
        workspaceDAO.update(workspace, id);
    }

    @Override
    public void remove(Workspace workspace) throws SQLException {
        workspaceDAO.remove(findById(workspace.getWorkspaceId()));
    }

    @Override
    public void remove(Long id) throws SQLException {
        workspaceDAO.remove(id);
    }

    @Override
    public void remove(String title) throws SQLException {
        workspaceDAO.remove(findByTitle(title));
    }

    @Override
    public boolean exist(Long id) throws SQLException {
        Connection connection = ConnectionManager.getConnection();

        String sql = "SELECT COUNT(*) AS count FROM private.t_workspace WHERE workspace_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, id);

        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        int count = resultSet.getInt("count");
        return count > 0;
    }

    public boolean exist(String title) throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String sql = "SELECT COUNT(*) AS count FROM private.t_workspace WHERE workspace_title = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, title);

        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        int count = resultSet.getInt("count");
        return count > 0;
    }

    @Override
    public boolean exist(Workspace workspace) throws SQLException {

        if(workspace == null) return false;

        Connection connection = ConnectionManager.getConnection();

        String sql = "SELECT COUNT(*) AS count FROM private.t_workspace WHERE workspace_title = ? AND start_reservations = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, workspace.getTitle());
        statement.setTimestamp(2, new Timestamp(workspace.getStartReservations().getTime()));

        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        int count = resultSet.getInt("count");
        return count > 0;
    }

    public boolean isDateOverlap(final Date newDate) throws SQLException {
        for (Workspace workspace : findAll()) {
            if (!newDate.before(workspace.getStartReservations()) && !newDate.after(workspace.getEndReservations())) {
                return true;
            }
        }
        return false;
    }

    public boolean isDateOverlap(final Date newDate, final Long workspaceId) throws SQLException {
        for (Workspace workspace : findAll()) {
            if (workspace.getWorkspaceId().equals(workspaceId)) continue;
            if (!newDate.before(workspace.getStartReservations()) && !newDate.after(workspace.getEndReservations())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long findLastId() throws SQLException {
        return workspaceDAO.findLastId();
    }
}
