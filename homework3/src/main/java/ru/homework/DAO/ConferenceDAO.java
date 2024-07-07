package ru.homework.DAO;

import ru.homework.DTO.Conference;
import ru.homework.connection.ConnectionManager;
import ru.homework.service.UserService;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConferenceDAO implements IDAO<Conference> {

    private static final UserService userService = new UserService();

    @Override
    public void add(Conference conference) throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String addRequest = "INSERT INTO private.t_conference(conference_id, conference_title, start_conference, end_conference, number_conference_room, user_id) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(addRequest);
        preparedStatement.setLong(1, findLastId()+1);
        preparedStatement.setString(2, conference.getConferenceTitle());
        preparedStatement.setTimestamp(3, new Timestamp(conference.getStartConference().getTime()));
        preparedStatement.setTimestamp(4, new Timestamp(conference.getEndConference().getTime()));
        preparedStatement.setLong(5, conference.getNumberConferenceRoom());
        preparedStatement.setLong(6, conference.getAuthor().getUserId());

        preparedStatement.executeUpdate();
    }

    @Override
    public List<Conference> findAll() throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        List<Conference> conferences = new ArrayList<>();

        String findAllRequest = "SELECT * FROM private.t_conference";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(findAllRequest);

        while (resultSet.next()) {
            Conference conference = new Conference();

            conference.setConferenceId(resultSet.getLong("conference_id"));
            conference.setConferenceTitle(resultSet.getString("conference_title"));
            conference.setStartConference(new Date(resultSet.getTimestamp("start_conference").getTime()));
            conference.setEndConference(new Date(resultSet.getTimestamp("end_conference").getTime()));
            conference.setNumberConferenceRoom(resultSet.getLong("number_conference_room"));
            conference.setAuthor(userService.findById(resultSet.getLong("user_id")));

            conferences.add(conference);
        }

        return conferences;
    }

    @Override
    public Conference findById(Long id) throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String findByIdRequest = "SELECT * FROM private.t_conference c WHERE c.conference_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(findByIdRequest);
        preparedStatement.setLong(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.next()) throw new SQLException("ID not found");

        Conference conference = new Conference();

        conference.setConferenceId(resultSet.getLong("conference_id"));
        conference.setConferenceTitle(resultSet.getString("conference_title"));
        conference.setStartConference(resultSet.getDate("start_conference"));
        conference.setEndConference(resultSet.getDate("end_conference"));
        conference.setNumberConferenceRoom(resultSet.getLong("number_conference_room"));
        conference.setAuthor(userService.findById(resultSet.getLong("user_id")));

        return conference;

    }


    @Override
    public long findLastId() throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String findLastRequest = "SELECT * FROM private.t_conference ORDER BY conference_id DESC LIMIT 1";

        PreparedStatement preparedStatement = connection.prepareStatement(findLastRequest);
        ResultSet resultSet = preparedStatement.executeQuery();

        resultSet.next();

        return resultSet.getLong("conference_id");

    }

    @Override
    public void update(Conference conference, Long id) throws SQLException {

        Connection connection = ConnectionManager.getConnection();
        String updateRequest = "UPDATE private.t_conference SET conference_title = ?, start_conference = ?, end_conference = ?, number_conference_room = ?, user_id = ? WHERE conference_id = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(updateRequest);

        preparedStatement.setString(1, conference.getConferenceTitle());
        preparedStatement.setTimestamp(2, new Timestamp(conference.getStartConference().getTime()));
        preparedStatement.setTimestamp(3, new Timestamp(conference.getEndConference().getTime()));
        preparedStatement.setLong(4, conference.getNumberConferenceRoom());
        preparedStatement.setLong(5, conference.getAuthor().getUserId());
        preparedStatement.setLong(6, id);

        preparedStatement.executeUpdate();
    }

    @Override
    public void remove(Long id) throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String removeByIdRequest = "DELETE FROM private.t_conference c WHERE c.conference_id = ?";
        PreparedStatement statement = connection.prepareStatement(removeByIdRequest);
        statement.setLong(1, id);
        statement.executeUpdate();
    }

    @Override
    public void remove(Conference conference) throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String removeByEntity = "DELETE FROM private.t_conference c WHERE c.conference_id = ? AND c.conference_title = ?";
        PreparedStatement statement = connection.prepareStatement(removeByEntity);
        statement.setLong(1, conference.getConferenceId());
        statement.setString(2, conference.getConferenceTitle());
        statement.executeUpdate();
    }

    @Override
    public void removeAll() throws SQLException {
        Connection connection = ConnectionManager.getConnection();

        String removeAllRequest = "DELETE FROM private.t_conference";
        PreparedStatement statement = connection.prepareStatement(removeAllRequest);
        statement.executeUpdate();
    }
}
