package com.group1.interview_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class OfferSearchDTO extends BaseDTO {
    private String query;
    private Integer status;
    private Integer department;
    private Integer abc;
}
