package com.dms.itos3.oracleintegration.repository;

import com.dms.itos3.oracleintegration.entity.InvoiceVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceVerificationRepository extends JpaRepository<InvoiceVerification, UUID> {

    List<InvoiceVerification> findByArStatus(boolean arStatus);

    @Modifying
    @Transactional
    @Query("UPDATE InvoiceVerification i SET i.arStatus = :arStatus, i.arDate = :arDate WHERE i.id IN :ids")
    void updateInvoiceVerification(@Param("arStatus") boolean arStatus, @Param("arDate") Date arDate, @Param("ids") List<UUID> ids);

}
