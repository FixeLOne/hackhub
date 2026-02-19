package com.gasing.hackhub.service;

import com.gasing.hackhub.enums.Role;
import com.gasing.hackhub.model.*;
import com.gasing.hackhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ViolationService {

    @Autowired private ViolationRepository violationRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private StaffAssignmentRepository staffAssignmentRepository;

    // --- IL MENTORE SEGNALA UNA VIOLAZIONE ---
    @Transactional
    public ViolationReport segnalaTeam(Long mentorId, Long hackathonId, Long teamId, String motivo) {

        // Verifiche di sicurezza: Chi sta segnalando?
        StaffAssignment mentor = staffAssignmentRepository.findByHackathonIdAndUserId(hackathonId, mentorId)
                .orElseThrow(() -> new RuntimeException("Errore: Non fai parte dello staff di questo evento!"));

        if (mentor.getRole() != Role.MENTOR) {
            throw new RuntimeException("Solo i Mentori possono segnalare le violazioni!");
        }

        // Recupero il Team
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team non trovato"));

        // Creo la segnalazione
        ViolationReport report = new ViolationReport();
        report.setReporter(mentor);   // Chi ha fatto la spia
        report.setReportedTeam(team); // Chi è stato segnalato
        report.setMotivo(motivo);
        report.setGestita(false);     // Nasce "da gestire"

        return violationRepository.save(report);
    }

    // --- L'ORGANIZZATORE DECIDE (Squalifica o Perdona) ---
    @Transactional
    public void gestisciSegnalazione(Long organizerId, Long violationId, boolean confermaSqualifica) {

        // Recupero la segnalazione
        ViolationReport report = violationRepository.findById(violationId)
                .orElseThrow(() -> new RuntimeException("Segnalazione non trovata"));

        // Controllo che l'utente sia l'ORGANIZZATORE di *quell'* Hackathon
        // (Risalgo all'evento tramite il reporter della segnalazione)
        Long hackathonId = report.getReporter().getHackathon().getId();

        StaffAssignment organizer = staffAssignmentRepository.findByHackathonIdAndUserId(hackathonId, organizerId)
                .orElseThrow(() -> new RuntimeException("Non sei nello staff di questo evento"));

        if (organizer.getRole() != Role.ORGANIZER) {
            throw new RuntimeException("Solo l'Organizzatore può decidere sulle violazioni!");
        }

        // Controllo se è già stata gestita per non farlo due volte
        if (report.isGestita()) {
            throw new RuntimeException("Questa segnalazione è già stata chiusa!");
        }

        // APPLICO LA DECISIONE
        if (confermaSqualifica) {
            // Caso 1: PUGNO DURO -> Squalifica
            Team team = report.getReportedTeam();
            team.setDisqualified(true); // Imposto il flag sul Team
            teamRepository.save(team);
        }
        // FALSO ALLARME -> Non faccio nulla sul team, chiudo solo il report.

        // Chiudo la segnalazione
        report.setGestita(true);
        violationRepository.save(report);
    }

    // --- DASHBOARD (Cose da fare) ---
    public List<ViolationReport> getPendingReports(Long hackathonId, Long organizerId) {

        // Devo sempre controllare che chi fa la richiesta sia l'Organizzatore legittimo
        StaffAssignment staff = staffAssignmentRepository.findByHackathonIdAndUserId(hackathonId, organizerId)
                .orElseThrow(() -> new RuntimeException("Accesso Negato: Non fai parte dello staff."));

        if (staff.getRole() != Role.ORGANIZER) {
            throw new RuntimeException("Accesso Negato: Solo l'Organizzatore può vedere le segnalazioni.");
        }

        // il DB mi restituisce solo le righe giuste.
        return violationRepository.findByGestitaFalseAndReporter_Hackathon_Id(hackathonId);
    }
}