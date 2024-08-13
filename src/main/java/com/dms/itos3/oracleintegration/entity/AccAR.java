package com.dms.itos3.oracleintegration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "acc_ar")
public class AccAR {

    private String headerId;            // VARCHAR2(50 BYTE)
    private String source;              // VARCHAR2(30 BYTE)
    private double batchId;               // NUMBER
    private String invoiceClass;        // VARCHAR2(30 BYTE)
    private String trnType;             // VARCHAR2(30 BYTE)
    private String actualCategory;      // Unknown SQL type
    private String legalEntity;         // VARCHAR2(30 BYTE)
    private LocalDate invoiceDate;      // DATE
    private LocalDate glDate;           // DATE
    private String currency;            // VARCHAR2(30 BYTE)
    private double customerCode;          // NUMBER
    private double customerSite;          // NUMBER
    private String salesPerson;         // VARCHAR2(30 BYTE)
    private double quantity;              // NUMBER
    private double invoiceAmount;         // NUMBER
    private String taxCode1;            // VARCHAR2(30 BYTE)
    private double taxAmount1;            // NUMBER
    private double taxRate1;              // NUMBER
    private String taxCode2;            // VARCHAR2(30 BYTE)
    private double taxAmount2;            // NUMBER
    private double taxRate2;              // NUMBER
    private String taxCode3;            // VARCHAR2(30 BYTE)
    private double taxAmount3;            // NUMBER
    private double taxRate3;              // NUMBER
    private String invoiceNo;           // VARCHAR2(30 BYTE)
    private String description;         // VARCHAR2(30 BYTE)
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
    private String attribute12;         // VARCHAR2(30 BYTE)
}
