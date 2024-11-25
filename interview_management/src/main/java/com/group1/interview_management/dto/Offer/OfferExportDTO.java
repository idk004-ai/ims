package com.group1.interview_management.dto.Offer;

import com.group1.interview_management.entities.InterviewAssignment;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

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
