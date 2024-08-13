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
@Table(name = "acc_operator")
public class AccOperator {
    private String agentId;    // operator.agentId
    private String accLink1;
    private String accLink2;
}
