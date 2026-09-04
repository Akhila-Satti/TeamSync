package com.projects.teamsync.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

        private JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                .csrf(AbstractHttpConfigurer::disable)

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/api/students/register",
                                                                "/api/students/verify-email",
                                                                "/api/students/resend-otp",
                                                                "/api/students/login",
                                                                "/api/students/forgot-password",
                                                                "/api/students/verify-reset-otp",
                                                                "/api/students/reset-password",
                                                                "/error",
                                                                "/ws/**")
                                                .permitAll()

                                                .anyRequest().authenticated()

                                )
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}