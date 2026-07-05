package net.niliara.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import net.niliara.database.Database;
import net.niliara.dto.Credentials;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HexFormat;
import java.util.Optional;

import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final SecretKey key = Jwts.SIG.HS256.key().build();
    private final Database database;

    public AuthService(Database database) {
        this.database = database;
    }

    public Optional<String> logIn(Credentials credentials) {
        Optional<String> storedPassword = database.getUserPassword(credentials.username());
        if (storedPassword.isEmpty()) {
            return Optional.empty();
        }

        boolean success = verifyHash(credentials.password(), storedPassword.get());
        if (success) {
            return Optional.of(createSessionToken(credentials.username()));
        }

        return Optional.empty();
    }

    public boolean register(Credentials credentials) {
        String hashedPassword = hash(credentials.password());
        if (hashedPassword.isEmpty()) {
            return false;
        }

        try {
            database.createUser(new Credentials(credentials.username(), hashedPassword));
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    public Optional<String> validateToken(String token) {
        try {
            String subject = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();

            return Optional.ofNullable(subject);
        } catch (JwtException | IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    private String createSessionToken(String username) {
        long timeNow = new Date().getTime();
        long hour = 1000 * 60 * 60;
        Date expirationDate = new Date(timeNow + 10 * hour);

        return Jwts.builder()
                .subject(username)
                .signWith(key)
                .expiration(expirationDate)
                .compact();
    }

    private String hash(String input) throws IllegalStateException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    private Boolean verifyHash(String input, String base) {
        return hash(input).equals(base);
    }
}
