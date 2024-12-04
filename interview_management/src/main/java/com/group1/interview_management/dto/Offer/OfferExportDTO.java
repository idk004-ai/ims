package com.group1.interview_management.dto.Offer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OfferExportDTO {
    Integer interviewId;
    Integer candidateId;
    String candidateName;
    String approvedBy;
    String contractType;
    String position;
    String level;
    String department;
    String recruiter;
    String interviewer;
    LocalDate contractFrom;
    LocalDate contractTo;
    Double salary;
    String interviewNote;
    String offerNote;
}
