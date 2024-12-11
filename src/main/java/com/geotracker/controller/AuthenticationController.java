package com.geotracker.controller;

import com.geotracker.model.auth.AuthenticationRequest;
import com.geotracker.model.auth.AuthenticationResponse;
import com.geotracker.model.auth.RegisterRequest;
import com.geotracker.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Handles authentication requests, including registration, login, and token refresh.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Registers a new user.
     *
     * @param request the registration request containing user information.
     * @return a response containing the JWT for the registered user.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        AuthenticationResponse authenticationResponse = authenticationService.register(request);
        if (authenticationResponse != null) {
            return ResponseEntity.ok(authenticationResponse);
        } else {
            log.warn("Registration failed for username: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Registration failed as " + request.getUsername() + " already exists");
        }
    }

    /**
     * Authenticates a user and returns a JWT.
     *
     * @param request the authentication request containing username and password.
     * @return a response containing the JWT for the authenticated user.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        log.info("Attempting to authenticate user: {}", request.getUsername());
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    /**
     * Refreshes the JWT token.
     *
     * @param request  the HTTP request containing the current JWT.
     * @param response the HTTP response where the new JWT will be attached.
     * @throws IOException if an input or output exception occurred.
     */
    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Refreshing JWT token.");
        authenticationService.refreshToken(request, response);
    }
}
