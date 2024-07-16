package ru.homework.DAO;

import java.sql.SQLException;
import java.util.List;

public interface IDAO<T> {
    void add(T t) throws SQLException;
    List<T> findAll() throws SQLException;
    T findById(Long id) throws SQLException;

    long findLastId() throws SQLException;

    void update(T t, Long id) throws SQLException;
    void remove(Long id) throws SQLException;
    void remove(T t) throws SQLException;
    void removeAll() throws SQLException;
}
