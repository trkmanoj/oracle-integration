package com.dms.itos3.oracleintegration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tax_details")
public class TaxGroupAndIndTaxResponse {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    private UUID id;
    private UUID taxId;

    private String code;
    private String description;
    private String type;
    private float tax;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "tax_group_id")
    private List<CustomizedTaxGroup> taxGroup;
}
