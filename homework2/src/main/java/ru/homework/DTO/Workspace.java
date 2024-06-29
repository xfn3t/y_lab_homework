package ru.homework.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * The Workspace class represents a workspace with a unique ID, title, start reservation date, and end reservation date.
 * This class provides getters and setters for all fields, a no-argument constructor, and an all-argument constructor.
 * It also overrides the toString method to provide a formatted string representation of the workspace.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Workspace {

    /**
     * The unique identifier for the workspace.
     */
    private Long workspaceId;

    /**
     * The title of the workspace.
     */
    private String title;

    /**
     * The start date and time for the workspace reservation.
     */
    private Date startReservations;

    /**
     * The end date and time for the workspace reservation.
     */
    private Date endReservations;

    public static boolean isDateOverlapWorkspace(final Date newDate, final List<Workspace> workspaces) {
        for (Workspace workspace : workspaces) {
            if (!newDate.before(workspace.getStartReservations()) && !newDate.after(workspace.getEndReservations())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDateOverlapWorkspace(final Date newDate, final List<Workspace> workspaces, final Long workspaceId) {
        for (Workspace workspace : workspaces) {
            if (workspace.getWorkspaceId().equals(workspaceId)) continue;
            if (!newDate.before(workspace.getStartReservations()) && !newDate.after(workspace.getEndReservations())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a string representation of the workspace with formatted date and time.
     *
     * @return A string representation of the workspace.
     */
    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        return new StringBuilder("{")
                .append("Title: ").append(title).append(", ")
                .append("Start reservation: ").append(formatter.format(startReservations)).append(", ")
                .append("End reservation: ").append(formatter.format(endReservations))
                .append("}")
                .toString();
    }
}
