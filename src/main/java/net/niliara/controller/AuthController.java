package net.niliara.controller;

import net.niliara.dto.Credentials;
import net.niliara.service.AuthService;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> logIn(@RequestBody Credentials credentials) {
        Optional<String> token = authService.logIn(credentials);
        if (token.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "invalid username or password"));
        }

        return ResponseEntity.ok(Map.of("token", token.get()));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Credentials credentials) {
        boolean success = authService.register(credentials);
        if (success == false) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Could not create new user"));
        }

        return ResponseEntity.ok()
                .body(Map.of("ok", "User created"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> removeSession() {
        return ResponseEntity.ok(Map.of("message", "session removed"));
    }
}
