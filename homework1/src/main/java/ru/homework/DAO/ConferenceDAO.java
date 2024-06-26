package ru.homework.DAO;

import ru.homework.DTO.Conference;

import java.util.ArrayList;
import java.util.List;

public class ConferenceDAO {

    private List<Conference> conferences = new ArrayList<>();

    public void add(Conference conference) {
        conferences.add(conference);
    }

    public List<Conference> findAll() {
        return conferences;
    }

    public Conference findById(Long id) {
        return conferences.stream()
                .filter(x -> x.getConferenceId().equals(id))
                .findFirst()
                .get();
    }

    public void update(Conference conference, Long id) {
        conferences.set(
                conferences.indexOf(findById(id)),
                conference
        );
    }

    public void remove(Long id) {
        conferences.remove(findById(id));
    }

    public void remove(Conference conference) {
        conferences.remove(conference);
    }

    public void removeAll() {
        conferences.clear();
    }


}
