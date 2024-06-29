package ru.homework;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import ru.homework.DTO.Conference;
import ru.homework.DTO.User;
import ru.homework.DTO.Workspace;
import ru.homework.connection.ConnectionManager;
import ru.homework.exceptions.EntityExistException;
import ru.homework.forms.Login;
import ru.homework.forms.Registration;
import ru.homework.service.ConferenceService;
import ru.homework.service.UserService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.text.ParseException;

import static ru.homework.Logger.infoMessage;
import static ru.homework.Logger.errorMessage;

public class App {

    private static final ConferenceService conferenceService = new ConferenceService();
    private static final UserService userService = new UserService();


    /**
     * Prompts the user to input a date and time in a specified format.
     *
     * @param title the title to be displayed in the prompt message.
     * @return the input date and time as a Date object.
     */
    private static Date inputDate(String title) {
        String pattern = "HH:mm dd.MM.yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setLenient(false);
        Date date = new Date();
        boolean correct_date = false;

        Scanner scanner = new Scanner(System.in);

        do {
            System.out.print("Enter " + title + " (" + pattern + "): ");
            try {
                date = formatter.parse(scanner.nextLine().replace(",", "."));
                correct_date = true;
            } catch (ParseException e) {
                System.out.println("Enter date by mask: " + pattern);
            }
        } while (!correct_date);

        return date;
    }

    /**
     * Prompts the user to input a start date for a conference and ensures the date
     * is not in the past and does not overlap with existing conferences.
     *
     * @param list the list of existing events.
     * @return the input start date as a Date object.
     */
    private static Date inputStart(List list) {
        Date start;
        Date today = new Date();
        ConferenceService conferenceService = new ConferenceService();

        do {
            start = inputDate("start");
            if (start.before(today)) {
                infoMessage("Conference can't be in the past");
                continue;
            }
            if (conferenceService.isDateOverlap(start, list)) {
                errorMessage("", "The conference dates overlap with an existing conference.");
            }
        } while (start.before(today) || conferenceService.isDateOverlap(start, list));

        return start;
    }

    /**
     * Prompts the user to input a title for a new conference and ensures it is
     * at least 2 characters long and not empty.
     *
     * @return the input title as a String.
     */
    private static String inputTitle() {
        Scanner scanner = new Scanner(System.in);
        String title;

        do {
            System.out.print("Enter new title: ");
            title = scanner.nextLine();
            if (title.trim().isEmpty() || title.length() < 2) {
                errorMessage("Title must be at least 2 characters long and not empty.");
            }
        } while (title.trim().isEmpty() || title.length() < 2);

        return title;
    }

    /**
     * Prompts the user to input a start date for a conference, ensuring the date
     * is not in the past and does not overlap with existing conferences, excluding
     * the specified conference.
     *
     * @param list the list of existing events.
     * @param id the ID of the event to exclude from overlap check.
     * @return the input start date as a Date object.
     */
    private static Date inputStart(List list, Long id) {
        Date start;
        Date today = new Date();
        ConferenceService conferenceService = new ConferenceService();

        do {
            start = inputDate("start");
            if (start.before(today)) {
                infoMessage("Conference can't be in the past");
                continue;
            }
            if (conferenceService.isDateOverlap(start, list, id)) {
                errorMessage("", "The conference dates overlap with an existing conference.");
            }
        } while (start.before(today) || conferenceService.isDateOverlap(start, list, id));

        return start;
    }

    /**
     * Prompts the user to input an end date for a conference and ensures the date
     * is after the start date and does not overlap with existing conferences.
     *
     * @param start the start date.
     * @param list the list of existing entity's.
     * @return the input end date as a Date object.
     */
    private static Date inputEnd(Date start, List list) {
        Date end;

        do {
            end = inputDate("end");
            if (end.before(start)) {
                errorMessage("End date must be after start date.");
                continue;
            }
            if (conferenceService.isDateOverlap(end, list)) {
                errorMessage("", "The conference dates overlap with an existing event.");
            }
        } while (end.before(start) || conferenceService.isDateOverlap(end, list));

        return end;
    }

    /**
     * Prompts the user to input an end date for a conference, ensuring the date is
     * after the start date and does not overlap with existing conferences, excluding
     * the specified conference.
     *
     * @param startConference the start date of the conference.
     * @param conferences the list of existing conferences.
     * @param conferenceId the ID of the conference to exclude from overlap check.
     * @return the input end date as a Date object.
     */
    private static Date inputEnd(Date startConference, List<Conference> conferences, Long conferenceId) {
        Date endConference;

        do {
            endConference = inputDate("end conference");
            if (endConference.before(startConference)) {
                errorMessage("End date must be after start date.");
                continue;
            }
            if (conferenceService.isDateOverlap(endConference, conferences, conferenceId)) {
                errorMessage("", "The conference dates overlap with an existing conference.");
            }
        } while (endConference.before(startConference) || conferenceService.isDateOverlap(endConference, conferences, conferenceId));

        return endConference;
    }


