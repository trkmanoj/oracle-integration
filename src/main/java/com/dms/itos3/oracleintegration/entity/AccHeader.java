package com.dms.itos3.oracleintegration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "acc_header")
public class AccHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acc_ar_sequence")
    @SequenceGenerator(
            name = "acc_ar_sequence",
            sequenceName = "acc_arr_seq",
            allocationSize = 1,    // Increment by 1
            initialValue = 1000000    // Start at 1000000
    )
    private Long headerId;
    private LocalDateTime transDate;
    private String type;
    private String batchId;
    private int recordCount;
    private String subCategory1;
    private double subCategory1Total;
    private String subCategory2;
    private double subCategory2Total;
    private String subCategory3;
    private double subCategory3Total;

}
