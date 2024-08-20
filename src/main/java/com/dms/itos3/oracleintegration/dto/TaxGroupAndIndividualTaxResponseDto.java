package com.dms.itos3.oracleintegration.dto;

import lombok.*;

import java.util.HashMap;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaxGroupAndIndividualTaxResponseDto {

        private UUID id;
        private String code;
        private String description;
        private String type;
        private float tax;
        private HashMap<Integer,Float> taxOrder;

}
