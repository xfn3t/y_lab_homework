package ru.homework;

import ru.homework.DTO.Conference;
import ru.homework.DTO.User;
import ru.homework.DTO.Workspace;
import ru.homework.forms.Login;
import ru.homework.forms.Registration;

import java.text.SimpleDateFormat;
import java.util.*;
import java.text.ParseException;

import static ru.homework.Logger.infoMessage;
import static ru.homework.Logger.errorMessage;

public class App {

    /**
     * Prompts the user to input a date and time in a specified format.
     *
     * @param title the title to be displayed in the prompt message.
     * @return the input date and time as a Date object.
     */
    public static Date inputDate(String title) {
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
    public static Date inputStart(List list) {
        Date start;
        Date today = new Date();

        do {
            start = inputDate("start");
            if (start.before(today)) {
                infoMessage("Conference can't be in the past");
                continue;
            }
            if (isDateOverlap(start, list)) {
                errorMessage("", "The conference dates overlap with an existing conference.");
            }
        } while (start.before(today) || isDateOverlap(start, list));

        return start;
    }

    /**
     * Prompts the user to input a title for a new conference and ensures it is
     * at least 2 characters long and not empty.
     *
     * @return the input title as a String.
     */
    public static String inputTitle() {
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
    public static Date inputStart(List list, Long id) {
        Date start;
        Date today = new Date();

        do {
            start = inputDate("start");
            if (start.before(today)) {
                infoMessage("Conference can't be in the past");
                continue;
            }
            if (isDateOverlap(start, list, id)) {
                errorMessage("", "The conference dates overlap with an existing conference.");
            }
        } while (start.before(today) || isDateOverlap(start, list, id));

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
    public static Date inputEnd(Date start, List list) {
        Date end;

        do {
            end = inputDate("end");
            if (end.before(start)) {
                errorMessage("End date must be after start date.");
                continue;
            }
            if (isDateOverlap(end, list)) {
                errorMessage("", "The conference dates overlap with an existing event.");
            }
        } while (end.before(start) || isDateOverlap(end, list));

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
    public static Date inputEnd(Date startConference, List<Conference> conferences, Long conferenceId) {
        Date endConference;

        do {
            endConference = inputDate("end conference");
            if (endConference.before(startConference)) {
                errorMessage("End date must be after start date.");
                continue;
            }
            if (isDateOverlap(endConference, conferences, conferenceId)) {
                errorMessage("", "The conference dates overlap with an existing conference.");
            }
        } while (endConference.before(startConference) || isDateOverlap(endConference, conferences, conferenceId));

        return endConference;
    }


    /**
     * Prompts the user to input details for a new conference and ensures the conference
     * does not overlap with existing conferences.
     *
     * @param conferences the list of existing conferences.
     * @param username the username of the conference creator.
     * @param numberConferenceRoom the room number for the conference.
     * @param lastId the last used conference ID.
     * @return an Optional containing the new conference if there are no overlaps, otherwise an empty Optional.
     */
    public static Optional<Conference> inputConference(List<Conference> conferences, String username, Long numberConferenceRoom, Long lastId) {
        String title;
        Date startConference;
        Date endConference;
        Date today = new Date();

        System.out.println("Today: " + today);
        title = inputTitle();
        startConference = inputStart(conferences);
        endConference = inputEnd(startConference, conferences);

        Conference newConference = new Conference(lastId + 1, title, startConference, endConference, username, numberConferenceRoom);
        if (isDateOverlap(newConference, conferences)) {
            errorMessage("", "The conference dates overlap with an existing conference.");
            return Optional.empty();
        }

        return Optional.of(newConference);
    }

    /**
     * Checks if the dates of a new conference overlap with any existing conferences.
     *
     * @param newConference the new conference to check.
     * @param conferences the list of existing conferences.
     * @return true if there is an overlap, otherwise false.
     */
    public static boolean isDateOverlap(final Conference newConference, final List<Conference> conferences) {
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
    public static boolean isDateOverlap(final Date newDate, final List<Conference> conferences) {
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
    public static boolean isDateOverlap(final Date newDate, final List<Conference> conferences, final Long conferenceId) {
        for (Conference conference : conferences) {
            if (conference.getConferenceId().equals(conferenceId)) continue;
            if (!newDate.before(conference.getStartConference()) && !newDate.after(conference.getEndConference())) {
                return true;
            }
        }
        return false;
    }


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
     * Allows the user to edit the details of an existing conference.
     *
     * @param conferenceId the ID of the conference to edit.
     * @param conferences the list of existing conferences.
     * @param numberConferenceRoom the room number for the conference.
     * @return the edited conference.
     */
    public static Conference editConference(final Long conferenceId, final List<Conference> conferences, Long numberConferenceRoom) {

        clearTerminal();

        Conference conference = conferences.stream()
                .filter(x -> x.getConferenceId().equals(conferenceId))
                .findFirst()
                .orElseThrow();

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

        return new Conference(conferenceId, title, startConference, endConference, conference.getAuthorUsername(), numberConferenceRoom);
    }

    /**
     * Prints the list of available conference rooms.
     */
    public static void printConferencesRoomList() {
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
    public static void printConferences(final List<Conference> conferences) {
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
                    conference.getAuthorUsername(),
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
    public static void printWorkspaces(final List<Workspace> workspaces) {
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
    public static void printChoices(final String... choices) {
        int i = 0;
        for (String choice : choices)
            System.out.println(++i + ". " + choice);
        System.out.println("0. Exit");
    }

    /**
     * Finds the index of a conference by its ID.
     *
     * @param conferences the list of conferences to search.
     * @param id the ID of the conference to find.
     * @return the index of the conference with the specified ID, or -1 if not found.
     * @throws IndexOutOfBoundsException if the ID is greater than the highest conference ID in the list.
     */
    public static Long findIndexById(List<Conference> conferences, Long id) {
        if (id > conferences.get(conferences.size() - 1).getConferenceId()) throw new IndexOutOfBoundsException();
        Long i = 0L;
        for (Conference conference : conferences) {
            if (conference.getConferenceId().equals(id))
                return i;
            ++i;
        }
        return -1L;
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
    public static Long findIndexByIdWorkspace(List<Workspace> workspaces, Long id) {
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
     * @param users the list of users to authenticate against.
     * @return an Optional containing the authenticated user, or an empty Optional if authentication fails.
     */
    public static Optional<User> authUser(List<User> users) {
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
                    user = Login.loginUser(users);
                    if (user.isEmpty())
                        System.out.println("User not found\nTry another login or password");
                    else
                        return user;
                }

                case "2" -> {
                    user = new Registration().registrationNewUser(users);
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
    public static void clearTerminal() {
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
    public static Workspace inputWorkspace(List<Workspace> workspaces) {
        String title = inputTitle();
        Date startReservation = inputStart(workspaces);
        Date endReservation = inputEnd(startReservation, workspaces);
        return new Workspace((long) (workspaces.size() + 1), title, startReservation, endReservation);
    }

    /**
     * Filters a list of conferences to include only those that start on or after the given start date
     * and end on or before the given end date.
     *
     * @param conferences The list of conferences to filter.
     * @param startDate The start date to filter the conferences.
     * @param endDate The end date to filter the conferences.
     * @return A list of conferences that occur within the specified date range.
     */
    public static List<Conference> filterConferencesByDateRange(List<Conference> conferences, Date startDate, Date endDate) {
        List<Conference> filteredConferences = new ArrayList<>();
        for (Conference conference : conferences) {
            if (conference.getStartConference().compareTo(startDate) >= 0 && conference.getEndConference().compareTo(endDate) <= 0) {
                filteredConferences.add(conference);
            }
        }
        return filteredConferences;
    }


    public static void main(String[] args) throws ParseException {

        List<User> users = new ArrayList<>();

        List<Workspace> workspaces = new ArrayList<>();

        List<Conference> conferences = new ArrayList<>();
        List<Conference> allUsersConferences = new ArrayList<>();
        List<Conference> conferencesRoom1 = new ArrayList<>();
        List<Conference> conferencesRoom2 = new ArrayList<>();
        List<Conference> conferencesRoom3 = new ArrayList<>();



        String command;
        boolean success = false;
        Long numberConferenceRoom;

        Optional<User> mainUser = Optional.empty();
        Scanner scanner = new Scanner(System.in);

        Conference conf1 = new Conference(
                (long) 1,
                "theta",
                new SimpleDateFormat("HH:mm dd.MM.yyyy").parse("12:00 23.06.2024"),
                new SimpleDateFormat("HH:mm dd.MM.yyyy").parse("14:00 23.04.2024"),
                "qwe",
                1L
        );

        Conference conf2 = new Conference(
                (long) 2,
                "betta",
                new SimpleDateFormat("HH:mm dd.MM.yyyy").parse("12:00 23.06.2024"),
                new SimpleDateFormat("HH:mm dd.MM.yyyy").parse("13:00 23.04.2024"),
                "qwe",
                2L
        );

        users.add(new User("qwe", "123", new ArrayList<>(Arrays.asList(conf1, conf2)), null));
        conferencesRoom1.add(conf1);
        allUsersConferences.add(conf1);

        conferencesRoom2.add(conf2);
        allUsersConferences.add(conf2);
        users.add(new User("admin", "admin", conferences, null));

        while (true) {

            mainUser = authUser(users);
            if (mainUser.isEmpty()) break;

            clearTerminal();
            printConferences(allUsersConferences);

            success = false;
            numberConferenceRoom = 0L;


            printChoices(
                    "Workspace",
                    "Conference room"
            );
            System.out.print("Enter command: ");
            String workConferenceChoice = scanner.nextLine();

            if (workConferenceChoice.equals("1")) {

                do {

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
                                mainUser.get().setWorkspace(null);
                            } catch (InputMismatchException | IndexOutOfBoundsException e) {
                                errorMessage("Enter correct id: " + e.getMessage());
                            }
                        }
                        default -> errorMessage("Unexpected value: " + command, "");
                    }
                } while (!command.equals("0"));
            } else if (workConferenceChoice.equals("2")) {
                do {
                    printConferencesRoomList();

                    System.out.print("Enter number conference room: ");
                    String StringNumberConferenceRoom = scanner.nextLine();

                    if (StringNumberConferenceRoom.equals("0")) break;

                    switch (StringNumberConferenceRoom) {
                        case "1" -> conferences = conferencesRoom1;
                        case "2" -> conferences = conferencesRoom2;
                        case "3" -> conferences = conferencesRoom3;
                        default -> errorMessage("Unexpected value: " + StringNumberConferenceRoom, "");
                    }
                    try {
                        numberConferenceRoom = Long.parseLong(StringNumberConferenceRoom);
                    } catch (Exception e) {
                        errorMessage(e.getMessage());
                    }
                } while (numberConferenceRoom < 1 || numberConferenceRoom > 3);

                if (numberConferenceRoom.equals(0L)) continue;
                printConferences(allUsersConferences);

                if (mainUser.get().getUsername().equals("admin"))
                    mainUser.get().setUserConferences(allUsersConferences);

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

                        case "1" -> printConferences(allUsersConferences);
                        case "2" -> printConferences(conferences);
                        case "3" -> printConferences(mainUser.get().getUserConferences());
                        case "4" -> {
                            Optional<Conference> conference = inputConference(
                                    conferences,
                                    mainUser.get().getUsername(),
                                    numberConferenceRoom,
                                    (long) allUsersConferences.size()
                            );

                            if (conference.isPresent()) {

                                conferences.add(conference.get());
                                allUsersConferences.add(conference.get());

                                List<Conference> userConferences = new ArrayList<>(mainUser.get().getUserConferences());
                                userConferences.add(conference.get());
                                mainUser.get().setUserConferences(userConferences);
                            }
                        }

                        case "5" -> {

                            Conference editedConference;
                            printConferences(mainUser.get().getUserConferences());
                            System.out.print("Enter number conference: ");

                            try {
                                Long id = scanner.nextLong();

                                Long conferencesIndex = findIndexById(conferences, id);
                                Long allConferencesIndex = findIndexById(allUsersConferences, id);
                                Long userConferenceIndex = findIndexById(mainUser.get().getUserConferences(), id);

                                editedConference = editConference(id, conferences, numberConferenceRoom);
                                conferences.set(conferencesIndex.intValue(), editedConference);
                                allUsersConferences.set(allConferencesIndex.intValue(), editedConference);
                                mainUser.get().getUserConferences().set(userConferenceIndex.intValue(), editedConference);

                                infoMessage("Success edit");

                            } catch (InputMismatchException | IndexOutOfBoundsException e) {
                                errorMessage("Enter correct id");
                            } catch (Exception e) {
                                errorMessage(e.getMessage());
                            }
                        }

                        case "6" -> {
                            printConferences(mainUser.get().getUserConferences());
                            System.out.print("Enter conference number: ");
                            List<Conference> conferences1 = new ArrayList<>(mainUser.get().getUserConferences());
                            try {
                                Long id = scanner.nextLong();

                                if (!findIndexById(conferences, id).equals(-1L))
                                    conferences.remove(findIndexById(conferences, id).intValue());

                                allUsersConferences.remove(findIndexById(allUsersConferences, id).intValue());
                                conferences1.remove(findIndexById(conferences1, id).intValue());
                                mainUser.get().setUserConferences(conferences1);
                            } catch (InputMismatchException | IndexOutOfBoundsException e) {
                                errorMessage("Enter correct id: " + e.getMessage());
                            }
                        }

                        case "7" -> {
                            System.out.println("For filter enter dates:");
                            Date startDate = inputDate("start");
                            Date endDate = inputDate("end");

                            printConferences(filterConferencesByDateRange(allUsersConferences, startDate, endDate));
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
