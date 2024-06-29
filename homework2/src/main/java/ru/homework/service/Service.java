package ru.homework.service;

import ru.homework.DTO.Conference;
import ru.homework.exceptions.EntityExistException;

import java.sql.SQLException;
import java.util.List;

public interface Service<T> {

    void add(T t) throws EntityExistException, SQLException;
    List<T> findAll() throws SQLException;
    T findById(Long id) throws SQLException;
    void update(T t, Long id) throws SQLException;
    void remove(T t) throws SQLException;
    void remove(Long id) throws SQLException;
    boolean exist(Long id) throws SQLException;
    boolean exist(T t) throws SQLException;

}
