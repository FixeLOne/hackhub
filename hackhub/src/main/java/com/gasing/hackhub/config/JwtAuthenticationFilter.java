package com.gasing.hackhub.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService; // Spring usa questo per cercare l'utente nel DB

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Leggiamo l'intestazione HTTP chiamata "Authorization"
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Se non c'è l'intestazione o non inizia con "Bearer ", ignoriamo e passiamo oltre 
        // (Magari è una richiesta pubblica come il Login)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Estraiamo il token (tagliamo via la parola "Bearer " che è lunga 7 caratteri)
        jwt = authHeader.substring(7);
        
        // 4. Chiediamo al fabbro (JwtService) di leggere l'email dentro il token
        userEmail = jwtService.extractUsername(jwt);

        // 5. Se abbiamo un'email e l'utente NON è ancora loggato nel contesto di sicurezza attuale
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Peschiamo l'utente dal Database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Se il token è valido e non scaduto...
            if (jwtService.isTokenValid(jwt, userDetails)) {
                
                // ...Creiamo l'oggetto "Utente Autenticato"
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities() // Qui ci sono i ruoli (es. MENTOR, TEAM)
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // ...E lo piazziamo nel SecurityContext. Da questo momento, i @PreAuthorize funzioneranno!
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 6. Diciamo a Spring: "Ho finito i miei controlli, fai continuare la richiesta verso il Controller!"
        filterChain.doFilter(request, response);
    }
}