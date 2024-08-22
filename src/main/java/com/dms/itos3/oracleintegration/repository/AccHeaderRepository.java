package com.dms.itos3.oracleintegration.repository;

import com.dms.itos3.oracleintegration.entity.AccHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface AccHeaderRepository extends JpaRepository<AccHeader, UUID> {

    @Modifying
    @Transactional
    @Query("update AccHeader a set a.printed = :printed, a.printedDateTime = :printedDateTime where a.headerId = :headerId")
    void updateAccHeaderDetails(@Param(value = "printed") boolean printed, @Param(value = "printedDateTime") LocalDateTime printedDateTime, @Param(value = "headerId") Long headerId);

    @Query(value = "SELECT * FROM acc_header ORDER BY headerId DESC LIMIT 2", nativeQuery = true)
    List<AccHeader> findLast2Records();
}
