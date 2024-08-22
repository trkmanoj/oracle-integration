package com.dms.itos3.oracleintegration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cost_bill_verification")
public class BillVerification {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    private UUID billId;
    private Date billDate; // need to check
    private double amountWithoutTaxes;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "billId",referencedColumnName = "billId")
    private List<BillTaxes> taxes;
    private double totalTaxAmount;
    private double totalAmountWithTax;
    private String supplierRemarks;
    private String userRemarks;
    private String typeId;
    private String tourId;
    private String type;
    private boolean verified;
    private String verifiedBy;
    private Date verifiedDate;
    private boolean  toggleStatus;
    private String supplier;
    private String crDbType;
    private boolean confirm;
    private double commission;
    private double addition;
    private String transportSupplier;
    private String supplierBillNo;
    private boolean ApStatus;// added for oracle intergration
    private Date apDate;

}
