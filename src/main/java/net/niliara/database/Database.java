package net.niliara.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Service;

import net.niliara.dto.DataDirectory;

@Service
public class Database {
    private final DataDirectory dataDirectory;
    private Connection connection;

    public Database(DataDirectory dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void connect() throws SQLException {
        String url = "jdbc:sqlite:" + dataDirectory.dataDirectory();
        this.connection = DriverManager.getConnection(url, null, null);
    }

    @PreDestroy
    public void close() throws SQLException {
        if (this.connection != null) {
            this.connection.close();
        }
    }

    public void migrate() throws SQLException {
        String sql = """
                create table if not exists users (
                    id integer primary key autoincrement,
                    username text not null unique,
                    password text not null
                )
                """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    public void createUser() throws SQLException {
        createUser("user", "1234");
    }

    public void createUser(String username, String password) throws SQLException {
        String sql = "insert into users (username, password) values (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
        }
    }

    public void removeUser(String username) throws SQLException {
        String sql = "delete from users where username = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }
    }

    public Optional<String> getUserPassword(String username) throws SQLException {
        String sql = "select password from users where username = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(result.getString("password"));
                }

                return Optional.empty();
            }
        }
    }
}
