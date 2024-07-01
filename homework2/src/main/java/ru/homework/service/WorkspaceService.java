package ru.homework.service;

import ru.homework.DAO.IDAO;
import ru.homework.DAO.WorkspaceDAO;
import ru.homework.DTO.Workspace;
import ru.homework.connection.ConnectionManager;
import ru.homework.exceptions.EntityExistException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class WorkspaceService implements Service<Workspace> {

    private final IDAO<Workspace> workspaceDAO = new WorkspaceDAO();

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

    public Workspace findByTitle(String title) {
        return new Workspace();
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
    public void removeAll() throws SQLException {
        workspaceDAO.removeAll();
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

    @Override
    public boolean exist(Workspace workspace) throws SQLException {

        if(workspace == null) return false;

        Connection connection = ConnectionManager.getConnection();

        String sql = "SELECT COUNT(*) AS count FROM private.t_workspace WHERE workspace_id = ? AND workspace_title = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, workspace.getWorkspaceId());
        statement.setString(2, workspace.getTitle());

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
