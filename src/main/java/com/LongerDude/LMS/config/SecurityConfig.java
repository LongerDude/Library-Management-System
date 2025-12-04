package com.LongerDude.LMS.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for Spring Security.
 * This sets up the security filters, authentication providers, and authorization rules
 * for the Library Management System (LMS) application.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enables security on specific methods (e.g., using @PreAuthorize)
public class SecurityConfig {

    /**
     * Defines the security filter chain that configures HTTP security.
     *
     * @param http The HttpSecurity object to configure.
     * @return The built SecurityFilterChain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) ->
                        // Require authentication for all incoming requests
                        authorize.anyRequest().authenticated())
                // Enable HTTP Basic authentication (username/password in the header)
                .httpBasic(Customizer.withDefaults())
                // Disable Cross-Site Request Forgery (CSRF) protection for simpler API testing
                // NOTE: This should be enabled in a production environment with session-based authentication.
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    /**
     * Defines an in-memory user details service for demonstration and testing purposes.
     * In a real-world application, this would typically connect to a database.
     *
     * @param passwordEncoder The PasswordEncoder bean used to secure the user's password.
     * @return An implementation of UserDetailsService.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // Create a default user with username "user" and password "password" and role "USER"
        UserDetails userDetails = User.builder()
                .username("user")
                // Encode the password before storing it for secure comparison
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();
        // Use InMemoryUserDetailsManager to manage the user in memory
        return new InMemoryUserDetailsManager(userDetails);
    }

    /**
     * Defines the PasswordEncoder bean, which is crucial for secure password storage.
     * BCrypt is a widely recommended, strong hashing algorithm.
     *
     * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}