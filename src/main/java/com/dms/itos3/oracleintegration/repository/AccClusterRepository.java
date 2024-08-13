package com.dms.itos3.oracleintegration.repository;

import com.dms.itos3.oracleintegration.entity.AccCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccClusterRepository extends JpaRepository<AccCluster, UUID> {
}
