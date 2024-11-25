package com.group1.interview_management.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.AccessLevel;

import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OfferCreateDTO {
    Integer InterviewId;
    String email;
    String candidateName;
    String position;
    String level;
    String recruiter;
    String interviewNote;
    LocalDateTime createDate;
    LocalDateTime modifiedDate;
    String modifiedBy;
    String interviewinfo;

    @NotBlank(message = "{ME002}")
    String approver;
    @NotBlank(message = "{ME002}")
    String department;
    @NotBlank(message = "{ME002}")
    String contractType;
    @NotNull(message = "{ME002}")
    LocalDate periodfrom;
    @NotNull(message = "{ME002}")
    LocalDate periodto;
    @NotNull(message = "{ME002}")
    LocalDate duedate;
    @NotNull(message = "{ME002}")
    @Digits(integer = 10, fraction = 2, message = "{salary.invalid}")
    @Min(value = 0, message = "{salary.nonNegative}")
    Double salary;
    @Size(max = 500, message = "{OfferNote.size}")
    String OfferNote;

    @AssertTrue(message = "Contract 'To' date must be after 'From' date.")
    public boolean isContractToAfterFrom() {
        if(periodfrom == null){
            return true;
        }
        return periodto != null && periodfrom != null && periodto.isAfter(periodfrom);
    }

    @AssertTrue(message = "Contract 'From' date must be after the current date.")
    public boolean isContractFromAfterToday() {
        if(periodto == null){
            return true;
        }
        return periodfrom != null && periodfrom.isAfter(LocalDate.now());
    }

    @AssertTrue(message = "Due date must be within the contract period.")
    public boolean isDueDateWithinPeriod() {
        if (duedate == null || periodfrom == null || periodto == null) {
            return true;
        } else {
            return (duedate.isEqual(periodfrom) || duedate.isEqual(periodto) ||
                    (duedate.isAfter(periodfrom) && duedate.isBefore(periodto)));
        }
    }
}
