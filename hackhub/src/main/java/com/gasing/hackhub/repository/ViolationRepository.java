package com.gasing.hackhub.repository;

import com.gasing.hackhub.model.ViolationReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationRepository extends JpaRepository<ViolationReport, Long> {

    // QUERY OTTIMIZZATA:
    // Filtra direttamente nel DB per:
    // 1. Stato (Non gestita)
    // 2. Hackathon specifico (tramite la relazione reporter -> hackathon -> id)
    List<ViolationReport> findByGestitaFalseAndReporter_Hackathon_Id(Long hackathonId);
}