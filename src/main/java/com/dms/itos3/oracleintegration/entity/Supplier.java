package com.dms.itos3.oracleintegration.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Supplier")
public class Supplier {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type="uuid-char")
    private UUID supplierId;
    private String supplierType;
    private String supplierCode;
    private String supplierName;
    private String contactNo1;
    private String contactNo2;
    private String email;
    private String contactPerson;
    private String registerNo;
    private boolean status;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;
    private double creditLimit;
    private String vatNo;
    private boolean vatApplicable;
    private String tdlNo;
    private String supplierAddress;
    private String bankDetailsId;
//    //private boolean group;
//    private String taxId;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "taxId")
    private TaxGroupAndIndTaxResponse tax;
    private String accountCode;
    private String accountName;
    private Integer creditPeriod;
    private String siteCode;//sitecode ADDED


}
