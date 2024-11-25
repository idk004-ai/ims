package com.group1.interview_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferDTO {
    private Integer InterviewId;
    private String candidateName;
    private String email;
    private String approver;
    private String department;
    private String notes;
    private String status;

}
