package com.dms.itos3.oracleintegration.repository;

import com.dms.itos3.oracleintegration.entity.AccCategory;
import com.dms.itos3.oracleintegration.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    Supplier findBySupplierId(UUID supplierId);
}
