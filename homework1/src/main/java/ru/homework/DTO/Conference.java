package ru.homework.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Conference class represents a conference with a unique ID, title, start and end date,
 * author username, and conference room number. It provides constructors, getters, setters,
 * and a toString method to display conference details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conference {

    /**
     * The unique identifier for the conference.
     */
    private Long conferenceId;

    /**
     * The title of the conference.
     */
    private String conferenceTitle;

    /**
     * The start date and time of the conference.
     */
    private Date startConference;

    /**
     * The end date and time of the conference.
     */
    private Date endConference;

    /**
     * The username of the author of the conference.
     */
    private String authorUsername;

    /**
     * The number of the conference room.
     */
    private Long numberConferenceRoom;

    /**
     * Returns a string representation of the conference with formatted date and time.
     *
     * @return A string representation of the conference.
     */
    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        return new StringBuilder("{")
                .append("ID: ").append(conferenceId).append(", ")
                .append("Title: ").append(conferenceTitle).append(", ")
                .append("Start conference: ").append(formatter.format(startConference)).append(", ")
                .append("End conference: ").append(formatter.format(endConference)).append(", ")
                .append("Username: ").append(authorUsername).append(", ")
                .append("Conference room number: ").append(numberConferenceRoom)
                .append("}")
                .toString();
    }
}
