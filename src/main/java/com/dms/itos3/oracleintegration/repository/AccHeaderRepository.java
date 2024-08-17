package com.dms.itos3.oracleintegration.repository;

import com.dms.itos3.oracleintegration.entity.AccHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface AccHeaderRepository extends JpaRepository<AccHeader, UUID> {
}
