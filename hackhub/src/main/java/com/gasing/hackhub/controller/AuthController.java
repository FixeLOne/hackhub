package com.gasing.hackhub.controller;

import com.gasing.hackhub.dto.auth.request.LoginRequest;
import com.gasing.hackhub.dto.auth.request.RegisterRequest;
import com.gasing.hackhub.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Metodo per la registrazione
    @PostMapping("/register")  // ResponseEntity<?> serve per restituire oggetto + codice status (es. 200) <?> o <Object> indica che non sappiamo cosa torna(UserResponse o String)
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {  // @RequestBody serve a convertire il JSON in oggetto RegisterRequest
        try {
            // Se va tutto bene restituisco codice 200 OK e l'utente creato
            return ResponseEntity.ok(authService.register(request));
        } catch (RuntimeException e) {
            // Se c'è un errore (tipo email duplicata) restituisco 400 Bad Request e il messaggio
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Metodo per il login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Se la password è giusta restituisco 200 OK e i dati utente
            return ResponseEntity.ok(authService.login(request));
        } catch (RuntimeException e) {
            // Se la password è sbagliata o l'utente non esiste restituisco 400 Bad Request
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}