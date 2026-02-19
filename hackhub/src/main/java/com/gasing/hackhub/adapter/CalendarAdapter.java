package com.gasing.hackhub.adapter;

import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * ADAPTER PATTERN - Secondo Esempio
 * Adatta l'interfaccia del nostro sistema a quella di un Calendar Esterno.
 */

@Service
public class CalendarAdapter {

    // Simula la prenotazione su un sistema esterno (es. Google Calendar API)
    public String prenotaCall(String nomeMentore, String nomeTeam, String dataOra) {

        System.out.println(">>> [CALENDAR ESTERNO] Ricevuta richiesta prenotazione.");
        System.out.println(">>> [CALENDAR ESTERNO] Host: " + nomeMentore);
        System.out.println(">>> [CALENDAR ESTERNO] Guest: Team " + nomeTeam);
        System.out.println(">>> [CALENDAR ESTERNO] Slot: " + dataOra);

        // Simuliamo la generazione di un link univoco
        String generatedLink = "https://meet.google.com/" + UUID.randomUUID().toString().substring(0, 8);

        System.out.println(">>> [CALENDAR ESTERNO] Slot confermato. Link generato: " + generatedLink);

        return generatedLink;
    }
}