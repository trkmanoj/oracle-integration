package com.dms.itos3.oracleintegration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
}
