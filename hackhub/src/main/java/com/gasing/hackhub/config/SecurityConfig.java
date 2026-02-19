package com.gasing.hackhub.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private AuthenticationProvider authenticationProvider;



//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//                // Lasciamo passare liberamente chi fa login o si registra
//                .requestMatchers("/api/auth/**").permitAll()
//                // Tutti gli altri percorsi sono bloccati se non hai il Token JWT
//                .anyRequest().authenticated()
//            )
//            .sessionManagement(session -> session
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            )
//            // Colleghiamo il motore di autenticazione creato in ApplicationConfig
//            .authenticationProvider(authenticationProvider)
//            // Aggiungiamo il nostro "Controllore di Biglietti" JWT
//            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            // 1. Disabilita CSRF (altrimenti i POST falliscono sempre nei test via Postman/HTTP Client)
            .csrf(csrf -> csrf.disable())

            // 2. Permetti l'accesso a TUTTI gli endpoint senza login
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()
            );

    return http.build();
}
}