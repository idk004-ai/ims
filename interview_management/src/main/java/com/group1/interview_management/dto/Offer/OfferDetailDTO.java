package com.group1.interview_management.dto.Offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OfferDetailDTO {
    Integer InterviewId;
    String candidateName;
    String contractType;
    String position;
    String level;
    String approver;
    String department;
    String interviewinfo;
    String recruiter;
    LocalDate periodfrom;
    LocalDate periodto;
    LocalDate duedate;
    String note;
    Double salary;
    String OfferNote;
    int status;
    LocalDateTime createDate;
    LocalDateTime modifiedDate;
    String modifiedBy;

    public String getFormattedCreateDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return createDate != null ? createDate.format(formatter) : "";
    }

    public String getFormattedModifiedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return modifiedDate != null ? modifiedDate.format(formatter) : "";
    }
}