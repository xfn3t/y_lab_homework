package ru.homework.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    public static Connection getConnection(String URL, String USERNAME, String PASSWORD) throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            connectionHolder.set(conn);
        }
        return conn;
    }

    public static void closeConnection() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn != null && !conn.isClosed()) {
            conn.close();
            connectionHolder.remove();
        }
    }
}
