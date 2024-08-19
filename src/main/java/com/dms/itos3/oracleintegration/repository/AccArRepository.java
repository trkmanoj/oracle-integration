package com.dms.itos3.oracleintegration.repository;

import com.dms.itos3.oracleintegration.entity.AccAR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccArRepository extends JpaRepository<AccAR,Long> {

    @Modifying
    @Transactional
    @Query("update AccAR a set a.printed = :printed, a.printedDateTime = :printedDateTime where a.headerId = :headerId")
    void updateAccArDetails(@Param(value = "printed") boolean printed, @Param(value = "printedDateTime") LocalDateTime printedDateTime, @Param(value = "headerId") Long headerId);

    @Modifying
    @Transactional
    @Query("update AccAR a set a.remarks = :remarks where a.headerId = :headerId")
    void updateAccArRemarks(@Param(value = "remarks") String remarks, @Param(value = "headerId") Long headerId);

    List<AccAR> findByPrinted(boolean b);

    AccAR findTopByOrderByBatchIdDesc();

    @Query(value = "SELECT * FROM acc_ar where remarks != ?1", nativeQuery = true)
    List<AccAR> findAllMissingArs(String remarks);
}
