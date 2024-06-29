package ru.homework;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.*;

public class Main {
    private static final String URL = "jdbc:postgresql://localhost:5432/coworking";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Statement statement = connection.createStatement();
            Liquibase liquibase = new Liquibase("db/changelog/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.clearCheckSums();
            liquibase.update();

            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM private.t_user");
            resultSet.next();

            System.out.println("Complete " + resultSet.getInt("count"));

        } catch (SQLException | LiquibaseException e) {
            System.out.println(e.getMessage());
        }
    }
}
