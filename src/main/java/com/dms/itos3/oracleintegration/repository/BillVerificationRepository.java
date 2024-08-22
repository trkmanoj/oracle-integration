package com.dms.itos3.oracleintegration.repository;

import com.dms.itos3.oracleintegration.entity.BillVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface BillVerificationRepository extends JpaRepository<BillVerification, UUID> {

    @Query(value = "SELECT * FROM cost_bill_verification cbv where verified =1 and apStatus =1",nativeQuery = true)
    List<BillVerification> findApData();

    @Query(value = "select (cbv.totalAmountWithTax - cbvt.taxAmount) as supplierBillAmount\n" +
            "from cost_bill_verification cbv\n" +
            "left join cost_bill_verification_taxes cbvt on cbvt.billId = cbv.billId\n" +
            "where cbvt.taxCode='VAT18' and cbv.tourId = :tourId",nativeQuery = true)
    Double findSupplierBillAmount(@Param("tourId") String tourId);

    @Query(value = "select cbvt.taxAmount\n" +
            "from cost_bill_verification cbv\n" +
            "left join cost_bill_verification_taxes cbvt on cbvt.billId = cbv.billId\n" +
            "where cbvt.taxCode='VAT18' and cbv.tourId = :tourId",nativeQuery = true)
    Double findVatAmount(@Param("tourId") String tourId);
@Query(value = "SELECT cbvt.taxCodeId\n" +
        "FROM cost_bill_verification cbv\n" +
        "LEFT JOIN cost_bill_verification_taxes cbvt \n" +
        "    ON cbv.billId = cbvt.billId\n" +
        "WHERE cbvt.billId = :billId\n" +
        "  AND cbvt.taxCode = 'VAT18'",nativeQuery = true )
    String findTaxCode(@Param("billId") String billId);

    @Modifying
    @Transactional
    @Query("UPDATE BillVerification i SET i.ApStatus = :apStatus, i.apDate = :apDate WHERE i.billId IN :ids")
    void updateBillVerification(@Param("apStatus") boolean apStatus, @Param("apDate") Date apDate, @Param("ids") List<UUID> ids);
}

//    @Query(value = "SELECT * FROM programme_miscellaneous pa WHERE pa.popUpType = ?1 AND pa.miscellaneousType = ?2 AND pa.detail_id =?3 ",nativeQuery = true)
//    List<ProgramMiscellaneous> findTypeAndMiscellaneousTypeAndDetailId(String type, String miscellaneousType, String detailsId);