package com.dms.itos3.oracleintegration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cost_invoice_verification")
public class InvoiceVerification {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    private UUID id;
    private String tourHeaderID;
    private String invoiceId;
    private String internalID;
    private String invoiceNo;
    private String type;
    private String date;
    private String operator;
    private String operatorId;
    private String marketId;
    private String currency;
    private Double exRate;
    private Double tot;
    private double invoiceWithoutTax;
    private double invoiceTax;
    private boolean validate;
    private Date validateDate;
    private String validateBy;
    private String pullInvoiceId;
    private Date pullDate;
    private boolean toggleStatus;
    private boolean arStatus;
    private Date arDate;
}