    /**
     * Prompts the user to input details for a new conference and ensures the conference
     * does not overlap with existing conferences.
     *
     * @param conferences the list of existing conferences.
     * @param numberConferenceRoom the room number for the conference.
     * @return an Optional containing the new conference if there are no overlaps, otherwise an empty Optional.
     */
    private static Optional<Conference> inputConference(List<Conference> conferences, User user, Long numberConferenceRoom) {
        String title;
        Date startConference;
        Date endConference;
        Date today = new Date();

        System.out.println("Today: " + today);
        title = inputTitle();
        startConference = inputStart(conferences);
        endConference = inputEnd(startConference, conferences);

        Conference newConference = new Conference(title, startConference, endConference, user, numberConferenceRoom);
        if (conferenceService.isDateOverlap(newConference, conferences)) {
            errorMessage("", "The conference dates overlap with an existing conference.");
            return Optional.empty();
        }

        return Optional.of(newConference);
    }

    /**
     * Allows the user to edit the details of an existing conference.
     *
     * @param conferenceId the ID of the conference to edit.
     * @param conferences the list of existing conferences.
     * @return the edited conference.
     */
    private static Conference editConference(final Long conferenceId, final List<Conference> conferences) {

        clearTerminal();

        Conference conference = conferences.stream()
                .filter(x -> x.getConferenceId().equals(conferenceId))
                .findFirst()
                .get();

        Long numberConferenceRoom = conference.getNumberConferenceRoom();

        String title = conference.getConferenceTitle();
        Date startConference = conference.getStartConference();
        Date endConference = conference.getEndConference();

        String sepr = new String(new char[5]).replace("\0", "-");
        System.out.println(sepr + "Conference for edit" + sepr);

        Scanner scanner = new Scanner(System.in);
        String command;

        do {
            printConferences(Collections.singletonList(conference));

            printChoices(
                    "Edit title",
                    "Edit start time",
                    "Edit end time",
                    "Save and exit"
            );
            System.out.print("Enter command: ");
            command = scanner.nextLine();

            switch (command) {
                case "1" -> conference.setConferenceTitle(inputTitle());
                case "2" -> startConference = inputStart(conferences, conferenceId);
                case "3" -> conference.setEndConference(inputEnd(startConference, conferences, conferenceId));
                case "4" -> {
                    return conference;
                }

                case "0" -> {
                    System.out.println("If you exit then you changes can't saved");
                    System.out.print("Ok? (y/N): ");
                    if (scanner.nextLine().equalsIgnoreCase("y"))
                        return conference;
                }
            }
        } while (!command.equals("0"));

        return new Conference(conferenceId, title, startConference, endConference, conference.getAuthor(), numberConferenceRoom);
    }

    /**
     * Prints the list of available conference rooms.
     */
    private static void printConferencesRoomList() {
        printChoices(
                "Conference Room 1",
                "Conference Room 2",
                "Conference Room 3"
        );
    }

    /**
     * Prints the list of conferences in a formatted table.
     *
     * @param conferences the list of conferences to print.
     */
    private static void printConferences(final List<Conference> conferences) {
        clearTerminal();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd.MM.yyyy");

        System.out.println("Conferences:");
        if (conferences.isEmpty()) {
            System.out.println("You have not created any conferences yet");
            return;
        }

        String format = "%-5s%-30s%-25s%-25s%-20s%-20s%n";
        System.out.printf(format, "ID", "Title", "Start", "End", "Author", "Conference room");
        System.out.println(new String(new char[120]).replace("\0", "-"));

        for (Conference conference : conferences) {
            System.out.printf(format,
                    conference.getConferenceId() + " ",
                    conference.getConferenceTitle(),
                    formatter.format(conference.getStartConference()),
                    formatter.format(conference.getEndConference()),
                    conference.getAuthor().getUsername(),
                    conference.getNumberConferenceRoom()
            );
        }
    }

