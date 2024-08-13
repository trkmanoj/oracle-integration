package com.dms.itos3.oracleintegration.repository;

import com.dms.itos3.oracleintegration.entity.AccAR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccArRepository extends JpaRepository<AccAR,Long> {
}
