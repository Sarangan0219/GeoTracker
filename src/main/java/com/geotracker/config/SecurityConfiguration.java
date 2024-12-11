package com.geotracker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * SecurityConfiguration class for setting up security configurations in the application.
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    /**
     * Configures and returns a {@link SecurityFilterChain} that contains the security settings for the application.
     *
     * @param http an instance of {@link HttpSecurity} used for customizing the security settings.
     * @return a {@link SecurityFilterChain} containing the configured security settings.
     * @throws Exception if an error occurs while configuring the security settings.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html",
                                "/actuator/**"
                        )
                        .permitAll()
                        .requestMatchers("/api/v1/management/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/management/**").hasAnyAuthority("ADMIN_READ", "MANAGER_READ")
                        .requestMatchers(HttpMethod.POST, "/api/v1/management/**").hasAnyAuthority("ADMIN_CREATE", "MANAGER_CREATE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/management/**").hasAnyAuthority("ADMIN_UPDATE", "MANAGER_UPDATE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/management/**").hasAnyAuthority("ADMIN_DELETE", "MANAGER_DELETE")
                        .anyRequest().authenticated()
                )
                .sessionManagement((sessions) -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}