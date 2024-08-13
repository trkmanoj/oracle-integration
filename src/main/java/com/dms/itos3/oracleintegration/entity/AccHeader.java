package com.dms.itos3.oracleintegration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "acc_header")
public class AccHeader {
    /*private String tourTypeId;
    private String tourType;
    private String tourTypeName;
    private String segmentValue;
    private String accLinkInv;
    private String accLinkCm;
    private String distributionSetId;*/
}