    /**
     * Prints the details of the given list of workspaces to the terminal.
     * The workspace details include the ID, title, start reservation time, and end reservation time.
     * If the list is empty, it prints a message indicating that all seats are available.
     * The date and time are formatted using the pattern "HH:mm dd.MM.yyyy".
     *
     * @param workspaces A list of Workspace objects to be printed. If the list is empty,
     *                   a message indicating that all seats are available is printed.
     */
    private static void printWorkspaces(final List<Workspace> workspaces) {
        clearTerminal();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd.MM.yyyy");

        System.out.println("Workspaces:");
        if (workspaces.isEmpty()) {
            System.out.println("All seats are available");
            return;
        }

        String format = "%-5s%-30s%-25s%-25s%n";
        System.out.printf(format, "ID", "Title", "Start Reservation", "End Reservation");
        System.out.println(new String(new char[90]).replace("\0", "-"));

        for (Workspace workspace : workspaces) {
            System.out.printf(format,
                    workspace.getWorkspaceId() + " ",
                    workspace.getTitle(),
                    formatter.format(workspace.getStartReservations()),
                    formatter.format(workspace.getEndReservations())
            );
        }
    }

    /**
     * Prints a list of choices with numbered options.
     *
     * @param choices the choices to print.
     */
    private static void printChoices(final String... choices) {
        int i = 0;
        for (String choice : choices)
            System.out.println(++i + ". " + choice);
        System.out.println("0. Exit");
    }


    /**
     * Finds the index of a workspace in the list by its ID.
     * If the given ID is greater than the ID of the last workspace in the list,
     * an IndexOutOfBoundsException is thrown.
     * If a workspace with the given ID is found, its index is returned.
     * If no such workspace is found, -1 is returned.
     *
     * @param workspaces A list of Workspace objects to search through.
     * @param id The ID of the workspace to find.
     * @return The index of the workspace with the given ID, or -1 if not found.
     * @throws IndexOutOfBoundsException if the given ID is greater than the ID of the last workspace in the list.
     */
    private static Long findIndexByIdWorkspace(List<Workspace> workspaces, Long id) {
        if (id > workspaces.get(workspaces.size() - 1).getWorkspaceId()) throw new IndexOutOfBoundsException();
        Long i = 0L;
        for (Workspace workspace : workspaces) {
            if (workspace.getWorkspaceId().equals(id))
                return i;
            ++i;
        }
        return -1L;
    }

    /**
     * Authenticates a user by prompting for login or registration.
     *
     * @return an Optional containing the authenticated user, or an empty Optional if authentication fails.
     */
    private static Optional<User> authUser() throws SQLException {

        Scanner scanner = new Scanner(System.in);
        Optional<User> user;
        String command;

        while (true) {
            printChoices(
                    "Login",
                    "Registration"
            );
            System.out.print("Enter command: ");

            command = scanner.nextLine();

            switch (command) {
                case "1" -> {
                    user = Login.loginUser(userService.findAll());
                    if (user.isEmpty())
                        System.out.println("User not found\nTry another login or password");
                    else
                        return user;
                }

                case "2" -> {
                    user = new Registration().registrationNewUser(userService);
                    if (user.isPresent())
                        return user;
                }

                case "0" -> {
                    return Optional.empty();
                }
                default -> errorMessage("Unexpected value: " + command, "");
            }
        }
    }

    /**
     * Clears the terminal screen.
     */
    private static void clearTerminal() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Prompts the user to input details for a new workspace and creates a new Workspace object.
     * The title, start reservation date, and end reservation date are input by the user.
     * The new workspace is assigned an ID that is one greater than the size of the given list of workspaces.
     *
     * @param workspaces The list of existing workspaces to determine the ID of the new workspace.
     * @return A new Workspace object with the input details.
     */
    private static Workspace inputWorkspace(List<Workspace> workspaces) {
        String title = inputTitle();
        Date startReservation = inputStart(workspaces);
        Date endReservation = inputEnd(startReservation, workspaces);
        return new Workspace((long) (workspaces.size() + 1), title, startReservation, endReservation);
    }


    public static void main(String[] args) throws SQLException {


        try(Connection connection = ConnectionManager.getConnection()) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db/changelog/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.clearCheckSums();
            liquibase.update();
        } catch (SQLException | LiquibaseException e) {
            System.out.println(e.getMessage());
        }


        List<Workspace> workspaces = new ArrayList<>();
        List<Conference> conferences = new ArrayList<>();

        String command;
        boolean success = false;
        Long numberConferenceRoom;

        Optional<User> mainUser = Optional.empty();
        Scanner scanner = new Scanner(System.in);

