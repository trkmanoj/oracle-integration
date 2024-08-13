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
@Table(name = "acc_cluster")
public class AccCluster {
    private String clusterId;
    private String clusterCode;
    private String clusterName;
    private String segmentValue;
}
