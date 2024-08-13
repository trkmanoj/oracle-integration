package com.dms.itos3.oracleintegration.repository;

import com.dms.itos3.oracleintegration.entity.AccOperator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccOperatorRepository extends JpaRepository<AccOperator, UUID> {
    AccOperator findByAgentId(String operatorId);
}
