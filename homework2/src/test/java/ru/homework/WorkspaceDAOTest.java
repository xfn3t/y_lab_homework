package ru.homework;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.homework.DAO.WorkspaceDAO;
import ru.homework.DTO.Workspace;
import ru.homework.connection.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class WorkspaceDAOTest {

    private static final String DATABASENAME = "test_coworking";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "root";

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName(DATABASENAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD);

    private WorkspaceDAO workspaceDAO;

    @BeforeAll
    public static void setUpClass() {
        try(Connection connection = ConnectionManager.getConnection(
                "jdbc:postgresql://localhost:5432/" + DATABASENAME,
                USERNAME,
                PASSWORD
        )
        ) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db/changelog/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.clearCheckSums();
            liquibase.update();
        } catch (SQLException | LiquibaseException e) {
            System.out.println(e.getMessage());
        }
        postgreSQLContainer.start();

    }

    @AfterAll
    public static void tearDownClass() {
        postgreSQLContainer.stop();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        workspaceDAO = new WorkspaceDAO();
        workspaceDAO.removeAll();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        workspaceDAO.removeAll();
    }

    private Date addHours(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTime();
    }

    @Test
    public void testAddWorkspace() throws SQLException {
        Date startDate = new Date();
        Date endDate = addHours(startDate, 1);
        Workspace workspace = new Workspace("Workspace 1", startDate, endDate);
        workspaceDAO.add(workspace);

        List<Workspace> workspaces = workspaceDAO.findAll();
        assertEquals(1, workspaces.size());
        assertEquals("Workspace 1", workspaces.get(0).getTitle());
    }

    @Test
    public void testFindAllWorkspaces() throws SQLException {
        Date startDate1 = new Date();
        Date endDate1 = addHours(startDate1, 1);
        Workspace workspace1 = new Workspace("Workspace 2", startDate1, endDate1);

        Date startDate2 = new Date();
        Date endDate2 = addHours(startDate2, 1);
        Workspace workspace2 = new Workspace("Workspace 3", startDate2, endDate2);

        workspaceDAO.add(workspace1);
        workspaceDAO.add(workspace2);

        List<Workspace> workspaces = workspaceDAO.findAll();
        assertEquals(2, workspaces.size());
    }

    @Test
    public void testFindWorkspaceById() throws SQLException {
        Date startDate = new Date();
        Date endDate = addHours(startDate, 1);
        Workspace workspace = new Workspace("Workspace 4", startDate, endDate);
        workspaceDAO.add(workspace);
        Long id = workspaceDAO.findLastId();

        Workspace foundWorkspace = workspaceDAO.findById(id);
        assertNotNull(foundWorkspace);
        assertEquals("Workspace 4", foundWorkspace.getTitle());
    }

    @Test
    public void testFindLastId() throws SQLException {
        Date startDate = new Date();
        Date endDate = addHours(startDate, 1);
        Workspace workspace = new Workspace(workspaceDAO.findLastId()+1, "Workspace 5", startDate, endDate);
        workspaceDAO.add(workspace);
        Long lastId = workspaceDAO.findLastId();
        assertEquals(workspace.getWorkspaceId(), lastId);
    }

    @Test
    public void testUpdateWorkspace() throws SQLException {
        Date startDate = new Date();
        Date endDate = addHours(startDate, 1);
        Workspace workspace = new Workspace("Workspace 6", startDate, endDate);
        workspaceDAO.add(workspace);
        Long id = workspaceDAO.findLastId();

        workspace.setTitle("Updated Workspace");
        workspaceDAO.update(workspace, id);

        Workspace updatedWorkspace = workspaceDAO.findById(id);
        assertEquals("Updated Workspace", updatedWorkspace.getTitle());
    }

    @Test
    public void testRemoveWorkspaceById() throws SQLException {
        Date startDate = new Date();
        Date endDate = addHours(startDate, 1);
        Workspace workspace = new Workspace("Workspace 7", startDate, endDate);
        workspaceDAO.add(workspace);
        Long id = workspaceDAO.findLastId();

        workspaceDAO.remove(id);

        Workspace foundWorkspace = workspaceDAO.findById(id);
        assertNull(foundWorkspace);
    }

    @Test
    public void testRemoveWorkspace() throws SQLException {
        Date startDate = new Date();
        Date endDate = addHours(startDate, 1);
        Workspace workspace = new Workspace(workspaceDAO.findLastId()+1, "Workspace 8", startDate, endDate);
        workspaceDAO.add(workspace);
        Long id = workspaceDAO.findLastId();

        workspaceDAO.remove(workspace);

        Workspace foundWorkspace = workspaceDAO.findById(id);
        assertNull(foundWorkspace);
    }

    @Test
    public void testRemoveAllWorkspaces() throws SQLException {
        Date startDate1 = new Date();
        Date endDate1 = addHours(startDate1, 1);
        Workspace workspace1 = new Workspace("Workspace 9", startDate1, endDate1);

        Date startDate2 = new Date();
        Date endDate2 = addHours(startDate2, 1);
        Workspace workspace2 = new Workspace("Workspace 10", startDate2, endDate2);

        workspaceDAO.add(workspace1);
        workspaceDAO.add(workspace2);

        workspaceDAO.removeAll();

        List<Workspace> workspaces = workspaceDAO.findAll();
        assertEquals(0, workspaces.size());
    }
}