        while (true) {

            try {
                mainUser = authUser();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            if (mainUser.isEmpty()) break;

            clearTerminal();
            try {
                conferences = conferenceService.findAll();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            printConferences(conferences);

            success = false;
            numberConferenceRoom = 0L;


            printChoices(
                    "Workspace",
                    "Conference room"
            );
            System.out.print("Enter command: ");
            String workConferenceChoice = scanner.nextLine();

            if (workConferenceChoice.equals("1")) {

                while(true) {

                    printWorkspaces(workspaces);
                    printChoices(
                            "View all workspaces",
                            "Reservation workspace",
                            "Cancel Reservation"
                    );
                    System.out.print("Enter command: ");
                    command = scanner.nextLine();

                    if(command.equals("0")) break;

                    switch (command) {
                        case "1" -> printWorkspaces(workspaces);
                        case "2" -> workspaces.add(inputWorkspace(workspaces));
                        case "3" -> {
                            System.out.print("Enter workspace number: ");
                            try {
                                Long id = scanner.nextLong();
                                workspaces.remove(findIndexByIdWorkspace(workspaces, id).intValue());
                                mainUser.get().setUserWorkspace(null);
                            } catch (InputMismatchException | IndexOutOfBoundsException e) {
                                errorMessage("Enter correct id: " + e.getMessage());
                            }
                        }
                        default -> errorMessage("Unexpected value: " + command, "");
                    }
                }

            } else if (workConferenceChoice.equals("2")) {
                do {
                    printConferencesRoomList();

                    System.out.print("Enter number conference room: ");
                    String StringNumberConferenceRoom = scanner.nextLine();

                    if (StringNumberConferenceRoom.equals("0")) break;

                    switch (StringNumberConferenceRoom) {
                        case "1" -> numberConferenceRoom = 1L;
                        case "2" -> numberConferenceRoom = 2L;
                        case "3" -> numberConferenceRoom = 3L;
                        default -> errorMessage("Unexpected value: " + StringNumberConferenceRoom, "");
                    }
                    try {
                        numberConferenceRoom = Long.parseLong(StringNumberConferenceRoom);
                    } catch (Exception e) {
                        errorMessage(e.getMessage());
                    }
                } while (numberConferenceRoom < 1 || numberConferenceRoom > 3);

                if (numberConferenceRoom.equals(0L)) continue;
                printConferences(conferences);

                if (mainUser.get().getUsername().equals("admin")) {
                    mainUser.get().setUserConferences(conferenceService.findAll());
                }

                do {
                    printChoices(
                            "View all conferences",
                            "View all conferences this room",
                            "View my conferences",
                            "Registration new conference",
                            "Edit conference",
                            "Delete conference",
                            "Found by date interval"
                    );
                    System.out.print("Enter command: ");
                    command = scanner.nextLine();

                    switch (command) {

                        case "1" -> printConferences(conferenceService.findAll());
                        case "2" -> printConferences(conferenceService.findAllByConferenceRoomNumber(numberConferenceRoom));
                        case "3" -> printConferences(conferenceService.findAllByUserId(mainUser.get().getUserId()));
                        case "4" -> {
                            Optional<Conference> conference = inputConference(
                                    conferences,
                                    mainUser.get(),
                                    numberConferenceRoom
                            );

                            if (conference.isPresent()) {
                                conferences.add(conference.get());
                                conferenceService.add(conference.get());
                            }
                        }

                        case "5" -> {

                            Conference editedConference;
                            printConferences(conferenceService.findAllByUserId(mainUser.get().getUserId()));
                            System.out.print("Enter number conference: ");

                            try {
                                Long id = scanner.nextLong();

                                editedConference = editConference(id, conferenceService.findAllByUserId(mainUser.get().getUserId()));
                                conferenceService.update(editedConference, id);

                                infoMessage("Success edit");

                            } catch (InputMismatchException | IndexOutOfBoundsException e) {
                                errorMessage("Enter correct id");
                            } catch (Exception e) {
                                errorMessage(e.getMessage());
                            }
                        }

                        case "6" -> {
                            printConferences(conferenceService.findAllByUserId(mainUser.get().getUserId()));
                            System.out.print("Enter conference number: ");
                            try {
                                conferenceService.remove(scanner.nextLong());
                            } catch (InputMismatchException | IndexOutOfBoundsException | SQLException e) {
                                errorMessage("Enter correct id: " + e.getMessage());
                            }
                        }

                        case "7" -> {
                            System.out.println("For filter enter dates:");
                            Date startDate = inputDate("start");
                            Date endDate = inputDate("end");

                            printConferences(conferenceService.filterConferencesByDateRange(startDate, endDate));
                        }

                        case "0" -> success = true;
                        default -> errorMessage("Unexpected value: " + command, "");
                    }
                } while (!success);

            } else if (workConferenceChoice.equals("0")) {
                break;
            } else {
                errorMessage("Unexpected value: " + workConferenceChoice, "");
            }
        }
    }
}
