package com.dms.itos3.oracleintegration.repository;

import com.dms.itos3.oracleintegration.entity.BillTaxes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BillTaxesRepository extends JpaRepository<BillTaxes, UUID> {
}
