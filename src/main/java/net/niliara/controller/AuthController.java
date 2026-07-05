package net.niliara.controller;

import net.niliara.dto.LoginRequest;
import net.niliara.service.AuthService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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

    @PostMapping("/session")
    public ResponseEntity<String> createSession(@RequestBody LoginRequest login) {
        String token = authService.createSessionToken(login.username());

        ResponseCookie cookie = ResponseCookie.from("session", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("session created");
    }

    @DeleteMapping("/session")
    public ResponseEntity<String> removeSession() {
        ResponseCookie cookie = ResponseCookie.from("session", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("session removed");
    }
}
