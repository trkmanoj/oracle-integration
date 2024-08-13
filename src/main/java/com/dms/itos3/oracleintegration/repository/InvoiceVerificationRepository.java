package com.dms.itos3.oracleintegration.repository;

import com.dms.itos3.oracleintegration.entity.InvoiceVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceVerificationRepository extends JpaRepository<InvoiceVerification, UUID> {
    List<InvoiceVerification> findByTourHeaderID(String tourId);

    List<InvoiceVerification> findByArStatus(boolean arStatus);

}
