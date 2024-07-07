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
import ru.homework.DAO.ConferenceDAO;
import ru.homework.DTO.Conference;
import ru.homework.DTO.User;
import ru.homework.connection.ConnectionManager;
import ru.homework.exceptions.EntityExistException;
import ru.homework.service.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class ConferenceDAOTest {

    private static final String DATABASENAME = "test_coworking";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "root";

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName(DATABASENAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD);

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

    private ConferenceDAO conferenceDAO;
    private UserService userService;
    private User testUser;

    @AfterAll
    public static void tearDownClass() throws SQLException {
        postgreSQLContainer.stop();
    }

    @BeforeEach
    public void setUp() throws SQLException, EntityExistException {
        conferenceDAO = new ConferenceDAO();
        userService = new UserService();
        conferenceDAO.removeAll();
        userService.removeAll();
        testUser = new User("testUser", "password");
        userService.add(testUser);
        testUser.setUserId(userService.findLastId());
    }

    @AfterEach
    public void tearDown() throws SQLException {
        conferenceDAO.removeAll();
        userService.removeAll();
    }

    private Date addHours(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTime();
    }

    @Test
    public void testAddConference() throws SQLException {
        Date startDate = new Date();
        Date endDate = addHours(startDate, 1);
        Conference conference = new Conference("Conference 1", startDate, endDate, testUser, 1L);
        conferenceDAO.add(conference);

        List<Conference> conferences = conferenceDAO.findAll();
        assertEquals(1, conferences.size());
        assertEquals("Conference 1", conferences.get(0).getConferenceTitle());
    }

    @Test
    public void testFindAllConferences() throws SQLException {
        Date startDate1 = new Date();
        Date endDate1 = addHours(startDate1, 1);
        Conference conference1 = new Conference("Conference 2", startDate1, endDate1, testUser, 1L);

        Date startDate2 = new Date();
        Date endDate2 = addHours(startDate2, 1);
        Conference conference2 = new Conference("Conference 3", startDate2, endDate2, testUser, 2L);

        conferenceDAO.add(conference1);
        conferenceDAO.add(conference2);

        List<Conference> conferences = conferenceDAO.findAll();
        assertEquals(2, conferences.size());
    }

    @Test
    public void testFindConferenceById() throws SQLException {
        Date startDate = new Date();
        Date endDate = addHours(startDate, 1);
        Conference conference = new Conference("Conference 4", startDate, endDate, testUser, 1L);
        conferenceDAO.add(conference);
        Long id = conferenceDAO.findLastId();

        Conference foundConference = conferenceDAO.findById(id);
        assertNotNull(foundConference);
        assertEquals("Conference 4", foundConference.getConferenceTitle());
    }

    @Test
    public void testFindLastId() throws SQLException {
        Date startDate = new Date();
        Date endDate = addHours(startDate, 1);
        Conference conference = new Conference(conferenceDAO.findLastId()+1, "Conference 5", startDate, endDate, testUser, 1L);
        conferenceDAO.add(conference);
        Long lastId = conferenceDAO.findLastId();
        assertEquals(conference.getConferenceId(), lastId);
    }

    @Test
    public void testUpdateConference() throws SQLException {
        Date startDate = new Date();
        Date endDate = addHours(startDate, 1);
        Conference conference = new Conference("Conference 6", startDate, endDate, testUser, 1L);
        conferenceDAO.add(conference);
        Long id = conferenceDAO.findLastId();

        conference.setConferenceTitle("Updated Conference");
        conferenceDAO.update(conference, id);

        Conference updatedConference = conferenceDAO.findById(id);
        assertEquals("Updated Conference", updatedConference.getConferenceTitle());
    }

    @Test
    public void testRemoveConferenceById() throws SQLException {
        Date startDate = new Date();
        Date endDate = addHours(startDate, 1);
        Conference conference = new Conference("Conference 7", startDate, endDate, testUser, 1L);
        conferenceDAO.add(conference);
        Long id = conferenceDAO.findLastId();

        conferenceDAO.remove(id);

        Conference foundConference = conferenceDAO.findById(id);
        assertNull(foundConference);
    }

    @Test
    public void testRemoveConference() throws SQLException {
        Date startDate = new Date();
        Date endDate = addHours(startDate, 1);
        Conference conference = new Conference(conferenceDAO.findLastId()+1, "Conference 8", startDate, endDate, testUser, 1L);
        conferenceDAO.add(conference);
        Long id = conferenceDAO.findLastId();

        conferenceDAO.remove(conference);

        Conference foundConference = conferenceDAO.findById(id);
        assertNull(foundConference);
    }

    @Test
    public void testRemoveAllConferences() throws SQLException {
        Date startDate1 = new Date();
        Date endDate1 = addHours(startDate1, 1);
        Conference conference1 = new Conference("Conference 9", startDate1, endDate1, testUser, 1L);

        Date startDate2 = new Date();
        Date endDate2 = addHours(startDate2, 1);
        Conference conference2 = new Conference("Conference 10", startDate2, endDate2, testUser, 2L);

        conferenceDAO.add(conference1);
        conferenceDAO.add(conference2);

        conferenceDAO.removeAll();

        List<Conference> conferences = conferenceDAO.findAll();
        assertEquals(0, conferences.size());
    }
}
