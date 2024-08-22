package com.dms.itos3.oracleintegration.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "acc_ap")
public class AccAP {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acc_ap_sequence")
    @SequenceGenerator(
            name = "acc_ap_sequence",
            sequenceName = "acc_arr_seq",
            allocationSize = 1,    // Increment by 1
            initialValue = 1000000    // Start at 1000000
    )
    private Long headerId;            // VARCHAR2(50 BYTE)
    private String source;              // VARCHAR2(30 BYTE)

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acc_ar_sequence")
    @SequenceGenerator(
            name = "acc_ar_sequence",
            sequenceName = "acc_arr_seq",
            allocationSize = 1,    // Increment by 1
            initialValue = 1000000    // Start at 1000000
    )
    private Long batchId;
    private UUID billId;           //post-itos invoice id====// NUMBER
    private String supplierName;        // VARCHAR2(200 BYTE)
    private String supplierCode;             // VARCHAR2(30 BYTE)
    private String siteCode;      // VARCHAR2(30 BYTE) not nuLL
    private String invoiceDate;      // DATE
    private String glDate;           // DATE
    private String actualCategory;      // VARCHAR2(50 BYTE)
    private String invoiceType;      // VARCHAR2(30 BYTE)
    private String invoiceNo;      // VARCHAR2(15 BYTE)
    private String currencyCode;            // VARCHAR2(30 BYTE)
    private double Amount;          // NUMBER
    private String taxCode1;            // VARCHAR2(30 BYTE)
    private double taxAmount1;            // NUMBER
    private double taxRate1;              // NUMBER
    private String taxCode2;            // VARCHAR2(30 BYTE)
    private double taxAmount2;            // NUMBER
    private double taxRate2;              // NUMBER
    private String taxCode3;            // VARCHAR2(30 BYTE)
    private double taxAmount3;            // NUMBER
    private double taxRate3;              // NUMBER
    private String description;           // VARCHAR2(240 BYTE)
    private String legalEntity;         // VARCHAR2(1 BYTE)
    private String destributionSetId;   //NUMBER
    private String destributionSetName;  // VARCHAR2(50 BYTE)
    private int requestedId;
    private String attribute1;          // VARCHAR2(30 BYTE)
    private String attribute2;          // VARCHAR2(30 BYTE)
    private String attribute3;          // VARCHAR2(30 BYTE)
    private String attribute4;          // VARCHAR2(30 BYTE)
    private String attribute5;          // VARCHAR2(30 BYTE)
    private String attribute6;          // VARCHAR2(30 BYTE)
    private String attribute7;          // VARCHAR2(30 BYTE)
    private String attribute8;          // VARCHAR2(30 BYTE)
    private String attribute9;          // VARCHAR2(30 BYTE)
    private String attribute10;         // VARCHAR2(30 BYTE)
    private String attribute11;         // VARCHAR2(30 BYTE)
    private String attribute12;          // VARCHAR2(30 BYTE)
    private boolean printed;
    private LocalDateTime printedDateTime;
    private String remarks;
}
