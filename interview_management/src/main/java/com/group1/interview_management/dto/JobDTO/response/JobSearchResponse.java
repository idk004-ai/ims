package com.group1.interview_management.dto.JobDTO.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobSearchResponse {
    int jobId;
    String title;
    LocalDate startDate;
    LocalDate endDate;
    String level;
    String skills;
    String status;
}
