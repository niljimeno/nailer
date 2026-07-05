package net.niliara.service;

import io.jsonwebtoken.Jwts;

import java.util.Date;

import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final SecretKey key = Jwts.SIG.HS256.key().build();

    public String createSessionToken(String username) {
        long timeNow = new Date().getTime();
        long hour = 1000 * 60 * 60;
        Date expirationDate = new Date(timeNow + hour);

        String token = Jwts.builder()
                .subject(username)
                .signWith(key)
                .expiration(expirationDate)
                .compact();

        return token;
    }
}
