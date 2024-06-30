package ru.homework.service;

import ru.homework.DAO.ConferenceDAO;
import ru.homework.DAO.IDAO;
import ru.homework.DTO.Conference;
import ru.homework.connection.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ConferenceService implements Service<Conference> {

    private IDAO<Conference> conferenceDAO = new ConferenceDAO();

    @Override
    public List<Conference> findAll() throws SQLException {
        return conferenceDAO.findAll();
    }

    @Override
    public Conference findById(Long id) throws SQLException {
        return conferenceDAO.findById(id);
    }

    public List<Conference> findAllByUserId(Long userId) throws SQLException {
        return conferenceDAO.findAll().stream()
                .filter(x -> x.getAuthor().getUserId().equals(userId))
                .collect(Collectors.toList());
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
    public boolean exist(Long id) throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String sql = "SELECT COUNT(*) AS count FROM private.t_conference WHERE conference_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, id);

        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        int count = resultSet.getInt("count");
        return count > 0;
    }

    @Override
    public boolean exist(Conference conference) throws SQLException {

        Connection connection = ConnectionManager.getConnection();

        String sql = "SELECT COUNT(*) AS count FROM private.t_conference WHERE conference_id = ? AND conference_title = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, conference.getConferenceId());
        statement.setString(2, conference.getConferenceTitle());

        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        int count = resultSet.getInt("count");
        return count > 0;
    }

    @Override
    public long findLastId() throws SQLException {
        return findLastId();
    }

    @Override
    public void remove(Conference conference) throws SQLException {
        conferenceDAO.remove(conference);
    }

    /**
     * Checks if the dates of a new conference overlap with any existing conferences.
     *
     * @param newConference the new conference to check.
     * @param conferences the list of existing conferences.
     * @return true if there is an overlap, otherwise false.
     */
    public boolean isDateOverlap(final Conference newConference, final List<Conference> conferences) {
        for (Conference conference : conferences) {
            if (newConference.getStartConference().before(conference.getEndConference()) &&
                    newConference.getEndConference().after(conference.getStartConference())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a given date overlaps with the dates of any existing conferences.
     *
     * @param newDate the date to check.
     * @param conferences the list of existing conferences.
     * @return true if there is an overlap, otherwise false.
     */
    public boolean isDateOverlap(final Date newDate, final List<Conference> conferences) {
        for (Conference conference : conferences) {
            if (!newDate.before(conference.getStartConference()) && !newDate.after(conference.getEndConference())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a given date overlaps with the dates of any existing conferences, excluding
     * a specific conference.
     *
     * @param newDate the date to check.
     * @param conferences the list of existing conferences.
     * @param conferenceId the ID of the conference to exclude from the overlap check.
     * @return true if there is an overlap, otherwise false.
     */
    public boolean isDateOverlap(final Date newDate, final List<Conference> conferences, final Long conferenceId) {
        for (Conference conference : conferences) {
            if (conference.getConferenceId().equals(conferenceId)) continue;
            if (!newDate.before(conference.getStartConference()) && !newDate.after(conference.getEndConference())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Filters a list of conferences to include only those that start on or after the given start date
     * and end on or before the given end date.
     *
     * @param startDate The start date to filter the conferences.
     * @param endDate The end date to filter the conferences.
     * @return A list of conferences that occur within the specified date range.
     */
    public List<Conference> filterConferencesByDateRange(Date startDate, Date endDate) throws SQLException {
        List<Conference> filteredConferences = new ArrayList<>();
        for (Conference conference : conferenceDAO.findAll()) {
            if (conference.getStartConference().compareTo(startDate) >= 0 && conference.getEndConference().compareTo(endDate) <= 0) {
                filteredConferences.add(conference);
            }
        }
        return filteredConferences;
    }

    public List<Conference> findAllByConferenceRoomNumber(Long numberConferenceRoom) throws SQLException {
        return conferenceDAO.findAll().stream()
                .filter(x -> x.getNumberConferenceRoom().equals(numberConferenceRoom))
                .collect(Collectors.toList());
    }
}
