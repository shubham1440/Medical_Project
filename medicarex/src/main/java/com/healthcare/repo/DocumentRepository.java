package com.healthcare.repo;

import com.healthcare.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByPatientEmail(String email);
    List<Document> findByPatientId(Long patientId);
}