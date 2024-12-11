package com.geotracker.config;


import com.geotracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * ApplicationConfig class for configuring the authentication beans in the application.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository repository;

    /**
     * Provides an instance of {@link UserDetailsService} to retrieve user-related data.
     *
     * @return a {@link UserDetailsService} implementation for retrieving user details.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Configures and returns an {@link AuthenticationProvider} that uses a DAO-based authentication mechanism.
     *
     * @return a {@link DaoAuthenticationProvider} containing the configured authentication settings.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configures and returns an {@link AuthenticationManager} to authenticate requests in the application.
     *
     * @param config an {@link AuthenticationConfiguration} instance used to retrieve the authentication manager.
     * @return an {@link AuthenticationManager} containing the configured authentication settings.
     * @throws Exception if an error occurs while retrieving the authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Provides an instance of {@link PasswordEncoder} to encode and validate passwords in the application.
     *
     * @return a {@link BCryptPasswordEncoder} implementation for encoding and validating passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

