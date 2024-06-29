package ru.homework.DAO;

import ru.homework.DTO.Conference;
import ru.homework.connection.ConnectionManager;
import ru.homework.service.UserService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConferenceDAO {


    private static final String URL = "jdbc:postgresql://localhost:5432/coworking";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "root";

    private static final UserService userService = new UserService();


    public void add(Conference conference) throws SQLException {

        Connection connection = ConnectionManager.getConnection(URL, USERNAME, PASSWORD);

        String addRequest = "INSERT INTO private.t_conference(title, start_conference, end_conference, number_conference_room, user_id) VALUES(?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(addRequest);
        preparedStatement.setString(1, conference.getConferenceTitle());
        preparedStatement.setDate(2, (Date) conference.getStartConference());
        preparedStatement.setDate(3, (Date) conference.getEndConference());
        preparedStatement.setLong(4, conference.getNumberConferenceRoom());
        preparedStatement.setLong(5, conference.getAuthor().getUserId());
        preparedStatement.executeUpdate();
    }

    public List<Conference> findAll() throws SQLException {

        Connection connection = ConnectionManager.getConnection(URL, USERNAME, PASSWORD);

        List<Conference> conferences = new ArrayList<>();

        String findAllRequest = "SELECT * FROM private.t_conference";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(findAllRequest);

        while (resultSet.next()) {
            Conference conference = new Conference();

            conference.setConferenceId(resultSet.getLong("conference_id"));
            conference.setConferenceTitle(resultSet.getString("conference_title"));
            conference.setStartConference(resultSet.getDate("start_conference"));
            conference.setEndConference(resultSet.getDate("end_conference"));
            conference.setNumberConferenceRoom(resultSet.getLong("number_conference_room"));
            conference.setAuthor(userService.findById(resultSet.getLong("user_id")));

            conferences.add(conference);
        }

        return conferences;
    }

    public Conference findById(Long id) throws SQLException {

        Connection connection = ConnectionManager.getConnection(URL, USERNAME, PASSWORD);

        String findByIdRequest = "SELECT * FROM private.t_conference c WHERE c.conference_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(findByIdRequest);
        preparedStatement.setLong(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        resultSet.next();

        Conference conference = new Conference();

        conference.setConferenceId(resultSet.getLong("conference_id"));
        conference.setConferenceTitle(resultSet.getString("conference_title"));
        conference.setStartConference(resultSet.getDate("start_conference"));
        conference.setEndConference(resultSet.getDate("end_conference"));
        conference.setNumberConferenceRoom(resultSet.getLong("number_conference_room"));
        conference.setAuthor(userService.findById(resultSet.getLong("user_id")));

        return conference;

    }

    public void update(Conference conference, Long id) throws SQLException {

        Connection connection = ConnectionManager.getConnection(URL, USERNAME, PASSWORD);
        String updateRequest = "UPDATE conferences SET conference_title = ?, start_conference = ?, end_conference = ?, number_conference_room = ?, user_id = ? WHERE conference_id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(updateRequest);

        preparedStatement.setString(1, conference.getConferenceTitle());
        preparedStatement.setTimestamp(2, new java.sql.Timestamp(conference.getStartConference().getTime()));
        preparedStatement.setTimestamp(3, new java.sql.Timestamp(conference.getEndConference().getTime()));
        preparedStatement.setLong(4, conference.getNumberConferenceRoom());
        preparedStatement.setString(5, conference.getAuthor().getUsername());
        preparedStatement.setLong(6, id);

        preparedStatement.executeUpdate();
    }

    public void remove(Long id) throws SQLException {

        Connection connection = ConnectionManager.getConnection(URL, USERNAME, PASSWORD);

        String removeByIdRequest = "DELETE FROM private.t_conference c WHERE c.conference_id = ?";
        PreparedStatement statement = connection.prepareStatement(removeByIdRequest);
        statement.setLong(1, id);
        statement.executeUpdate();
    }

    public void remove(Conference conference) throws SQLException {

        Connection connection = ConnectionManager.getConnection(URL, USERNAME, PASSWORD);

        String removeById = "DELETE FROM private.t_conference c WHERE c.conference_id = ? AND c.conference_title = ?";
        PreparedStatement statement = connection.prepareStatement(removeById);
        statement.setLong(1, conference.getConferenceId());
        statement.setString(2, conference.getConferenceTitle());
        statement.executeUpdate();
    }

    public void removeAll() throws SQLException {
        Connection connection = ConnectionManager.getConnection(URL, USERNAME, PASSWORD);

        String removeAllRequest = "DELETE FROM private.t_conference";
        PreparedStatement statement = connection.prepareStatement(removeAllRequest);
        statement.executeUpdate();
    }

}
