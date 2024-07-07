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
import ru.homework.DAO.UserDAO;
import ru.homework.DTO.User;
import ru.homework.connection.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserDAOTest {

    private static final String DATABASENAME = "test_coworking";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "root";

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName(DATABASENAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD);

    private UserDAO userDAO;

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
    }

    @AfterAll
    public static void tearDownClass() throws SQLException {
        postgreSQLContainer.stop();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        userDAO = new UserDAO();
        userDAO.removeAll();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        userDAO.removeAll();
    }

    @Test
    public void testAddUser() throws SQLException {
        User user = new User("testuser", "testpassword");
        userDAO.add(user);

        User foundUser = userDAO.findById(userDAO.findLastId());
        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
        assertEquals("testpassword", foundUser.getPassword());
    }

    @Test
    public void testFindAllUsers() throws SQLException {
        User user1 = new User("user1", "password1");
        User user2 = new User("user2", "password2");
        userDAO.add(user1);
        userDAO.add(user2);

        List<User> users = userDAO.findAll();
        assertEquals(2, users.size());
    }

    @Test
    public void testFindUserById() throws SQLException {
        User user = new User("user3", "password3");
        userDAO.add(user);

        User foundUser = userDAO.findById(userDAO.findLastId());
        assertNotNull(foundUser);
        assertEquals("user3", foundUser.getUsername());
        assertEquals("password3", foundUser.getPassword());
    }

    @Test
    public void testFindLastId() throws SQLException {
        User user = new User("user4", "password4");
        userDAO.add(user);

        long lastId = userDAO.findLastId();
        assertEquals(userDAO.findById(lastId).getUsername(), user.getUsername());
    }

    @Test
    public void testUpdateUser() throws SQLException {
        User user = new User("user5", "password5");
        userDAO.add(user);

        user.setUsername("updatedUser");
        user.setPassword("updatedPassword");
        userDAO.update(user, userDAO.findLastId());

        User updatedUser = userDAO.findById(userDAO.findLastId());
        assertEquals("updatedUser", updatedUser.getUsername());
        assertEquals("updatedPassword", updatedUser.getPassword());
    }

    @Test
    public void testRemoveUserById() throws SQLException {
        User user = new User("user6", "password6");
        userDAO.add(user);

        userDAO.remove(userDAO.findLastId());

        User foundUser = userDAO.findById(userDAO.findLastId());
        assertNull(foundUser);
    }

    @Test
    public void testRemoveUser() throws SQLException {
        User user = new User(userDAO.findLastId()+1, "user7", "password7");
        userDAO.add(user);

        userDAO.remove(user);

        User foundUser = userDAO.findById(user.getUserId());
        assertNull(foundUser);
    }

    @Test
    public void testRemoveAllUsers() throws SQLException {
        User user1 = new User("user8", "password8");
        User user2 = new User("user9", "password9");
        userDAO.add(user1);
        userDAO.add(user2);

        userDAO.removeAll();

        List<User> users = userDAO.findAll();
        assertEquals(0, users.size());
    }
}

