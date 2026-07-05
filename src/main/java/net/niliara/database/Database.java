package net.niliara.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Service;

import net.niliara.dto.Credentials;
import net.niliara.dto.DataDirectory;

@Service
public class Database {
    private final DataDirectory dataDirectory;
    private Connection connection;

    public Database(DataDirectory dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    @PostConstruct
    public void init() throws SQLException {
        connect();
        migrate();
    }

    public void connect() throws SQLException {
        String url = "jdbc:sqlite:" + dataDirectory.dataDirectory().resolve("my.db");
        this.connection = DriverManager.getConnection(url, null, null);
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

    @PreDestroy
    public void close() throws SQLException {
        if (this.connection != null) {
            this.connection.close();
        }
    }

    public void createUser(Credentials credentials) throws SQLException {
        String sql = "insert into users (username, password) values (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, credentials.username());
            statement.setString(2, credentials.password());
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

    public Optional<String> getUserPassword(String username) {
        String sql = "select password from users where username = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(result.getString("password"));
                }

                return Optional.empty();
            }
        } catch (SQLException exception) {
            return Optional.empty();
        }
    }
}
