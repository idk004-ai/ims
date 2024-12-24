package com.group1.interview_management.dto.JobDTO.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobResponse {
    String jobId;
    String title;
    String description;
    Double salaryFrom;
    Double salaryTo;
    LocalDate startDate;
    LocalDate endDate;
    String level;
    String benefits;
    String skills;
    String workingAddress;
    String status;
    Boolean deleteFlag;
}
