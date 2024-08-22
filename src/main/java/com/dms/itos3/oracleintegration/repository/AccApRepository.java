package com.dms.itos3.oracleintegration.repository;

import com.dms.itos3.oracleintegration.entity.AccAP;
import com.dms.itos3.oracleintegration.entity.AccAR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccApRepository extends JpaRepository<AccAP,Long> {
    List<AccAP> findByPrinted(boolean b);

    @Modifying
    @Transactional
    @Query("update AccAP a set a.printed = :printed, a.printedDateTime = :printedDateTime where a.headerId = :headerId")
    void updateAccApDetails(@Param(value = "printed") boolean printed, @Param(value = "printedDateTime") LocalDateTime printedDateTime, @Param(value = "headerId") Long headerId);

    @Modifying
    @Transactional
    @Query("update AccAP a set a.remarks = :remarks where a.headerId = :headerId")
    void updateAccApRemarks(@Param(value = "remarks") String remarks, @Param(value = "headerId") Long headerId);

    AccAP findTopByOrderByBatchId();

}
