package com.dms.itos3.oracleintegration.repository;

import com.dms.itos3.oracleintegration.entity.AccSalesPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface AccSalesPersonRepository extends JpaRepository<AccSalesPerson, UUID> {

    AccSalesPerson findByUserName(String userName);
}
