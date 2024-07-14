package ru.homework.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.homework.DAO.IDAO;
import ru.homework.DTO.Conference;
import ru.homework.connection.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConferenceService implements ru.homework.service.Service<Conference> {

    private final IDAO<Conference> conferenceDAO;
    private final @Lazy UserService userService;

    @Override
    public List<Conference> findAll() throws SQLException {
        return conferenceDAO.findAll();
    }

    @Transactional
    public List<Conference> findAllByUserId(Long userId) throws SQLException {
        Connection connection = ConnectionManager.getConnection();

        List<Conference> conferences = new ArrayList<>();

        String findAllRequest = "SELECT * FROM private.t_conference WHERE user_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(findAllRequest);
        preparedStatement.setLong(1, userId);
        ResultSet resultSet = preparedStatement.executeQuery(findAllRequest);

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
        return conferenceDAO.findById(id);
    }

    public Conference findByTitle(String title) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM private.t_conference WHERE conference_title = ?")) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToConference(rs);
            }
        }
        return null;
    }

    @Override
    public void add(Conference conference) throws SQLException {
        conferenceDAO.add(conference);
    }

    @Override
    public void update(Conference conference, Long id) throws SQLException {
        conferenceDAO.update(conference, id);
    }

    @Override
    public void remove(Long id) throws SQLException {
        conferenceDAO.remove(id);
    }

    @Override
    public void remove(String title) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM private.t_conference WHERE conference_title = ?")) {
            stmt.setString(1, title);
            stmt.executeUpdate();
        }
    }

    @Override
    public boolean exist(Long id) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM private.t_conference WHERE conference_id = ?")) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        }
        return false;
    }

    @Override
    public boolean exist(Conference conference) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM private.t_conference WHERE conference_id = ? AND conference_title = ?")) {
            stmt.setLong(1, conference.getConferenceId());
            stmt.setString(2, conference.getConferenceTitle());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        }
        return false;
    }

    public boolean exist(String title) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM private.t_conference WHERE conference_title = ?")) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        }
        return false;
    }

    @Override
    public long findLastId() throws SQLException {
        return conferenceDAO.findLastId();
    }

    @Override
    public void remove(Conference conference) throws SQLException {
        conferenceDAO.remove(conference.getConferenceId());
    }

    public boolean isDateOverlap(final Date newDate, final List<Conference> conferences) {
        for (Conference conference : conferences) {
            if (!newDate.before(conference.getStartConference()) && !newDate.after(conference.getEndConference())) {
                return true;
            }
        }
        return false;
    }

    public boolean isDateOverlap(final Date newDate, final List<Conference> conferences, final Long conferenceId) {
        for (Conference conference : conferences) {
            if (conference.getConferenceId().equals(conferenceId)) continue;
            if (!newDate.before(conference.getStartConference()) && !newDate.after(conference.getEndConference())) {
                return true;
            }
        }
        return false;
    }

    public List<Conference> filterConferencesByDateRange(Date startDate, Date endDate) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM private.t_conference WHERE start_conference >= ? AND end_conference <= ?")) {
            stmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            stmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            ResultSet rs = stmt.executeQuery();
            return mapResultSetToList(rs);
        }
    }

    public List<Conference> findAllByConferenceRoomNumber(Long numberConferenceRoom) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM private.t_conference WHERE number_conference_room = ?")) {
            stmt.setLong(1, numberConferenceRoom);
            ResultSet rs = stmt.executeQuery();
            return mapResultSetToList(rs);
        }
    }

    private List<Conference> mapResultSetToList(ResultSet rs) throws SQLException {
        List<Conference> conferences = new ArrayList<>();
        while (rs.next()) {
            conferences.add(mapResultSetToConference(rs));
        }
        return conferences;
    }

    private Conference mapResultSetToConference(ResultSet rs) throws SQLException {
        Conference conference = new Conference();
        conference.setConferenceId(rs.getLong("conference_id"));
        conference.setConferenceTitle(rs.getString("conference_title"));
        conference.setStartConference(new Date(rs.getTimestamp("start_conference").getTime()));
        conference.setEndConference(new Date(rs.getTimestamp("end_conference").getTime()));
        conference.setNumberConferenceRoom(rs.getLong("number_conference_room"));
        conference.setAuthor(userService.findById(rs.getLong("user_id")));
        return conference;
    }
}
