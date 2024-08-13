package com.dms.itos3.oracleintegration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pull_tour_header")
public class PullTourHeaderDetails {

        @Id
        @GeneratedValue(generator = "UUID")
        @GenericGenerator(
                name = "UUID",
                strategy = "org.hibernate.id.UUIDGenerator"
        )
        @Type(type = "uuid-char")
        @Column(insertable = false, updatable = false, nullable = false)
        private UUID id;
        private String tourId;
        private String tourNo;
        private String tourName;
        private Date startDate;
        private Date endDate;
        private String tourTypeId;
        private String startActiveCostingPeriod;
        private String activeCostingPeriod;


}
