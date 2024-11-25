package com.group1.interview_management.dto.candidate;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDetailDTO {
    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private String currentPosition;
    private String ownerHR;
    private String status;
    private String address;
    private LocalDate dob;
    private String gender;
    private String cvLink;
    private String highestLevel;
    private String skills;
    private Integer yoe ;
    private String note;
    private String cvFileName;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;
    private String modifiedBy;
    public String getFormattedDob() {
        if (dob != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return dob.format(formatter);
        }
        return "N/A";
    }
    public String getFormattedCreateDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return createDate != null ? createDate.format(formatter) : "";
    }

    public String getFormattedModifiedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return modifiedDate != null ? modifiedDate.format(formatter) : "";
    }
}
